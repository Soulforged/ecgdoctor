package edu.uk.dromm.fcl;

/**
 * 
 * @author dicardo
 * 
 */
public class Diagnosis {

	private final String title;
	private final String description;
	private final String detail;

	public Diagnosis(final String title, final String description,
			final String detail) {
		super();
		this.title = title;
		this.description = description;
		this.detail = detail;
	}

	public String getDescription() {
		return description;
	}

	public String getDetail() {
		return detail;
	}

	public String getTitle() {
		return title;
	}
}
