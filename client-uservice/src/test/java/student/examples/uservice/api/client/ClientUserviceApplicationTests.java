package student.examples.uservice.api.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import student.examples.uservice.api.client.dto.UserSignupRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONObject;
import org.springframework.http.MediaType;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ClientUserviceApplicationTests {
	
	@TempDir
	static File tempDir;

	private static String VALID_DATA_CSV_FILE = "C:\\Users\\User\\AppData\\Local\\Temp\\data\\validData.csv";
	private static String INVALID_DATA_CSV_FILE = "C:\\Users\\User\\AppData\\Local\\Temp\\data\\invalidData.csv";

	private static final String REPOSITORY_URL = "https://github.com/valentinignatov/testValidation.git";
	private static final String BRANCH_NAME = "main";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	private static final String SAMPLE_CSV_FILE = System.getProperty("user.dir") + "/sample.csv";
	
	@BeforeEach
	public void setUp() throws IOException {
		cloneRepository();
	}

	@AfterAll
	public static void tearDown() {
		pushToRepository();
	}
	
	private void cloneRepository() throws IOException {
		try {
			File destinationDirectory = new File("C:\\Users\\User\\AppData\\Local\\Temp\\data");
			log.info("Destination Directory: " + destinationDirectory.getAbsolutePath());

			if (destinationDirectory.exists() && destinationDirectory.list().length == 0) {
				Git.cloneRepository().setURI(REPOSITORY_URL).setDirectory(destinationDirectory).setBranch(BRANCH_NAME)
						.call();
			} else {
				log.info("Destination directory is not empty. Skipping cloning.");
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
			log.info("Error cloning repository: " + e.getMessage());
		}
	}

	private static void pushToRepository() {
		try (Git git = Git.open(new File("C:\\Users\\User\\AppData\\Local\\Temp\\data\\.git"))) {
			git.add().addFilepattern(".").call();
			git.add().addFilepattern(".").call();
			git.commit().setMessage("Test results").call();
			git.push().setCredentialsProvider(
					new UsernamePasswordCredentialsProvider("valentinignatov", "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"))
					.call();
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			log.info("Error pushing to repository: " + e.getMessage());
		}
	}
	
	@BeforeAll
	public static void init() throws IOException {

		Faker faker = new Faker();

		Files.createDirectories(Paths.get(tempDir.getAbsolutePath(), "data"));

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(VALID_DATA_CSV_FILE)));
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("UserName", "Email", "Password", "PasswordConfirmation"));) {

			for (int i = 1; i <= 10; i++) {
				String username = faker.regexify("[a-zA-Z0-9]{8,}$");
				String email = faker.internet().emailAddress();
				String generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");

				csvPrinter.printRecord(username, email, generatedPassword, generatedPassword);
			}
			csvPrinter.flush();
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(INVALID_DATA_CSV_FILE)));
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("UserName", "Email", "Password", "PasswordConfirmation"));) {

			for (int i = 1; i <= 10; i++) {
				String username = faker.lorem().characters(1, 2);
				String email = faker.lorem().word();
				String generatedPassword = faker.lorem().characters(1, 7);

				csvPrinter.printRecord(username, email, generatedPassword, generatedPassword);
			}
			csvPrinter.flush();
		}

		try (Git git = Git.open(new File("C:\\Users\\User\\AppData\\Local\\Temp\\data"))) {
			AddCommand add = git.add();
			add.addFilepattern(".").call();
		} catch (IOException | GitAPIException e) {
			e.printStackTrace();
			log.info("Error adding files to repository: " + e.getMessage());
		}

	}

	@Test
	void validateTrueDataTest() throws Exception {

		Validator validator = factory.getValidator();

		Set<jakarta.validation.ConstraintViolation<UserSignupRequest>> validation = null;

		try (Reader reader = Files.newBufferedReader(Paths.get(VALID_DATA_CSV_FILE));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
			for (CSVRecord csvRecord : csvParser) {
				String username = csvRecord.get("UserName");
				String email = csvRecord.get("Email");
				String password = csvRecord.get("Password");
				String passwordConfirmation = csvRecord.get("PasswordConfirmation");

				validation = validator.validate(new UserSignupRequest(username, email, password, passwordConfirmation));
			}
		}
		Assert.assertTrue(validation.isEmpty());
	}

	@Test
	public void signupTest() throws IllegalArgumentException, IllegalAccessException, Exception {
		JSONObject userSignupRequestJsonMock = new JSONObject();
		userSignupRequestJsonMock.put("username", "Useruser1!");
		userSignupRequestJsonMock.put("email", "valentin@mail.com");
		userSignupRequestJsonMock.put("password", "6efRYx6L2j9EnT$"); // v1QaL7fR6M$@ St@ngP@ssword1
		userSignupRequestJsonMock.put("passwordConfirmation", "6efRYx6L2j9EnT$");

		mockMvc.perform(post("/auth/signup").contentType("application/json")
				.content(userSignupRequestJsonMock.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
