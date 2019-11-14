package net.securesite.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.securesite.domain.User;

import net.securesite.message.Result;
import net.securesite.repository.UserRepository;

import net.securesite.session.HttpSessionUtils;

// (관리 페이지) 회원 관리 클래스
@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserRepository userRepository;



	// #계정 삭제 FORM
	@GetMapping("/{{id}}")
	public String delete() {
		return "/user/delete";
	}

	// #계정 정보 수정 FORM
	@GetMapping
	public String update(Model model, HttpSession session) {
		// 현제 로그인 중인 계정 ID (세션 값)
		String id = HttpSessionUtils.getUserSession(session).getUserId();
		model.addAttribute("User", userRepository.findById(id));
		return "/user/update";
	}

	// #계정 정보 수정
	@PostMapping
	public String update(User newUser, Model model) {
		User User = userRepository.findByUserId(newUser.getUserId());

		User.update(newUser);
		// 수정된 객체(계정) 업데이트
		userRepository.save(User);

		model.addAttribute("User", User);
		//model.addAttribute(Result.MESSAGE_KEY, new Result("수정 완료"));
		return "/user/update";
	}

	// #계정 생성 FORM
	@GetMapping("/create")
	public String createForm() {
		return "/user/create";
	}
	
	@PostMapping("/create")
	public String create(User User) {
		System.out.println("User"+User);
		//users.add(user);
		userRepository.save(User);
		return "redirect:/login";
	}


}