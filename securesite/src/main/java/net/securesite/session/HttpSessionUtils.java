package net.securesite.session;

import javax.servlet.http.HttpSession;

import net.securesite.domain.User;
import net.securesite.enums.Authority;

// 홈페이지  세션 관리 클래스
public class HttpSessionUtils {

	// 사용자 세션
	public static final String User_SESSION_KEY = "sessionedUser";

	// 관리자 세션
	public static final String MANAGER_SESSION_KEY = "sessionedManager";

	// 세션값에 sessionedUser 값이 있다면 User 객체를 리턴 해준다.
	public static User getUserSession(HttpSession session) {
		if (session.getAttribute(User_SESSION_KEY) == null) {
			return null; // 세션 값이 없으면 null 리턴
		}
		return (User)session.getAttribute(User_SESSION_KEY);
	}

	public static void save(HttpSession session, User User) {
		// 로그인된 계정에 세션값 할당
		session.setAttribute(HttpSessionUtils.User_SESSION_KEY, User);

		// 권한 설정 MANAGER(관리) 세션값 할당
		if (User.getAuthority().equals(Authority.MANAGER)) {
			session.setAttribute(HttpSessionUtils.MANAGER_SESSION_KEY, "OK");
		}
	}
	
	
	public static boolean isLoginUser(HttpSession session) {
		Object sessionedUser=session.getAttribute(User_SESSION_KEY);
		if(sessionedUser==null) {
			return false;
		}
		return true;
	}
	
	
	
	
	
	

}
