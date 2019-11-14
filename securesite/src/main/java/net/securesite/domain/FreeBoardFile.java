package net.securesite.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@Entity
@Setter //setter생성
@Getter //getter생성
public class FreeBoardFile{

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne
	@JoinColumn(foreignKey=@ForeignKey(name="fk_file_to_freeboard"))
	private FreeBoard freeboard;
	
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;
	
	
	public FreeBoardFile() {
		
	}


	public FreeBoardFile(String fileName, String fileDownloadUri, String fileType, long size,FreeBoard freeboard) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
		this.freeboard=freeboard;
	}
	
	
	public FreeBoardFile(String fileName, String fileDownloadUri, String fileType, long size) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
		
	}
	

	
}
