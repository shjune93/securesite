package net.securesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.securesite.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	// 아이디 조회
	//Account findById(String id);
		
	// 아이디 조회
	User findByUserId(String id);
	
}
