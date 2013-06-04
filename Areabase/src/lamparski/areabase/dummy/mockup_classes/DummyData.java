/**
 * 
 */
package lamparski.areabase.dummy.mockup_classes;

/**
 * Indicates that whatever code is annotated by this is a placeholder or uses
 * dummy data.
 * 
 * @author filip
 * 
 */
public @interface DummyData {
	/**
	 * 
	 * @return The reason for using dummy data.
	 */
	String why() default "Will implement later";

	/**
	 * 
	 * @return What real data should be used.
	 */
	String replace_with() default "Something that does what it says on the tin.";
}
