package net.securesite.domain;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class CategoryBoard extends AbstractEntity implements Comparable<CategoryBoard>{
	@ManyToOne
	@JoinColumn(foreignKey=@ForeignKey(name="fk_categoryboard_writer"))
	private User writer;
	
	private String title;
	
	private String contents;
	
	private String link;
	
	private int countOfAnswer = 0;
	
	
	
	@OneToMany(mappedBy="categoryboard")
	@OrderBy("id DESC")
	@JsonManagedReference
	private List<CategoryBoardAnswer> answers;
	
	
	public CategoryBoard() {}
	
	
	public CategoryBoard(User writer, String title,String link, String contents) {
		
		this.writer = writer;
		this.title = title;
		this.link=link;
		this.contents = contents;
		this.setFormattedCreateDate();
	}
	
	
	
	public void update(String title,String link,String contents) {
		this.title=title;
		this.contents=contents;
		this.link=link;
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
	public int compareTo(CategoryBoard categoryBoard) {//내림차순
		if(getId()>categoryBoard.getId()) {
			return -1;
		}else if(getId()<categoryBoard.getId()) {
			return 1;
		}
		return 0;
	}

	
}
