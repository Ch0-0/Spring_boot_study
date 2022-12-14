package board.board.service;

import java.util.List;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import board.board.dto.BoardDto;
import board.board.dto.BoardFileDto;


//서비스 영역의 인터페이스
public interface BoardService {
	
	List<BoardDto> selectBoardList() throws Exception;

	//게시글 등록 + 파일 업로드
	void insertBoard_433(BoardDto board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception;
	
//	게시글 상세보기
	BoardDto selectBoardDetail(int boardIdx) throws Exception;

//	업데이트
	void updateBoard(BoardDto board) throws Exception;

//	삭제
	void deleteBoard(int boardIdx) throws Exception;
	
	//파일 다운로드
	BoardFileDto selectBoardFileInformation(int idx, int boardIdx) throws Exception;
}
