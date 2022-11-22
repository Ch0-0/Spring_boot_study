package board.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import board.board.dto.BoardDto;
import board.board.dto.BoardFileDto;

@Mapper
public interface BoardMapper {
	List<BoardDto> selectBoardList() throws Exception;
	
	//게시글 등록
	void insertBoard_434(BoardDto board) throws Exception;
	
	//파일 업로드
	void insertBoardFileList(List<BoardFileDto> list) throws Exception;
	
	//조회수
	void updateHitCount(int boardIdx) throws Exception;
	
	//게시글 상세보기
	BoardDto selectBoardDetail(int boardIdx) throws Exception;
	
	//파일 상세보기
	List<BoardFileDto> selectBoardFileList(int boardIdx) throws Exception;
	
	
	//수정
	void updateBoard(BoardDto board) throws Exception;
	
	//삭제
	void deleteBoard(int boardIdx) throws Exception;

	//파일 다운로드
	BoardFileDto selectBoardFileInformation(@Param("idx") int idx, @Param("boardIdx" )int boardIdx);
}
