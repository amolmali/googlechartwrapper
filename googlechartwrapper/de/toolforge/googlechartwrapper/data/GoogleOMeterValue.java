package de.toolforge.googlechartwrapper.data;

/**
 * Specifies a Google O Meter arrow.
 * 
 * @author martin
 * @author steffan
 * 
 */
public class GoogleOMeterValue {

	private String label;
	private double value;

	/**
	 * Constructs a GoogleOMeter value, which is rendered as an arrow. The label
	 * is displayed at the pike of the arrow.
	 * 
	 * @param label
	 *            label to display, can be empty or.
	 * @param value
	 *            value of the arrow
	 * 
	 * @throws IllegalArgumentException
	 *             if label is {@code null}
	 */
	public GoogleOMeterValue(String label, double value) {

		if (label == null)
			throw new IllegalArgumentException("label can not be null");

		this.label = label;
		this.value = value;
	}

	/**
	 * Returns the label text.
	 * 
	 * @return text
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label text.
	 * 
	 * @param label
	 *            text to be displayed at the pike of the arrow
	 * 
	 * @throws IllegalArgumentException
	 *             if label is {@code null}
	 */
	public void setLabel(String label) {

		if (label == null)
			throw new IllegalArgumentException("label can not be null");

		this.label = label;
	}

	/**
	 * Returns the value where the arrow is rendered.
	 * 
	 * @return value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of the arrow.
	 * 
	 * @param value
	 *            where the arrow is rendered.
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
