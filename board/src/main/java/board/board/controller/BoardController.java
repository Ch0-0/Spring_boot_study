package board.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import board.board.dto.BoardDto;
import board.board.dto.BoardFileDto;
import board.board.service.BoardService;



//스프링 MVC컨트롤러
@Controller
public class BoardController {
	//로그
//	private Logger log = LoggerFactory.getLogger(this.getClass());

	//비즈니스 로직을 처리하는 서비스
	@Autowired
	private BoardService boardService;
	
	
	
	
	//웹브라우저에서 /board/openBoardList.do 를 호출하면 스프링 디스패처는 호출된 주소와 @RequestMapping 어노테이션 값이 동일한 메서드를 찾아서 실행
	@RequestMapping("/board/openBoardList.do")
	//호출된 요청의 결과를 보여주는 View
	public ModelAndView openBoardList() throws Exception{
		ModelAndView mv = new ModelAndView("/board/boardList.html");
		
	//게시글 목록을 조회
	List<BoardDto> list = boardService.selectBoardList();
	//실행된 비즈니스 로직의 결과 값을 뷰에 list라는 이름으로 저장
	mv.addObject("list", list);
	
	return mv;
	}
	
	
	
	//게시글 등록
	@RequestMapping("/board/openBoardWrite.do")
	public String openBoardWrite() throws Exception{
		return "/board/boardWrite.html";
	}
	
	
	
	
	
	//게시글 Insert + 파일 업로드
	@RequestMapping("/board/insertBoard.do")
	public String insertBoard(BoardDto board, MultipartHttpServletRequest multipartHttpServletRequest) throws Exception{
		boardService.insertBoard_433(board, multipartHttpServletRequest);
		return "redirect:/board/openBoardList.do";
	}
	
	
	
    //게시글 상세보기
	@RequestMapping("/board/openBoardDetail.do")
	public ModelAndView openBoardDetail(@RequestParam int boardIdx) throws Exception{
		ModelAndView mv = new ModelAndView("/board/boardDetail"); 
		
		BoardDto board = boardService.selectBoardDetail(boardIdx);
		mv.addObject("board",board);
		
		return mv;
	}

	//업데이트
	@RequestMapping("/board/updateBoard.do")
	public String updateBoard(BoardDto board) throws Exception{
		//System.out.println("borad내용:"+board);
		boardService.updateBoard(board);
		return "redirect:/board/openBoardList.do";
	}
	
	//삭제
	@RequestMapping("/board/deleteBoard.do")
	public String deleteBoard(int boardIdx) throws Exception{
		boardService.deleteBoard(boardIdx);
		return "redirect:/board/openBoardList.do";
	}
	
	//파일 다운로드
	@RequestMapping("/board/downloadBoardFile.do")
	public void downloadBoardFile(@RequestParam int idx, @RequestParam int boardIdx, HttpServletResponse response) throws Exception{
		BoardFileDto boardFile = boardService.selectBoardFileInformation(idx, boardIdx);
		if(ObjectUtils.isEmpty(boardFile) == false) {
			String fileName = boardFile.getOriginalFileName();
			
			byte[] files = FileUtils.readFileToByteArray(new File(boardFile.getStoredFilePath()));
			
			response.setContentType("application/octet-stream");
			response.setContentLength(files.length);
			response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(fileName,"UTF-8")+"\";");
			response.setHeader("Content-Transfer-Encoding", "binary");
			
			response.getOutputStream().write(files);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}
	
}
