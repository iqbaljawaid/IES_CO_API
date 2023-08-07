package in.globalit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.globalit.entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
	
	public UserEntity findByUsername(String username);
	
	
}
