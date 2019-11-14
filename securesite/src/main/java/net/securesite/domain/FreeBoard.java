package net.securesite.domain;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;

import lombok.Setter;



//@JsonProperty getter/setter 의 이름을 property 와 다른 이름을 사용할 수 있도록 설정한다. Database 를 자바 클래스로 매핑하는데 DB의 컬럼명이 알기 어려울 경우등에 유용하게 사용할 수 있다.

@Entity
@Setter //setter생성
@Getter //getter생성
public class FreeBoard extends AbstractEntity implements Comparable<FreeBoard>{// implements Comparable<FreeBoard> 객체 정렬을 위한 부분 overrid 부분

	
	
	@ManyToOne
	@JoinColumn(foreignKey=@ForeignKey(name="fk_freeboard_writer"))
	private User writer;
	

	private String title;
	
	private String contents;


	private int countOfAnswer = 0;
	
	
	
	@OneToMany(mappedBy="freeboard")
	@OrderBy("id DESC")
	@JsonManagedReference
	private List<FreeBoardAnswer> answers;
	
//	@OneToMany(mappedBy="freeboard")
//	@OrderBy("id DESC")
//	private List<FreeBoardFile> files;
	
	@OneToOne(mappedBy="freeboard")
	@OrderBy("id DESC")
	@JsonManagedReference
	private FreeBoardFile file;
	
	public FreeBoard() {}
	
//	public FreeBoard(User writer, String title, String contents,FreeBoardFile file) {
//		
//		this.writer = writer;
//		this.title = title;
//		this.contents = contents;
//		this.file=file;
//		this.setFormattedCreateDate();
//	
//	}
	
	public FreeBoard(User writer, String title, String contents) {
		
		this.writer = writer;
		this.title = title;
		this.contents = contents;
		this.setFormattedCreateDate();
	}
	
	
	
	public void update(String title,String contents) {
		this.title=title;
		this.contents=contents;
		
		this.setFormattedModifiedDate();
		
	}

	public boolean isSameWriter(User loginUser) {
		//System.out.println(this.writer.getId());
		//System.out.println(loginUser.getId());
		return this.writer.matchId(loginUser.getId());
	}

	public void addAnswer() {
		this.countOfAnswer+=1;
		// TODO Auto-generated method stub
		
	}

	public void deleteAnswer() {
		this.countOfAnswer-=1;
	}

	@Override
	public int compareTo(FreeBoard freeboard) {//내림차순
		if(getId()>freeboard.getId()) {
			return -1;
		}else if(getId()<freeboard.getId()) {
			return 1;
		}
		return 0;
	}
	

	
	
}
