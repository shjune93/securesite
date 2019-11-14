package net.securesite.snslogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import net.securesite.domain.User;
import net.securesite.enums.LoginType;
import net.securesite.repository.UserRepository;
import net.securesite.session.HttpSessionUtils;

import lombok.extern.slf4j.Slf4j;

// 카카오 로그인
// 1. 로그인 요청 ( 요청 : /oauth/authorize?... )  
//  - 로그인 성공할 경우 code 값을 받아온다. ( 응답 : code={authorize_code} ) 
// 2. 사용자 토큰 받기 ( 요청 : /oauth/token )
//  - 응답으로 KakaoToken 객체(getter/setter로 JSON값 사용)에 데이터(JSON) 담는다.
// 3. 로그인한 사용자 정보 조회 ( 2. 사용자 토큰을 사용해서 정보를 조회)
//  - 응답...
@Controller
@Slf4j
public class KakaoLoginController {

	// 애플리케이션 등록 후 발급받은 appKey [ 수정 O ]
	private final String appKey = "424b55f5ddb82448866d67cfe0a6aeec";
	// 인증의 결과를 전달받을 콜백 URL ( 로그인 요청을 처리할때 콜백 URI 값과 합쳐진다) [수정 X ]
	private String redirectURL = "";
	// 인증의 결과를 전달받을 콜백 URI . [ 수정 O ]
	private final String RedirectURI = "/redirectkakao";
	// 사용자 토큰 요청할 URL [수정 X ]
	private final String tokenURL = "https://kauth.kakao.com/oauth/token?"
			+ "grant_type=authorization_code&client_id={appKey}&redirect_uri={redirectUri}&code={authorizeCode}";
	// 요청할때 사용할 객체.
	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private UserRepository customerRepository;

	// 로그인 요청 성공후 콜백 되는 URL
	@GetMapping(RedirectURI)
	// 로그인 요청 성공후 얻은 code(토큰)값
	public String redirectUri(final String code, HttpSession session) {

		// 사용자 토큰 요청
		Kakao.Token kakaoToken = restTemplate.getForObject(tokenURL, Kakao.Token.class, appKey, redirectURL, code);

		HttpHeaders headers = new HttpHeaders();
		// Authorization 헤더 값추가해서
		headers.add("Authorization", "Bearer " + kakaoToken.getAccess_token());
		final String findByUserUrl = "https://kapi.kakao.com/v1/user/me";
		// 발급 받은 사용자 토큰을 사용해서 카카오 정보 요청
		ResponseEntity<Kakao.Customer> responseEntity = restTemplate.exchange(findByUserUrl, HttpMethod.GET,
				new HttpEntity<>(headers), Kakao.Customer.class);
		log.info("\n[카카오 응답 데이터 : {}]", responseEntity.getBody());
		
		User newCustomer = new  User(responseEntity.getBody().getId(), responseEntity.getBody().getProperties().getNickname(),
				LoginType.KAKAO);
		
		User Customer=customerRepository.findByUserId(responseEntity.getBody().getId());
		
		// 로그인을 한번도 하지 않았을 경우. db에 정보 저장.
		if (Customer == null) {
			// 정보 저장
			customerRepository.save(newCustomer);
			log.info("\n[카카오 첫 로그인 : {}]", newCustomer);
		}
		// 로그인된 계정에 세션값 저장
		HttpSessionUtils.save(session, newCustomer);

		return "redirect:/";
	}

	// 로그인 요청
	@GetMapping("/kakaologin")
	public String login(HttpServletRequest request) {
		// 인증의 결과를 전달받을 콜백 URL 얻어 온다. ( 응답 데이터 : http://Host/redirecturikakao )
		redirectURL = request.getRequestURL().toString().replace(request.getServletPath(), RedirectURI);

		return String.format(
				"redirect:https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
				appKey, redirectURL);
	}

}