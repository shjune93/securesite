package net.securesite.web;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



import net.securesite.domain.CategoryBoard;

import net.securesite.domain.User;

import net.securesite.message.Result;

import net.securesite.repository.CategoryBoardRepository;

import net.securesite.session.HttpSessionUtils;

import lombok.Getter;
import lombok.Setter;


@Controller
@Getter
@Setter
@RequestMapping(value="categoryboard",method = {RequestMethod.GET, RequestMethod.POST}) //Get,Post Mapping 동작하게 해줌
public class CategoryBoardController {
	

	@Autowired
	private CategoryBoardRepository categoryBoardRepository;
	


	private Result valid(HttpSession session,CategoryBoard categoryBoard) {
		if(!HttpSessionUtils.isLoginUser(session)) {

			return Result.fail("로그인이 필요합니다.");
			//throw new IllegalStateException("로그인이 필요합니다.");
		}
		User loginUser=HttpSessionUtils.getUserSession(session);
		if(!categoryBoard.isSameWriter(loginUser)) {
			return Result.fail("자신이 쓴 글만 수정,삭제 가능합니다.");
			//throw new IllegalStateException("자신이 쓴 글만 수정,삭제 가능합니다.");
		}
		return Result.ok();

	}

	//리스트 보기
	@GetMapping("list/{curPage}")
	public String list(
			Model model,
			@PathVariable int curPage,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String contents
			) {
		//model.addAttribute("CategoryBoards",CategoryBoardRepository.findAll());
		List<CategoryBoard> categoryBoardList;
		 if (keyword != null) {
			 categoryBoardList= categoryBoardRepository.findByTitleContaining(keyword);
	        } else if (contents != null) {
	        	categoryBoardList= categoryBoardRepository.findByContentsContaining(contents);
	        }else {
	        	categoryBoardList=categoryBoardRepository.findAll();
	        	model.addAttribute("Null","null");
	        }
		//List<CategoryBoard> CategoryBoardList=CategoryBoardRepository.findAll();
		int listCnt=categoryBoardList.size();
		
		CategoryBoard[] arrayCategoryBoard=categoryBoardList.toArray(new CategoryBoard[listCnt]);
		Arrays.sort(arrayCategoryBoard);
		//System.out.println(arrayCategoryBoard.length);
		
		List tmpArrayList = new ArrayList<CategoryBoard>();
		int pageRange=(curPage-1)/10; //현재 페이지 범위
		for(int i=(curPage-1)*10;i<(curPage-1)*10+10;i++) {//현재 페이지에 해당하는 글담기
			if(i==arrayCategoryBoard.length)break;
			tmpArrayList.add(arrayCategoryBoard[i]);
			//System.out.println(arrayList[i]);
			
			
		}
		List beforePageNum=new ArrayList<Integer>();
		List curPageNum=new ArrayList<Integer>();
		List nextPageNum=new ArrayList<Integer>();
		int next=0;
		int before=0;
		if(pageRange!=0) {
			before=(pageRange-1)*10+10;
			model.addAttribute("before",before);
		}
		for(int i=1;i<=10;i++) {//현재페이지에 해당하는 페이지 범위 표시
			//페이지숫자
			if(pageRange*10+i>listCnt/10+1)break; //페이지 끝일경우
			//pageNum.add(new PageNum(pageRange*10+i));
			if(curPage>pageRange*10+i) {
				beforePageNum.add(pageRange*10+i);
			}else if(curPage<pageRange*10+i) {
				nextPageNum.add(pageRange*10+i);
			}else {
				curPageNum.add(pageRange*10+i);
			}
			//pageNum.add(pageRange*10+i); 페이지 추가
			if(i==10) {//next표시여부
				next=(pageRange+1)*10+1;
				model.addAttribute("next",next);
			}
		}
		model.addAttribute("keyword",keyword);
		model.addAttribute("beforePageNum",beforePageNum);
		model.addAttribute("curPageNum",curPageNum);
		model.addAttribute("nextPageNum",nextPageNum);
		//model.addAttribute("page",pageNum); //전체 페이지 넘겨주기
		model.addAttribute("categoryboards",tmpArrayList);
//		
		return "/categoryboard/categorylist";
	}


	//상세내용 보기
	@GetMapping("{listNum}/{id}")//@RequestParam("errorMessage") String errorMessage 리다이렉트 받는부분
	public String show(
			HttpSession session,
			@PathVariable Long id,
			@PathVariable int listNum,
			@RequestParam(required = false) String keyword,
			Model model) {
		CategoryBoard categoryBoard=categoryBoardRepository.findById(id).get();
		Result result=valid(session,categoryBoard);
		//System.out.println("null");
		if(!result.isValid()) {
			//인증안될시 알림창 띄움
			model.addAttribute("showupdatedelete","show"); //수정/삭제 생성여부 결정
			
		}
	
	
		model.addAttribute("keyword",keyword);
		model.addAttribute("listNum",listNum);
		model.addAttribute("categoryBoard",categoryBoard);
		return "/categoryboard/categoryshow";
	}


	//#로그인 확인후 게시판 적기 이동
	@GetMapping("form")
	public String loginForm(HttpSession session, HttpServletRequest request) {
		if (session.getAttribute(HttpSessionUtils.User_SESSION_KEY) == null) {
			// 이미 로그인 상태일 경우
			return "redirect:/login";
		}
		return "/categoryboard/categoryform";
	}


	//게시판 수정 폼으로 이동
	@PostMapping("{listNum}/{id}/updateform")
	public String updateForm(
			HttpSession session,
			@PathVariable Long id,
			@PathVariable int listNum,
			Model model){

		CategoryBoard categoryBoard=categoryBoardRepository.findById(id).get();
		Result result=valid(session,categoryBoard);
		if(!result.isValid()) {
			//인증안될시 알림창 띄움
			model.addAttribute("errorMessage",result.getErrorMessage()); //알림창에 뜰 메세지 저장 리다이렉트 되는곳으로 전달
			//model.addAttribute("CategoryBoard",CategoryBoard);
//			response.setContentType("text/html; charset=UTF-8");
//			PrintWriter out = response.getWriter();
//			out.println("<script>alert("+result.getErrorMessage()+");location.href='/CategoryBoard/"+id+"';</script>");
//			out.flush();
			return "redirect:/categoryBoard/"+id;
			//return "";
			
		}
	
		model.addAttribute("categoryBoard",categoryBoard);
		return "/categoryboard/categoryupdateForm";
	}
	//redirect는 주소로 없으면 templates 폴더의 html파일로

	//게시판 수정
	@PostMapping("{listNum}/{id}/update")
	public String update(HttpSession session,
			@PathVariable Long id,
			@PathVariable int listNum,
			String title,
			String contents,
			String link,
			Model model) {

		CategoryBoard CategoryBoard=categoryBoardRepository.findById(id).get();
		Result result=valid(session,CategoryBoard);
		if(!result.isValid()) {
			model.addAttribute("errorMessage",result.getErrorMessage());
			return "redirect:/categoryboard/"+id;
		}

		CategoryBoard.update(title,link,contents);
		categoryBoardRepository.save(CategoryBoard);
	
		return "redirect:/categoryboard/"+listNum+"/"+id;
	}

	//게시판 삭제
	@PostMapping("{listNum}/{id}/delete")
	public String delete(HttpSession session
			,@PathVariable Long id
			,@PathVariable int listNum
			,Model model){

		CategoryBoard CategoryBoard=categoryBoardRepository.findById(id).get();
		Result result=valid(session,CategoryBoard);
		if(!result.isValid()) {
			model.addAttribute("errorMessage",result.getErrorMessage());
			return "redirect:/categoryboard/"+listNum+"/"+id;
		}


		categoryBoardRepository.deleteById(id);
		return "redirect:/categoryboard/list/1";
	}


	//게시판등록
	@PostMapping("create")
	public String create(String title,String contents,String link,HttpSession session){
		if(!HttpSessionUtils.isLoginUser(session)) {
			return "/login";
		}


		User sessionUser=HttpSessionUtils.getUserSession(session);
		CategoryBoard categoryBoard=new CategoryBoard(sessionUser,title,link,contents);

		CategoryBoard savedCategoryBoard=categoryBoardRepository.save(categoryBoard);
		//파일 저장 파일 db저장
		
		return "redirect:/categoryboard/list/1";
	}
}
