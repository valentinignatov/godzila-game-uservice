package student.examples.business.uservice.services;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import student.examples.business.uservice.domain.entity.User;
import student.examples.business.uservice.repository.UserRepository;

@Service
public class UserMangementService {

	@Autowired
	private UserRepository userRepository;
	
	public User activationByToken(String token) {
		User userByToken = userRepository.getUserByToken(token).stream().findFirst().get();
		
		if (userByToken != null) {
			userByToken.setActive(true);
			return userRepository.save(userByToken);
		}
		
		return null;
	}
	
	public boolean deleteByToken(String token) {
		User userByToken = userRepository.getUserByToken(token).stream().findFirst().get();
		
		if (userByToken != null) {
			userRepository.delete(userByToken);
			return true;
		}
		
		return false;
	}
	
	public static String encodeToBase64(String originalString) {
        byte[] encodedBytes = Base64.getEncoder().encode(originalString.getBytes());
        return new String(encodedBytes);
    }

    public static String decodeFromBase64(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        return new String(decodedBytes);
    }

}
