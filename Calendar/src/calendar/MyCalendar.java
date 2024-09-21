package calendar;

/**
* MyCalendar
* @author Joyce Liu 
* @version 1.0 9/7/2024
* */

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A MyCalendar has arraylist data structures to store events and string of
 * events.
 */
public class MyCalendar {

	// different data structures
	private ArrayList<Event> events;

	private ArrayList<Event> onetime;

	private ArrayList<Event> recurring;

	private ArrayList<String> onetimeString;

	private ArrayList<String> recurringString;

	/**
	 * Constructs a MyCalendar, initalizes the arraylists, and reads the events.txt
	 * text file.
	 */
	public MyCalendar() {

		events = new ArrayList<Event>();

		onetime = new ArrayList<Event>();

		recurring = new ArrayList<Event>();

		onetimeString = new ArrayList<String>();

		recurringString = new ArrayList<String>();

		loadTextFile();

	}

	/**
	 * Reads the events.txt text file and adds the events into its respective arraylists 
	 * precondition: the text file must be formatted in a particular way where:
	 * 
	 * RECURRING EVENTS: first line: name of the event (may contain spaces); second
	 * line: a sequence of day abbreviations (SMTWRFA, upper or lower case) followed
	 * by a starting date and an ending date of the recurring event
	 * 
	 * ONETIME EVENTS: first line: name of the event (may contain spaces); second
	 * line: a date in the format mm/dd/yy (no spaces) e.g. 3/22/22, a starting time
	 * and an ending time (24-hour military time) e.g. 18:15 instead of 6:15 pm
	 */
	public void loadTextFile() {
		// use scanner to read events.txt and make it into events
		try {
			DateTimeFormatter time = DateTimeFormatter.ISO_LOCAL_TIME;
			DateTimeFormatter test = DateTimeFormatter.ISO_LOCAL_DATE;
			LocalTime start;
			LocalTime end;
			LocalDate date;

			File file = new File("events.txt");
			Scanner sc = new Scanner(file);

			while (sc.hasNextLine()) {

				String eventName = sc.nextLine();
				String eventInfo = sc.nextLine();
				String[] lineArray = eventInfo.split(" ");

				// adding a 0 to times like 9:00 where the hour is only 1 digit
				if (lineArray[1].length() < 5) {
					lineArray[1] = "0" + lineArray[1];
				}

				if (lineArray[2].length() < 5) {
					lineArray[2] = "0" + lineArray[2];
				}
				
				//turn the start and end time into a TimeInterval
				start = LocalTime.parse(lineArray[1], time);
				end = LocalTime.parse(lineArray[2], time);
				TimeInterval t = new TimeInterval(start, end);
				
				//if event is reoccuring, created a LocalDate for the start and the end date
				if (lineArray[0].length() <= 7) {
//					System.out.println("reoccuring");

					String[] singleDate = lineArray[3].split("/");
					LocalDate sd = LocalDate.parse(singleDate[2] + "-" + singleDate[0] + "-" + singleDate[1]);
					String[] secondDate = lineArray[4].split("/");
					LocalDate ed = LocalDate.parse(secondDate[2] + "-" + secondDate[0] + "-" + secondDate[1]);

					String[] recurrence = lineArray[0].split("");
					LocalDate nextWeekDay = null;
					
					//interpret the recurring weekdays of the recurring event 
					for (int s = 0; s < recurrence.length; s++) {

						if (recurrence[s].equals("M")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
						} else if (recurrence[s].equals("T")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
						} else if (recurrence[s].equals("W")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
						} else if (recurrence[s].equals("R")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
						} else if (recurrence[s].equals("F")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
						} else if (recurrence[s].equals("S")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
						} else if (recurrence[s].equals("U")) {
							nextWeekDay = sd.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
						}
						
						//create a stream of LocalDates from the start date until the end date, incrementing by one week
						// if it was MW, take the all the monday dates, then take all the wednesday dates
						Stream<LocalDate> dates = nextWeekDay.datesUntil(ed.plus(Period.ofDays(1)), Period.ofDays(7));
						Object[] datesArray = dates.toArray();
						
						//add these dates to the recurring arraylists
						for (int a = 0; a < datesArray.length; a++) {
							Event temp = new Event(eventName, (LocalDate) datesArray[a], t);
							events.add(temp);
							if (a == datesArray.length - 1
									&& !getRecurringStringArray().contains(eventName + "---" + eventInfo)) {
								recurring.add(temp);
								recurringString.add(eventName + "---" + eventInfo);
							}
						}

					}
					
				//handles one time events
				} else {
//					System.out.println("onetime");
					String[] singleDate = lineArray[0].split("/");
					date = LocalDate.parse(singleDate[2] + "-" + singleDate[0] + "-" + singleDate[1]);
					Event e = new Event(eventName, date, t);
					events.add(e);
					onetime.add(e);
					onetimeString.add(eventInfo + "---" + eventName);

				}

			}
			sc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("file not found");
		} catch (DateTimeParseException d) {
			d.printStackTrace();
			System.out.println("Please make sure your dates/times are in the right format.");
		}
	}

	/**
	 * Adds an event to the all events arraylist if there is no time conflict and sorts them 
	 * @param e - Event object to be added to the main events array
	 */
	public void addEvent(Event e) {
		boolean conflict = false;

		for (int i = 0; i < getSize(); i++) {
			LocalTime currEventStart = getEvent(i).getTimeInterval().getStartTime();
			LocalTime currEventEnd = getEvent(i).getTimeInterval().getEndTime();
			LocalTime newEventStart = e.getTimeInterval().getStartTime();
			LocalTime newEventEnd = e.getTimeInterval().getEndTime();

			//#1 && currEventEnd.isAfter(newEventStart)
			// #2 && currEventStart.isBefore(newEventEnd)
			//discod
//			(newEventStart.isBefore(currEventEnd) && newEventStart.isAfter(currEventStart)) ||
//			(newEventEnd.isBefore(currEventEnd) && newEventEnd.isAfter(currEventEnd)) ||
//			(newEventStart.isBefore(currEventStart) && newEventEnd.isAfter(currEventEnd))

			if (getEvent(i).getDate().compareTo(e.getDate()) == 0) {
				//ensures there is no time conflict between the new event we want to add and events we already have scheduled
				if ( (currEventEnd.isAfter(newEventStart) && currEventStart.isAfter(newEventStart) && currEventEnd.isAfter(newEventEnd) && currEventEnd.isAfter(newEventStart)) ||
						(currEventStart.isBefore(newEventEnd) && currEventStart.isBefore(newEventStart) && currEventEnd.isBefore(newEventEnd) && currEventEnd.isAfter(newEventStart)) ||
						(currEventStart.isBefore(newEventStart) && currEventEnd.isAfter(newEventEnd) && currEventStart.isBefore(newEventEnd) && currEventEnd.isAfter(newEventStart)) ||
						(currEventStart.isAfter(newEventStart) && currEventEnd.isBefore(newEventEnd) && currEventStart.isBefore(newEventEnd) && currEventEnd.isAfter(newEventStart))
						){
					conflict = true;
				}
			} 
		}
		
		if (conflict == false){
			events.add(e);
			onetime.add(e);
			addOneTimeEvent(e);
			System.out.println("Event created!");
		}
		
		if(conflict == true){
			System.out.println("You are busy during this time frame. Try Again. ");
		}
		sortByStartTime(events);

	}
	
	/**
	 * Adds an event to the one time event arraylists
	 * @param e - Event object to be added to the onetime arraylists 
	 */
	public void addOneTimeEvent(Event e) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		boolean ifContains = false;
		String line = "";
		for (String s : onetimeString) {
			line = formatter.format(e.getDate()) + " " + e.getTimeInterval().getStartTime() + " "
					+ e.getTimeInterval().getEndTime() + "---" + e.getName();
			if (s.equals(line)) {
				ifContains = true;

			}
		}
		if (ifContains == false) {
			onetimeString.add(line);
		}
	}
	
	/**
	 *  A comparator that compares events by their start time
	 */
	public class ListComparator implements Comparator<Event> {
		//need this comparator because events is a custom class

		// override the compare() method
		// compare the events by start time
		
		/**
		 * Compares two events by their start time
		 * @param s1 - an Event object
		 * @param s2 - an Event object
		 * @return an integer that represents whether s1 is greater than, less than, or equal to s2
		 */
		public int compare(Event s1, Event s2) {
			if (s1.getTimeInterval().getStartTime().equals(s2.getTimeInterval().getStartTime()))
				return 0;
			else if (s1.getTimeInterval().getStartTime().isAfter(s2.getTimeInterval().getStartTime()))
				return 1;
			else
				return -1;
		}

	}
	
	/**
	 * Sorts an arraylist of events by their start time using the ListComparator
	 * @param list - an ArrayList of events
	 */
	public void sortByStartTime(ArrayList<Event> list) {
		Collections.sort(list, new ListComparator());
	}

	/**
	 *  A comparator that compares events by their date
	 */
	public class DateComparator implements Comparator<Event> {

		// override the compare() method
		//compare the events by date 
		public int compare(Event s1, Event s2) {
			if (s1.getDate().equals(s2.getDate()))
				return 0;
			else if (s1.getDate().isAfter(s2.getDate()))
				return 1;
			else
				return -1;
		}

	}

	/**
	 * Sorts an arraylist of events by their dates using the DateComparator
	 * @param list - an ArrayList of events
	 */
	public void sortByDate(ArrayList<Event> list) {
		Collections.sort(list, new DateComparator());
	}

	/**
	 * Sorts an arraylist of events by their names alphabetically
	 * @param list - an ArrayList of events
	 */
	public void sortByName(ArrayList<String> list) {
		list.sort(null);
	}

	/**
	 * Returns the size of the all events arraylist
	 * @return the numerical size of the events arraylist
	 */
	public int getSize() {
		return events.size();
	}

	/**
	 * Returns the size of the one time events arraylist
	 * @return the numerical size of the onetime arraylist
	 */
	public int getOneTimeSize() {
		return onetime.size();
	}
	
	/**
	 * Returns the size of the recurring arraylist
	 * @return the numerical size of the recurring arraylist
	 */
	public int getRecurringSize() {
		return recurring.size();
	}

	/**
	 * Returns an event from the all events arraylist by index
	 * @param index - an index of the events arraylist
	 * @return an event
	 */
	public Event getEvent(int index) {
		return events.get(index);
	}

	/**
	 * Returns an event from the one time arraylist by index
	 * @param index - an index of the one time arraylist
	 * @return an event
	 */
	public Event getOneTimeEvent(int index) {
		return onetime.get(index);
	}

	/**
	 * Returns a string from the one time string arraylist by index
	 * @param index - an index of the one time string arraylist
	 * @return a string
	 */
	public String getOneTimeStringEvent(int index) {
		return onetimeString.get(index);
	}
	
	/**
	 * Returns a string from the recurring string arraylist by index
	 * @param index - an index of the recurring string arraylist
	 * @return a string
	 */
	public String getRecurringStringEvent(int index) {
		return recurringString.get(index);
	}

	/**
	 * Returns an event from the recurring arraylist by index
	 * @param index - an index of the recurring arraylist
	 * @return an event
	 */
	public Event getRecurringEvent(int index) {
		return recurring.get(index);
	}

	/**
	 * Returns the entire all events arraylist
	 * @return an arraylist of events
	 */
	public ArrayList<Event> getArray() {
		return events;
	}

	/**
	 * Returns the entire one time arraylist
	 * @return an arraylist of one time events
	 */
	public ArrayList<Event> getOneTimeArray() {
		return onetime;
	}
	
	/**
	 * Returns the entire recurring arraylist
	 * @return an arraylist of recurring events
	 */
	public ArrayList<Event> getRecurringArray() {
		return recurring;

	}

	/**
	 * Returns the entire one time string arraylist
	 * @return an arraylist of one time event strings
	 */
	public ArrayList<String> getOneTimeStringArray() {
		return onetimeString;
	}
	
	/**
	 * Returns the entire recurring string arraylist
	 * @return an arraylist of recurring event strings
	 */
	public ArrayList<String> getRecurringStringArray() {
		return recurringString;

	}

	/**
	 * Deletes an event from onetime arraylists based on index
	 * @param index - an index of the one time arraylists
	 */
	public void deleteOneTime(int index) {
		onetime.remove(index);
		onetimeString.remove(index);
	}

	/**
	 * Deletes an event from recurring arraylists based on index
	 * @param index - an index of the recurring arraylists
	 */
	public void deleteRecurring(int index) {
		recurring.remove(index);
		recurringString.remove(index);
	}
	/**
	 * 
	 * Deletes an event from all events arraylists based on index
	 * @param index - an index of the all events arraylists
	 */
	public void delete(int index) {
		events.remove(index);
	}

	/**
	 * Prints events of a particular date in all events arraylist in order of start time
	 * @param d - a LocalDate 
	 */
	public void printEvents(LocalDate d) {
		sortByStartTime(getArray());
		for (int i = 0; i < getSize(); i++) {
			LocalDate currEvent = getEvent(i).getDate();
			if (currEvent.compareTo(d) == 0)
				System.out.println(getEvent(i).getName() + " : " + getEvent(i).getTimeInterval().getStartTime() + " - "
						+ getEvent(i).getTimeInterval().getEndTime());
		}
	}
	
	/**
	 * Prints all strings from the one time string arraylist in sorted order
	 */
	public void printOneTime() {
		sortByStartTime(getOneTimeArray());
		sortByName(getOneTimeStringArray());

		for (int i = 0; i < getOneTimeStringArray().size(); i++) {
			System.out.println(getOneTimeStringEvent(i));
		}
	}
	/**
	 * Prints all strings from the recurring string arraylist in sorted order
	 */
	public void printRecurring() {
		sortByStartTime(getRecurringArray());
		sortByName(getRecurringStringArray());

		for (int i = 0; i < getRecurringSize(); i++) {
			System.out.println(getRecurringStringEvent(i));
		}
	}

	/**
	 * Deletes a one time event of a particular date and name, printing the if it was successful or not
	 * @param d - a LocalDate of the event
	 * @param s - a string name of the event 
	 */
	public void deleteS(LocalDate d, String s) {
		boolean deleted = false;

//		System.out.println("241: " + getOneTimeSize());
		for (int a = getOneTimeSize() - 1; a >= 0; a--) {
			Event curr = getOneTimeArray().get(a);
			if (curr.getDate().equals(d) && curr.getName().equals(s)) {
				deleteOneTime(a);
				deleted = true;
			}
		}

		for (int b = getSize() - 1; b >= 0; b--) {
			Event curr = getArray().get(b);
			if (curr.getDate().equals(d) && curr.getName().equals(s)) {
				delete(b);
			}
		}

		if (deleted == false)
			System.out.println("No such event was found");
		else
			System.out.println("Delete successful!");

	}

	/**
	 * Deletes all one time events of a particular date  printing the if it was successful or not
	 * @param d - a LocalDate of the event
	 */
	public void deleteA(LocalDate d) {
		boolean deleted = false;
		for (int a = getOneTimeSize() - 1; a >= 0; a--) {
			Event curr = getOneTimeArray().get(a);
			
			if (curr.getDate().equals(d)) {
				deleteOneTime(a);
				deleted = true;
			}
		}

		for (int b = getSize() - 1; b >= 0; b--) {
			Event curr = getArray().get(b);
			if (curr.getDate().equals(d)) {
				delete(b);
			}
		}

		if (deleted == false)
			System.out.println("No such event was found");
		else
			System.out.println("Delete successful!");

	}

	/**
	 * Deletes a recurring event of a particular name, printing the if it was successful or not
	 * @param s - an event name string
	 */
	public void deleteDR(String s) {
		boolean deleted = false;
		for (int a = getRecurringSize() - 1; a >= 0; a--) {
			Event curr = getRecurringArray().get(a);
			if (curr.getName().equals(s)) {
				deleteRecurring(a);
				deleted = true;

			}
		}

		for (int b = getSize() - 1; b >= 0; b--) {
			Event curr = getArray().get(b);
			if (curr.getName().equals(s)) {
				delete(b);

			}
		}

		if (deleted == false)
			System.out.println("No such event was found");
		else
			System.out.println("Delete successful!");

	}

}
