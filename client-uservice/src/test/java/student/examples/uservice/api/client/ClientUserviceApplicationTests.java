package student.examples.uservice.api.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import student.examples.uservice.api.client.dto.UserSignupRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.BufferedWriter;
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
import org.json.JSONObject;
import org.springframework.http.MediaType;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ClientUserviceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	private static final String SAMPLE_CSV_FILE = System.getProperty("user.dir") + "/sample.csv";
	
	@BeforeAll
	public static void init() throws IOException{
		System.out.println("BeforeAll init() method called");
		Faker faker = new Faker();
		
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE));
				CSVPrinter csvPrinter = new CSVPrinter(writer,
						CSVFormat.DEFAULT.withHeader("username", "email", "password", "passwordConfirmation"));) {
			
			String generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username1", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username2", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username3", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username4", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username5", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username6", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username7", faker.internet().emailAddress(), generatedPassword,generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username8", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username9", faker.internet().emailAddress(), generatedPassword, generatedPassword);
			
			generatedPassword = faker.regexify("[a-zA-Z0-9]{8,}$");
			csvPrinter.printRecord("username10", faker.internet().emailAddress(), generatedPassword, generatedPassword);

			csvPrinter.flush();
		}
		
	}

	@Test
	void validateTrueDataTest() throws Exception {
		
		Validator validator = factory.getValidator();
		
		Set<jakarta.validation.ConstraintViolation<UserSignupRequest>> validation = null;

		try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE));
				CSVParser csvParser = new CSVParser(reader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
			for (CSVRecord csvRecord : csvParser) {
				String username = csvRecord.get("username");
				String email = csvRecord.get("email");
				String password = csvRecord.get("password");
				String passwordConfirmation = csvRecord.get("passwordConfirmation");
				
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
