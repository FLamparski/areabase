package nde2.methodcalls;

/**
 * Methods marked with this annotation call the NeSS web service. This means
 * they should be used sparingly and asynchronously.
 * 
 * @author filip
 * 
 */
public @interface WebMethod {
	public String endpoint();

	public String method();
}
