package com.dd.menyoo.model;

public class BaseResponse {

	public BaseResponse() {
		Success = false;
		FaultMessage = "";
		FaultType = "";
		ErrorCode = 0;
		exception = new Exception();
        IsEmailVerficationRequired = false;
	}

	public enum ErrorType {
		// PENNY(1), NICKLE(5), DIME(10), QUARTER(25);
		NetworkingError(-1), UnknownError(0), EmailAlreadyExists(1), PhoneNumberAlreadyExists(
				2), InvalidUserCredentials(3), TargetUserNotOnline(4), UserNotLoggedIn(
				5), RequestedFileNotFound(6), NoFileToUpload(7);

		public final int ErrorCode;

		private ErrorType(int ErrorCode) {
			this.ErrorCode = ErrorCode;
		}

		public int getValue() {
			return ErrorCode;
		}

	}


	public String FaultMessage;
	public String FaultType;
	public String CountryCode;
	public int ErrorCode;
	public Exception exception;
	public String RemainingTimeBeforeUpload;
    public Boolean Success;
    public Boolean IsEmailVerficationRequired;
    public Boolean IsPhoneVerificationRequired;
    public Boolean WasItPhoneNumber;
    public int CountryId;

}
