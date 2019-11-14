package net.securesite.domain;



import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;

import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
public class FreeBoardAnswer extends AbstractEntity{
	
	@ManyToOne
	@JoinColumn(foreignKey=@ForeignKey(name="fk_answer_writer"))
	private User writer;
	
	@ManyToOne
	@JoinColumn(foreignKey=@ForeignKey(name="fk_answer_to_freeboard"))
	@JsonBackReference
	private FreeBoard freeboard;
	
	@JsonProperty
	private String contents;
	

	public FreeBoardAnswer(User writer,FreeBoard freeboard,String contents) {
		this.writer=writer;
		this.freeboard=freeboard;
		this.contents=contents;
		this.setFormattedCreateDate();
		
	}
	
	

	public FreeBoardAnswer() {
		
	}
	
	public boolean isSameWriter(User loginUser) {
		//System.out.println(this.writer.getId());
		//System.out.println(loginUser.getId());
		return this.writer.matchId(loginUser.getId());
	}
	

	@Override
	public String toString() {
		return "Answer [" + super.toString() + ", writer=" + writer + ", contents=" + contents + ",]";
	}
	

	
}
