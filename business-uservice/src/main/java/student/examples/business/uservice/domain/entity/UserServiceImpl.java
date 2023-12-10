package student.examples.business.uservice.domain.entity;

import java.util.UUID;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import student.examples.business.uservice.repository.UserRepository;
import grpc.UserSignupServiceGrpc;
import grpc.UserSignupServiceOuterClass;
import grpc.UserSignupServiceOuterClass.DeleteRequest;
import grpc.UserSignupServiceOuterClass.DeleteResponse;
import grpc.UserSignupServiceOuterClass.UserSignupResponse;

@GrpcService
public class UserServiceImpl extends UserSignupServiceGrpc.UserSignupServiceImplBase {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;

	@Override
	public void signup(UserSignupServiceOuterClass.UserSignupDtoRequest request,
			StreamObserver<UserSignupServiceOuterClass.UserSignupResponse> responseObserver) {
		User userEntity = new User();
		UUID id = UUID.fromString(request.getId());
		userEntity.setId(id);
		userEntity.setUsername(request.getUserName());
		userEntity.setEmail(request.getEmail());
		userEntity.setPassword(encrypt(request.getPassword()));
		//Did not user encrypt() because when sending email the '+' is cut and the token is not the same
		userEntity.setToken(encodeToBase64(request.getId() + request.getEmail()));//uuid plus email
		userEntity.setActive(false);

		UserSignupResponse response = UserSignupResponse.newBuilder()
				.setGreeting("Hi from business uservice, " + userEntity).build();

		responseObserver.onNext(response);

		responseObserver.onCompleted();

		User saved = userRepository.save(userEntity);
		
		if (saved != null) {
			emailService.sendEmail(userRepository.getDistinctByEmail(userEntity.getEmail()));
			System.out.println("PiChaChu");
		}
	}
	
	@Override
	public void deleteUser(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
		// TODO Auto-generated method stub
		super.deleteUser(request, responseObserver);
	}
	
	public static String encodeToBase64(String originalString) {
        byte[] encodedBytes = Base64.getEncoder().encode(originalString.getBytes());
        return new String(encodedBytes);
    }

    public static String decodeFromBase64(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        return new String(decodedBytes);
    }
	
	private static SecretKey getKey() {
		String hardcodedKey = "ThisIsA16ByteKey";//hardcoded key 16 bytes long
		byte[] keyBytes = hardcodedKey.getBytes(StandardCharsets.UTF_8);
		return new SecretKeySpec(keyBytes, "AES"); //using 128 bit AES key
	}

	private static String encrypt(String plaintext) {
		SecretKey secretKey = getKey();
		Cipher cipher;
		byte[] encryptedBytes = null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	private static String decrypt(String encryptedText) {
		SecretKey secretKey = getKey();
		Cipher cipher;
		byte[] decryptedBytes=null;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
			decryptedBytes = cipher.doFinal(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}
}
