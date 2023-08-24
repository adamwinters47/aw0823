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