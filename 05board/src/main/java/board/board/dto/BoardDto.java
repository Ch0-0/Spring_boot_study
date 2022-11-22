package board.board.dto;

import java.util.List;

import lombok.Data;

//롬복의 어노테이션으로 모든 필드의 getter 와 setter를 생성하고 toString,hashcode, equals 메서드 생성
@Data
public class BoardDto {
	
	private int boardIdx;
	
	private String title;
	
	private String contents;
	
	private int hitCnt;
	
	private String creatorId;
	
	private String createdDatetime;
	
	private String updaterId;
	
	private String updatedDatetime;
	
	private List<BoardFileDto> fileList;
}