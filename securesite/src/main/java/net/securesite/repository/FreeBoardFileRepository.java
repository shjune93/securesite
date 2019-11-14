package net.securesite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.securesite.domain.FreeBoard;
import net.securesite.domain.FreeBoardFile;

public interface FreeBoardFileRepository  extends JpaRepository<FreeBoardFile, Long>{

}
