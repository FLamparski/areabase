package nde2.types.discovery;

import nde2.types.NDE2Result;

public class Subject extends NDE2Result {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private int id;

	public Subject(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

}
