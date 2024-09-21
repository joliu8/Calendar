package calendar;

/**
* TimeInteral
* @author Joyce Liu 
* @version 1.0 9/7/2024
* */
import java.time.LocalTime;
/**
* A time has a start time and an end time that can be set and get.
*/

public class TimeInterval {
	// private variables
	private LocalTime st;
	private LocalTime et;

	// constructor
	/**
	 * Constructs a TimeInterval with a start and end LocalTime
	 * @param start - a LocalTime object with the start time
	 * @param end - a LocalTime object with the end time
	 */
	public TimeInterval(LocalTime start, LocalTime end) {
		setStartTime(start);
		setEndTime(end);
	}

	// setters and getters
	/**
	 * Returns the start time of an event
	 * @return a LocalTime start time
	 */
	public LocalTime getStartTime() {
		return st;
	}

	/**
	 * Return the end time of an event
	 * @return a LocalTime end time
	 */
	public LocalTime getEndTime() {
		return et;
	}

	/**
	 * Sets the start time of an event
	 * @param time - LocalTime to set the start time to
	 */
	public void setStartTime(LocalTime time) {
		st = time;
	}

	/**
	 * Sets the end time of an event
	 * @param time - LocalTime to set the end time to
	 */
	public void setEndTime(LocalTime time) {
		et = time;
	}
}
