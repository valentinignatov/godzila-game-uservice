package student.examples.business.uservice.services;

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

}
