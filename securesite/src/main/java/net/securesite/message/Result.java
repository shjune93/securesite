package net.securesite.message;

// 상태 메시지를 model에 담거나 json으로 만들때 사용.
public class Result {

	
	private boolean valid; // ok, error
	private String errorMessage;

	private Result(boolean valid) {
		this.valid=valid;
	}
	
	private Result(boolean valid,String errorMessage) {
		this.valid=valid;
		this.errorMessage=errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean isValid() {
		return valid;
	}


	public static Result ok() {
		return new Result(true);
	}
	
	public static Result fail(String errorMessage) {
		return new Result(false,errorMessage);
	}


}
