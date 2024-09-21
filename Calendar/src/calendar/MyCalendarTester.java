package calendar;

/**
* MyCalendarTester
* @author Joyce Liu 
* @version 1.0 9/7/2024
* */

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.FileWriter;

/**
 * MyCalendarTester creates a Calendar object and executes the program depending on user input.
 */
public class MyCalendarTester {
	
	/**
	 * @param args - the command line arguments
	 */
	public static void main(String[] args) {
		// testing to see if the calendar is working
		MyCalendar calendar = new MyCalendar();

		// print the calendar
		LocalDate today = LocalDate.now();
		printTodayCalendar(today);
		System.out.println();
		System.out.println();

		System.out.println("Loading is done!");
		printMenu();

		Scanner scnr = new Scanner(System.in);

		while (scnr.hasNextLine()) {

			String input = scnr.nextLine();

			//main menu features 
			if (input.equals("v")) {
				System.out.println("[D]ay view or [M]view ?");
				String afterV = scnr.nextLine();
				if (afterV.equals("d")) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d yyyy");
					System.out.println(" " + formatter.format(today));

					calendar.printEvents(today);

					System.out.println("[P]revious or [N]ext or [G]o back to the main menu ?");
					afterV = scnr.nextLine();

					while (!afterV.equals("g")) {
						afterV = scnr.nextLine();
						if (afterV.equals("p")) {
							today = today.minusDays(1);
							System.out.println(" " + formatter.format(today));
							calendar.printEvents(today);
						} else if (afterV.equals("n")) {
							today = today.plusDays(1);
							System.out.println(" " + formatter.format(today));
							calendar.printEvents(today);
						} else if (afterV.equals("g")) {
							printMenu();
						}
					}

				} else if (afterV.equals("m")) {
					printEventsMonthCalendar(today, calendar);

					System.out.println("[P]revious or [N]ext or [G]o back to the main menu ?");
					String afterdm = scnr.nextLine();
					System.out.println(afterdm);

					while (!afterdm.equals("g")) {
						afterdm = scnr.nextLine();
						if (afterdm.equals("p")) {
							today = today.minusMonths(1);
							printEventsMonthCalendar(today, calendar);
						} else if (afterdm.equals("n")) {
							today = today.plusMonths(1);
							printEventsMonthCalendar(today, calendar);
						} else if (afterdm.equals("g")) {
							printMenu();
						}
					}

				}

			} else if (input.equals("c")) {

				System.out.println("Name of the event ? ");
				System.out.println("Note: It does not need to be one word");
				String name = scnr.nextLine();

				System.out.println("Date of the event ? ");
				System.out.println("Note: Use (MM/DD/YYYY) format");
				String date = scnr.nextLine();

				String[] cDate = date.split("/");
				LocalDate createdDate = LocalDate.parse(cDate[2] + "-" + cDate[0] + "-" + cDate[1]);

				System.out.println("Starting time of the event ?");
				System.out.println("(Note: Use a 24 hour clock from 00:00 to 23:59 (ex. 06:00 for 6 AM and 15:30 for 3:30 PM))");
				String startTime = scnr.nextLine();
				LocalTime createdStartTime = LocalTime.parse(startTime);

				System.out.println("End time of the event ?");
				System.out.println("(Note: Use a 24 hour clock from 00:00 to 23:59 (ex. 06:00 for 6 AM and 15:30 for 3:30 PM))");
				String endTime = scnr.nextLine();
				LocalTime createdEndTime = LocalTime.parse(endTime);

				TimeInterval createdTime = new TimeInterval(createdStartTime, createdEndTime);

				Event createdEvent = new Event(name, createdDate, createdTime);
				calendar.addEvent(createdEvent);
				

			} else if (input.equals("g")) {
				System.out.println("Please enter a date to look up.");
				System.out.println("Note: Use (MM/DD/YYYY) format");

				String goDate = scnr.nextLine();

				String[] gDate = goDate.split("/");
				LocalDate createdDate = LocalDate.parse(gDate[2] + "-" + gDate[0] + "-" + gDate[1]);

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, MMM d yyyy");
				System.out.println(" " + formatter.format(createdDate));

				calendar.printEvents(createdDate);

			} else if (input.equals("e")) {
				System.out.println("ONE TIME EVENTS");
				calendar.printOneTime();

				System.out.println();
				System.out.println("RECURRING EVENTS");
				calendar.printRecurring();

			} else if (input.equals("d")) {
				System.out.println("Delete [S]elected  [A]ll   [DR]Recurring event ?");

				String dInput = scnr.nextLine();
				if (dInput.equals("s")) {
					System.out.println("Enter the date of the one time event");
					System.out.println("Note: Use (MM/DD/YYYY) format");
					String sDate = scnr.nextLine();
					String[] ssDate = sDate.split("/");
					LocalDate sssDate = LocalDate.parse(ssDate[2] + "-" + ssDate[0] + "-" + ssDate[1]);

					System.out.println("Enter the name of the one time event.");
					String sName = scnr.nextLine();

					calendar.deleteS(sssDate, sName);

				} else if (dInput.equals("a")) {
					System.out.println("Enter the date you want to delete all one-time events from");
					System.out.println("Note: Use (MM/DD/YYYY) format");
					String aDate = scnr.nextLine();
					String[] aaDate = aDate.split("/");
					LocalDate aaaDate = LocalDate.parse(aaDate[2] + "-" + aaDate[0] + "-" + aaDate[1]);

					calendar.deleteA(aaaDate);

				} else if (dInput.equals("dr")) {
					System.out.println("Enter the name of the recurring event you want to delete");
					String drDate = scnr.nextLine();

					calendar.deleteDR(drDate);

				}

			} else if (input.equals("q")) {
				System.out.println("Good Bye");
				System.out.println("output.txt file saved");

				try {
					File outputText = new File("output.txt");
					FileWriter output = new FileWriter(outputText);
					calendar.sortByDate(calendar.getArray());
					calendar.sortByDate(calendar.getOneTimeArray());

//					for (int o = 0; o < calendar.getSize(); o++) {
//						Event curr = calendar.getEvent(o);
//						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//						ArrayList<String> outputted = new ArrayList<String>();
//						String name = curr.getName();
//						if (!outputted.contains(name + curr.getTimeInterval().getStartTime().toString())) {
//							output.write(name + System.getProperty("line.separator"));
//							output.write(formatter.format(curr.getDate()) + " " + curr.getTimeInterval().getStartTime()
//									+ " " + curr.getTimeInterval().getEndTime() + System.getProperty("line.separator"));
//							
//							outputted.add(curr.getName() + curr.getTimeInterval().getStartTime().toString());
//							
//							System.out.println(curr.getName() + curr.getTimeInterval().getStartTime().toString());
//						}
//					}
					
					calendar.sortByName(calendar.getOneTimeStringArray());
					for(String e: calendar.getOneTimeStringArray()) {
						output.write(e + System.getProperty("line.separator"));
					}
					
					calendar.sortByName(calendar.getRecurringStringArray());
					for(String e: calendar.getRecurringStringArray()) {
						output.write(e + System.getProperty("line.separator"));
					}
					

					// TR 09:00 10:15 08/22/2024 12/09/2024
					// 10/03/2024 01:00 02:00
//		            for(Event e: calendar.getOneTimeArray()) {
//		            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//			            output.write(e.getName() + System.getProperty("line.separator"));
//			            output.write(formatter.format(e.getDate()) + " "+ e.getTimeInterval().getStartTime() + " " + e.getTimeInterval().getEndTime() + System.getProperty("line.separator")); 
//	            }

					output.close();

				}

				catch (Exception e) {
					e.getStackTrace();
		}

			} else {
				printMenu();
			}
		}
		scnr.close();

	}
	
	/**
	 * Prints out a monthly calendar with square brackets around today's date
	 * @param d - a variable with type LocalDate
	 */
	public static void printTodayCalendar(LocalDate d) {
		// prints month and year, and all the weekdays
		System.out.println(d.getMonth() + " " + d.getYear());
		System.out.println("Su Mo Tu We Th Fr Sa");

		// figure out which week day the first day of each month is
		LocalDate weekdayOfTheFirst = LocalDate.of(d.getYear(), d.getMonth(), 1);
		int numericalWeekday = weekdayOfTheFirst.getDayOfWeek().getValue();

		// figure out the last day of each month
		int lastDay = d.lengthOfMonth();

		// keep track of the number of days printed to figure out when to start a new
		// week
		int numDaysPrinted = 0;

		// print out spaces if month does not start on that weekday
		// increment numDaysPrinted to maintain calendar organization
		if (numericalWeekday != 7) {
			for (int i = 0; i < numericalWeekday; i++) {
				System.out.print("   ");
				numDaysPrinted++;
			}
		} else {
			numDaysPrinted = 7;
		}

		// print out the days of the month
		for (int j = 1; j <= lastDay; j++) {
			if (numDaysPrinted % 7 == 0 && numDaysPrinted != 7) {
				System.out.println();
			}
			numDaysPrinted++;

			if (j == d.getDayOfMonth()) {
				System.out.print("[" + j + "]");
				continue;
			}

			if (j >= 10)
				System.out.print(j + " ");
			else
				System.out.print(" " + j + " ");

		}

	}
	/**
	 * Prints a monthly calendar with curly brackets around days that have at least one event scheduled
	 * @param d - LocalDate object
	 * @param c - MyCalendar object
	 */
	public static void printEventsMonthCalendar(LocalDate d, MyCalendar c) {
		// prints month and year, and all the weekdays
		System.out.println(d.getMonth() + " " + d.getYear());
		System.out.println("Su Mo Tu We Th Fr Sa");

		// figure out which week day the first day of each month is
		LocalDate weekdayOfTheFirst = LocalDate.of(d.getYear(), d.getMonth(), 1);
		int numericalWeekday = weekdayOfTheFirst.getDayOfWeek().getValue();

		// figure out the last day of each month
		int lastDay = d.lengthOfMonth();

		// keep track of the number of days printed to figure out when to start a new week
		int numDaysPrinted2 = 0;

		// print out spaces if month does not start on that weekday
		// increment numDaysPrinted to maintain calendar organization
		if (numericalWeekday != 7) {
			for (int i = 0; i < numericalWeekday; i++) {
				System.out.print("   ");
				numDaysPrinted2++;
			}
		} else {
			numDaysPrinted2 = 7;
		}

		// print out the days of the month
		for (int j = 1; j <= lastDay; j++) {

			boolean isPrinted = false;

			if (numDaysPrinted2 % 7 == 0) {
				System.out.println();
			}
			numDaysPrinted2++;

			for (int p = 0; p < c.getSize(); p++) {
//				System.out.println(c.getEvent(p).getDate().getMonthValue());
//				System.out.println(d.getMonthValue());
//				System.out.println(c.getEvent(p).getDate().getDayOfMonth());
//				System.out.println(d.getDayOfMonth());

				if (c.getEvent(p).getDate().getMonthValue() == d.getMonthValue()
						&& c.getEvent(p).getDate().getDayOfMonth() == j
						&& c.getEvent(p).getDate().getYear() == d.getYear()) {
					System.out.print("{" + j + "} ");
					isPrinted = true;
					break;
				}
			}

			if (isPrinted == false) {
				if (j >= 10)
					System.out.print(j + " ");
				else
					System.out.print(" " + j + " ");
			}

		}
		System.out.println();
	}
	
	/**
	 * Prints out a string with all the options a user can choose
	 */
	public static void printMenu() {
		System.out.println("Select one of the following main menu options:");
		System.out.println("[V]iew by  [C]reate  [G]o to  [E]vent list  [D]elete  [Q]uit");
	}
	


}
