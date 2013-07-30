package areabase.tests;

import java.lang.reflect.Field;

public class Repr {
	/**
	 * Prints information about the object, including its non-static fields.
	 * 
	 * @param obj
	 *            The object to print information about
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void repr(Object obj) throws IllegalArgumentException,
			IllegalAccessException {
		Class<?> cls = obj.getClass();
		System.out.printf("Class %s\n", cls.getName());
		Field[] clsDeclaredFields = cls.getDeclaredFields();
		System.out.printf("  %d DECLARED fields; listing non-static\n",
				clsDeclaredFields.length);
		System.out.println("  NAME\t\t\tVALUE");
		for (Field field : clsDeclaredFields) {
			field.setAccessible(true);
			if (!(java.lang.reflect.Modifier.isStatic(field.getModifiers()))) {
				try {
					System.out.printf("  %s\t\t\t%s\n", field.getName(), field
							.get(obj).toString());
				} catch (Exception e) {
					break;
				}
			}
		}
	}
}
