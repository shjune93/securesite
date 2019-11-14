package net.securesite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.securesite.domain.CategoryBoard;
import net.securesite.domain.FreeBoard;


public interface CategoryBoardRepository extends JpaRepository<CategoryBoard, Long>{

	
	
	List<CategoryBoard> findByTitleContaining(String title); //search query
	List<CategoryBoard> findByContentsContaining(String contents);//search query
	 
}
