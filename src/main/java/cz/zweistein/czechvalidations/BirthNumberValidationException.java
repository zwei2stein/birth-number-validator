package cz.zweistein.czechvalidations;

/**
 * 
 * Holder of error message for birth number validation
 * 
 * @author Petr Prokop
 *
 */
public class BirthNumberValidationException extends Exception {
	private static final long serialVersionUID = 9031608194616187505L;
	
	private BirthNumberValidityError errorCode;

	public BirthNumberValidationException(String message, BirthNumberValidityError errorCode) {
		super(message);
		
		this.errorCode = errorCode;
	}

	public BirthNumberValidityError getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getClass().getName());
		sb.append(": ").append(this.getMessage());
		sb.append(", ").append(this.errorCode);
		
		return sb.toString();
	}
	
	

}
