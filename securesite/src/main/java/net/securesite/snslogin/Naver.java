package net.securesite.snslogin;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "data")
@Data
public class Naver {

	private Result result;
	private Customer response;

	@Data
	public static class Token {
		private String access_token;
		private String refresh_token;
		private String token_type;
		private String expires_in;
	}

	@Data
	public static class Result {
		private String resultcode;
		private String message;
	}

	@Data
	public static class Customer {
		private String enc_id;
		private String nickname;
		private String id;
		private String gender;
		private String age;
		private String birthday;
		private String profile_image;
	}

}
