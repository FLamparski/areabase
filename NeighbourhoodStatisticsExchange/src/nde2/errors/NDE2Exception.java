package nde2.errors;

/**
 * A subtype of Exception that is used by the NDE2 API. It is thrown when
 * internal errors happen, or when the server itself returns a problem. If it is
 * a server error, use the ness* properties to determine what it is.
 * 
 * @author filip
 * 
 */
public class NDE2Exception extends Exception {

	private static final long serialVersionUID = -2521548020242335676L;

	private String nessMessage;
	private String nessDetail;
	private int nessCode;

	public NDE2Exception() {
	}

	public NDE2Exception(String detailMessage) {
		super(detailMessage);
	}

	public NDE2Exception(Throwable throwable) {
		super(throwable);
	}

	public NDE2Exception(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NDE2Exception(String nessMessage, String nessDetail, int nessCode) {
		this.nessMessage = nessMessage;
		this.nessDetail = nessDetail;
		this.nessCode = nessCode;
	}

	public NDE2Exception(String nessMessage, String nessDetail, int nessCode,
			Throwable cause) {
		super(cause);
		this.nessMessage = nessMessage;
		this.nessDetail = nessDetail;
		this.nessCode = nessCode;
	}

	public NDE2Exception(String nessMessage, String nessDetail, int nessCode,
			String detailMessage, Throwable cause) {
		super(detailMessage, cause);
		this.nessMessage = nessMessage;
		this.nessDetail = nessDetail;
		this.nessCode = nessCode;
	}

	public String getNessMessage() {
		return nessMessage;
	}

	public String getNessDetail() {
		return nessDetail;
	}

	public int getNessCode() {
		return nessCode;
	}

	/**
	 * @param nessMessage
	 *            the nessMessage to set
	 */
	public void setNessMessage(String nessMessage) {
		this.nessMessage = nessMessage;
	}

	/**
	 * @param nessDetail
	 *            the nessDetail to set
	 */
	public void setNessDetail(String nessDetail) {
		this.nessDetail = nessDetail;
	}

	/**
	 * @param nessCode
	 *            the nessCode to set
	 */
	public void setNessCode(int nessCode) {
		this.nessCode = nessCode;
	}

}
