package net.securesite.web;


import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import net.securesite.domain.User;
import net.securesite.domain.FreeBoard;
import net.securesite.domain.FreeBoardFile;
import net.securesite.fileservice.FileUploadDownloadService;
import net.securesite.message.Result;

import net.securesite.repository.FreeBoardFileRepository;
import net.securesite.repository.FreeBoardRepository;
import net.securesite.session.HttpSessionUtils;

import lombok.Getter;
import lombok.Setter;


@Controller
@Getter
@Setter
@RequestMapping(value="freeboard",method = {RequestMethod.GET, RequestMethod.POST}) //Get,Post Mapping 동작하게 해줌
public class FreeBoardContoller {
	private static final Logger logger = LoggerFactory.getLogger(FreeBoardContoller.class);

	@Autowired
	private FileUploadDownloadService service;

	@Autowired
	private FreeBoardRepository freeBoardRepository;
	@Autowired
	private FreeBoardFileRepository freeBoardFileRepository;



	private Result valid(HttpSession session,FreeBoard freeboard) {
		if(!HttpSessionUtils.isLoginUser(session)) {

			return Result.fail("로그인이 필요합니다.");
			//throw new IllegalStateException("로그인이 필요합니다.");
		}
		User loginUser=HttpSessionUtils.getUserSession(session);
		if(!freeboard.isSameWriter(loginUser)) {
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
		//model.addAttribute("freeboards",freeBoardRepository.findAll());
		List<FreeBoard> freeBoardList;
		 if (keyword != null) {
			 freeBoardList= freeBoardRepository.findByTitleContaining(keyword);
	        } else if (contents != null) {
	        	freeBoardList= freeBoardRepository.findByContentsContaining(contents);
	        }else {
	        	freeBoardList=freeBoardRepository.findAll();
	        	model.addAttribute("Null","null");
	        }
		//List<FreeBoard> freeBoardList=freeBoardRepository.findAll();
		int listCnt=freeBoardList.size();
		
		FreeBoard[] arrayFreeBoard=freeBoardList.toArray(new FreeBoard[listCnt]);
		Arrays.sort(arrayFreeBoard);
		//System.out.println(arrayFreeBoard.length);
		
		List tmpArrayList = new ArrayList<FreeBoard>();
		int pageRange=(curPage-1)/10; //현재 페이지 범위
		for(int i=(curPage-1)*10;i<(curPage-1)*10+10;i++) {//현재 페이지에 해당하는 글담기
			if(i==arrayFreeBoard.length)break;
			tmpArrayList.add(arrayFreeBoard[i]);
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
		model.addAttribute("freeboards",tmpArrayList);
//		
		return "/freeboard/list";
	}


	//상세내용 보기
	@GetMapping("{listNum}/{id}")//@RequestParam("errorMessage") String errorMessage 리다이렉트 받는부분
	public String show(
			HttpSession session,
			@PathVariable Long id,
			@PathVariable int listNum,
			@RequestParam(required = false) String keyword,
			Model model) {
		FreeBoard freeboard=freeBoardRepository.findById(id).get();
		Result result=valid(session,freeboard);
		//System.out.println("null");
		if(!result.isValid()) {
			//인증안될시 알림창 띄움
			model.addAttribute("showupdatedelete","show"); //수정/삭제 생성여부 결정
			
		}
	
	
		model.addAttribute("keyword",keyword);
		model.addAttribute("listNum",listNum);
		model.addAttribute("freeboard",freeboard);
		return "/freeboard/show";
	}


	//#로그인 확인후 게시판 적기 이동
	@GetMapping("form")
	public String loginForm(
			HttpSession session) {
		if (session.getAttribute(HttpSessionUtils.User_SESSION_KEY) == null) {
			// 이미 로그인 상태일 경우
			return "redirect:/login";
		}
		return "/freeboard/form";
	}


	//게시판 수정 폼으로 이동
	@PostMapping("{listNum}/{id}/updateform")
	public String updateForm(HttpSession session,
			@PathVariable Long id,
			@PathVariable int listNum,
			Model model){

		FreeBoard freeboard=freeBoardRepository.findById(id).get();
		Result result=valid(session,freeboard);
		if(!result.isValid()) {
			//인증안될시 알림창 띄움
			model.addAttribute("errorMessage",result.getErrorMessage()); //알림창에 뜰 메세지 저장 리다이렉트 되는곳으로 전달
			return "redirect:/freeboard/"+listNum+"/"+id;
			//return "";
			
		}
		model.addAttribute("listNum",listNum);
		model.addAttribute("freeboard",freeboard);
		return "/freeboard/updateForm";
	}
	//redirect는 주소로 없으면 templates 폴더의 html파일로

	//게시판 수정
	@PostMapping("{listNum}/{id}/update")
	public String update(
			HttpSession session,
			@PathVariable int listNum,
			@PathVariable Long id,
			String title,
			String contents,
			@RequestParam("file") MultipartFile file,
			Boolean fileDeleteCheck,
			Model model) {

		FreeBoard freeboard=freeBoardRepository.findById(id).get();
		Result result=valid(session,freeboard);
		if(!result.isValid()) {
			model.addAttribute("errorMessage",result.getErrorMessage());
			return "redirect:/freeboard/"+listNum+"/"+id;
		}

		freeboard.update(title,contents);
		freeBoardRepository.save(freeboard);
		if(!file.isEmpty()) {
			if(freeboard.getFile()!=null) {
				String path="~/securesite/securesite/securesite/files/"+freeboard.getId().toString()+"/"+freeboard.getFile().getFileName();
				System.out.println("파일경로"+path);
				File beforeFile= new File(path);
				if(beforeFile.exists()==true) {

					beforeFile.delete();
					System.out.println(path+"삭제 완료");
				}
				freeBoardFileRepository.deleteById(freeboard.getFile().getId());
				//freeBoardFileRepository.deleteById(id);
			}
			
			String i=id.toString();
			String fileName = service.storeFile(file,i);


			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/freeboard/downloadFile/")	
					.path(i+"/")
					.path(fileName)
					.toUriString();

			FreeBoardFile freeboardFile=new FreeBoardFile(fileName, fileDownloadUri, file.getContentType(), file.getSize(),freeboard);
			freeBoardFileRepository.save(freeboardFile);

		}
		if(fileDeleteCheck!=null&&fileDeleteCheck){
			if(freeboard.getFile()!=null) {
				String path="~/securesite/securesite/securesite/files/"+freeboard.getId().toString()+"/"+freeboard.getFile().getFileName();
				System.out.println("파일경로"+path);
				File beforeFile= new File(path);
				if(beforeFile.exists()==true) {

					beforeFile.delete();
					System.out.println(path+"삭제 완료");
				}
				freeBoardFileRepository.deleteById(freeboard.getFile().getId());
				//freeBoardFileRepository.deleteById(id);
			}
			
		}
		return "redirect:/freeboard/"+listNum+"/"+id;
	}

	//게시판 삭제
	@PostMapping("{id}/delete")
	public String delete(HttpSession session,HttpServletRequest request,@PathVariable Long id,Model model){

		FreeBoard freeboard=freeBoardRepository.findById(id).get();
		Result result=valid(session,freeboard);
		if(!result.isValid()) {
			model.addAttribute("errorMessage",result.getErrorMessage());
			return "redirect:/freeboard/"+id;
		}
		if(freeboard.getFile()!=null) {
			//파일경로 하드코딩
			String path="~/securesite/securesite/securesite/files/"+freeboard.getId().toString()+"/"+freeboard.getFile().getFileName();
			//
			System.out.println("파일경로"+path);
			File file= new File(path);
			if(file.exists()==true) {
				System.out.println("여기 들어옴");
				file.delete();
			}
			freeBoardFileRepository.deleteById(freeboard.getFile().getId());
		}


		freeBoardRepository.deleteById(id);
		return "redirect:/freeboard/list";
	}


	//게시판등록
	@PostMapping("create")
	public String create(
			String title,
			String contents,
			@RequestParam("file") MultipartFile file,
			HttpSession session){
		if(!HttpSessionUtils.isLoginUser(session)) {
			return "/login";
		}


		User sessionUser=HttpSessionUtils.getUserSession(session);
		FreeBoard freeboard=new FreeBoard(sessionUser,title,contents);

		FreeBoard savedFreeboard=freeBoardRepository.save(freeboard);
		//파일 저장 파일 db저장
		if(!file.isEmpty()) {
			String i=savedFreeboard.getId().toString();
			String fileName = service.storeFile(file,i);
			System.out.println("파일 이름 "+fileName);


			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path("/freeboard/downloadFile/")	
					.path(i+"/")
					.path(fileName)
					.toUriString();

			FreeBoardFile freeboardFile=new FreeBoardFile(fileName, fileDownloadUri, file.getContentType(), file.getSize(),savedFreeboard);
			freeBoardFileRepository.save(freeboardFile);



		}
		return "redirect:/freeboard/list/1";
	}

	//파일 다운로드
	@GetMapping("downloadFile/{id}/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,@PathVariable String id, HttpServletRequest request){
		// Load file as Resource
		//System.out.println(fileName);
		Resource resource = service.loadFileAsResource(id+"/"+fileName);
		//System.out.println(resource.getFilename());


		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			System.out.println("컨텐츠 타입"+contentType);
			// System.out.println(contentType);
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	
//	@GetMapping("list/{curPage}")
//	public String search(@RequestParam(required = false) String title,
//            			@RequestParam(required = false) String contents,
//            			@PathVariable int curPage,Model model) {
//		List<FreeBoard> freeBoardSearchList;
//		 if (title != null) {
//			 freeBoardSearchList= freeBoardRepository.findByTitleContaining(title);
//	        } else if (contents != null) {
//	        	 freeBoardSearchList= freeBoardRepository.findByContentsContaining(contents);
//	        }else {
//	        	freeBoardSearchList=freeBoardRepository.findAll();
//	        	model.addAttribute("Null","null");
//	        }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//			
//			int listCnt=freeBoardSearchList.size();
//			
//			FreeBoard[] arrayFreeBoard=freeBoardSearchList.toArray(new FreeBoard[listCnt]);
//			Arrays.sort(arrayFreeBoard);
//			//System.out.println(arrayFreeBoard.length);
//			
//			List tmpArrayList = new ArrayList<FreeBoard>();
//			int pageRange=(curPage-1)/10; //현재 페이지 범위
//			for(int i=(curPage-1)*10;i<(curPage-1)*10+10;i++) {//현재 페이지에 해당하는 글담기
//				if(i==arrayFreeBoard.length)break;
//				tmpArrayList.add(arrayFreeBoard[i]);
//				//System.out.println(arrayList[i]);
//				
//				
//			}
//			List beforePageNum=new ArrayList<Integer>();
//			List curPageNum=new ArrayList<Integer>();
//			List nextPageNum=new ArrayList<Integer>();
//			int next=0;
//			int before=0;
//			if(pageRange!=0) {
//				before=(pageRange-1)*10+10;
//				model.addAttribute("before",before);
//			}
//			for(int i=1;i<=10;i++) {//현재페이지에 해당하는 페이지 범위 표시
//				//페이지숫자
//				if(pageRange*10+i>listCnt/10+1)break; //페이지 끝일경우
//				//pageNum.add(new PageNum(pageRange*10+i));
//				if(curPage>pageRange*10+i) {
//					beforePageNum.add(pageRange*10+i);
//				}else if(curPage<pageRange*10+i) {
//					nextPageNum.add(pageRange*10+i);
//				}else {
//					curPageNum.add(pageRange*10+i);
//				}
//				//pageNum.add(pageRange*10+i); 페이지 추가
//				if(i==10) {//next표시여부
//					next=(pageRange+1)*10+1;
//					model.addAttribute("next",next);
//				}
//			}
//			model.addAttribute("title",title);
//			model.addAttribute("beforePageNum",beforePageNum);
//			model.addAttribute("curPageNum",curPageNum);
//			model.addAttribute("nextPageNum",nextPageNum);
//			//model.addAttribute("page",pageNum); //전체 페이지 넘겨주기
//			model.addAttribute("freeboards",tmpArrayList);
//			
//			return "/freeboard/list";
//	}



}



/////////////////////////////////////////////////////////////////////////// 파일업로드 원본
//@PostMapping("{id}/uploadFile")
//public FreeBoardFile uploadFile(@RequestParam("file") MultipartFile file,@PathVariable Long id) {
//	String i="";
//    String fileName = service.storeFile(file,i);
//    
//    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                            .path("/downloadFile/")
//                            .path(fileName)
//                            .toUriString();
//    
//    return new FreeBoardFile(fileName, fileDownloadUri, file.getContentType(), file.getSize());
//}

///////////////////////////////////////////////////////////////////////////////////////////////

//@PostMapping("{id}/uploadMultipleFiles")
//public List<FreeBoardFile> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,@PathVariable Long id){
//    return Arrays.asList(files)
//            .stream()
//            .map(file -> uploadFile(file,id))
//            .collect(Collectors.toList());
//}