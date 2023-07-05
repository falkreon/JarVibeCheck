package blue.endless.jarvibecheck.impl;

import java.time.LocalDate;
import java.time.LocalTime;

public class DosDateTime {
	public static LocalDate resolveDate(int value) {
		int day = value & 0x1F;
		int month = (value >>> 5) & 0xF;
		int year = (value >>> 9) & 0x7F;
		
		return LocalDate.of(1980+year, month, day);
	}
	
	public static LocalTime resolveTime(int value) {
		int secondDiv2 = value & 0x1F;
		int minute = (value >>> 5) & 0x3F;
		int hour = (value >>> 11) & 0x1F;
		
		return LocalTime.of(hour, minute, secondDiv2 * 2);
	}
}
