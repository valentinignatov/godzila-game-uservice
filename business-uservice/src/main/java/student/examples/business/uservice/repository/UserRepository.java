package student.examples.business.uservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import student.examples.business.uservice.domain.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	User getDistinctByEmail(String email);
	
//	@Query("select u from Users u where u.token like %?1")
	Optional<User> getUserByToken(String token);
	
//	public void updateUserByToken(User user, String token);
	
}
