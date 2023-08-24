package com.winters.tool.rental.util;

import com.winters.tool.rental.data.Tool;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RentalUtilTest {

    // All tests are starting on Monday, July 3rd through Sunday, July 9th of 2023
    private final Date startDate = new GregorianCalendar(2023, Calendar.JULY, 3).getTime();
    private final Date endDate = new GregorianCalendar(2023, Calendar.JULY, 9).getTime();

    @Test
    void testCalculateNumHolidays() {
        int numHolidays = RentalUtil.calculateNumHolidays(startDate, endDate);
        // Week of fourth of july with the Holiday on Tuesday should give us one holiday
        assertEquals(1, numHolidays);
    }

    @Test
    void testCalculateZeroNumHolidaysObservedOnWeekend() {
        Date weekendHolidayStartDate = new GregorianCalendar(2021, Calendar.JULY, 3).getTime();
        Date weekendHolidayEndDate = new GregorianCalendar(2021, Calendar.JULY, 4).getTime();

        int numHolidays = RentalUtil.calculateNumHolidays(weekendHolidayStartDate, weekendHolidayEndDate);
        // Fourth of July is on a Sunday, so the Observed day should be Monday, the 5th. We should have no holidays on the weekend here
        assertEquals(0, numHolidays);
    }

    @Test
    void testCalculateNumHolidaysObservedOnWeekend() {
        Date weekendHolidayStartDate = new GregorianCalendar(2021, Calendar.JULY, 5).getTime();
        Date weekendHolidayEndDate = new GregorianCalendar(2021, Calendar.JULY, 6).getTime();
        int numHolidays = RentalUtil.calculateNumHolidays(weekendHolidayStartDate, weekendHolidayEndDate);
        // Fourth of July is on a Sunday, so the Observed day should be Monday, the 5th. We should have the single one on Monday, despite the date not being the 4th
        assertEquals(1, numHolidays);
    }

    @Test
    void testCalculateNumWeekendDays() {
        int numWeekendDays = RentalUtil.calculateNumWeekendDays(startDate, endDate);
        assertEquals(2, numWeekendDays);
    }

    @Test
    void testCalculateNumWeekDays() {
        int numWeekdays = RentalUtil.calculateNumWeekDays(startDate, endDate);
        assertEquals(5, numWeekdays);
    }

    @Test
    void testAssembleToolFromToolCode() {
        String toolCode = "CHNS";
        Tool tool = RentalUtil.assembleToolFromToolCode(toolCode);

        assertEquals(Tool.Type.CHAINSAW, tool.getType());
        assertEquals(Tool.Brand.STIHL, tool.getBrand());
    }

}