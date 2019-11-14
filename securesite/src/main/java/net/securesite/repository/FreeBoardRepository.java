package net.securesite.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.securesite.domain.FreeBoard;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>{//스프링프레임웍이 구현체 생성,인스턴스 까지 만들어서 관리
	//List findAllByAll(String content);  
	//List findAllByTitle(String title);
	//List findAllByTag(String tag);
	
	List<FreeBoard> findByTitleContaining(String title); //search query
	List<FreeBoard> findByContentsContaining(String contents);//search query
	 
}
