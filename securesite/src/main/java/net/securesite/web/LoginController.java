package net.securesite.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.securesite.domain.User;
import net.securesite.domain.FreeBoard;
import net.securesite.message.Result;
import net.securesite.repository.UserRepository;
import net.securesite.session.HttpSessionUtils;

//  로그인 관리
@Controller
public class LoginController {

	@Autowired
	private UserRepository UserRepository;


	// #로그인 FORM
	@GetMapping("login")
	public String loginForm(HttpSession session, HttpServletRequest request) {
		if (session.getAttribute(HttpSessionUtils.User_SESSION_KEY) != null) {
			// 이미 로그인 상태일 경우
			return "redirect:/";
		}
		return "/login/login";
	}

	// 로그인
	@PostMapping("login")
	public String login(User loginUser, HttpSession session, Model model) {
		User User = UserRepository.findByUserId(loginUser.getUserId());

		// 정보가 없는 회원 일경우..
		if (User == null) {
			Result result=Result.fail("존재하지 않는 아이디입니다.");
			model.addAttribute("errorMessage",result.getErrorMessage());
			return "/login/login";
		}

		// 아이디 비밀번호 체크
		if (User.machUser(loginUser)) {
			// 로그인된 계정 세션에 저장
			HttpSessionUtils.save(session, User);

			return "redirect:/"; // 로그인 완료
		}

		Result result=Result.fail("비밀 번호가 틀렸습니다.");
		model.addAttribute("errorMessage",result.getErrorMessage());
		// 비밀번호가 틀릴경우
		return "/login/login";
		
	}

	// #로그아웃
	@GetMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate(); // 모든 세션 삭제
		return "redirect:/";
	}
}
