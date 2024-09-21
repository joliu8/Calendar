package calendar;

import java.time.LocalDate;

/**
* Event
* @author Joyce Liu 
* @version 1.0 9/7/2024
* */

/**
* A event has a name, date, and time interval that can be set and get.
*/

public class Event {
	private String name;
	private LocalDate date;
	private TimeInterval ti;
	
	/**
	 * Constructs a event with a name, date, and time interval
	 * @param name - a string event name
	 * @param d - the date of the event
	 * @param range - TimeInterval made of a start/end time
	 */

	public Event(String name, LocalDate d, TimeInterval range) {
		setName(name);
		setTimeInterval(range);
		setDate(d);

	}

	/**
	 * Returns the name of an event
	 * @return a string event name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the event 
	 * @param n - new name of event
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * Sets the time interval of the event
	 * @param t - new time interval of event
	 */
	public void setTimeInterval(TimeInterval t) {
		ti = t;
	}

	/**
	 * Returns the time interval of an event
	 * @return a TimeInterval
	 */
	public TimeInterval getTimeInterval() {
		return ti;
	}

	/**
	 * Returns the date of an event
	 * @return a LocalDate
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * Sets the date of an event to a particular date
	 * @param d - a LocalDate
	 */
	public void setDate(LocalDate d) {
		date = d;
	}

}
