package com.winters.tool.rental.util;

import com.winters.tool.rental.data.Tool;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public final class RentalUtil {

    private RentalUtil() {
    }

    public static Calendar convertDateToCalendar(Date dateToConvert) {
        Calendar c = Calendar.getInstance();
        c.setTime(dateToConvert);
        return c;
    }

    public static LocalDate convertDateToLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * In the case where a renter is going to be renting on the extreme long term (greater than a year),
     * we want to make sure that the holiday discount is given on each instance of the present holiday,
     * not just the first or last
     *
     * @param firstChargeDate - The first possible date we will charge the renter
     * @param dueDate         - Date the item is due back, which is a normally charged day
     * @return total number of days that meet the given criteria
     */
    public static int calculateNumHolidays(Date firstChargeDate, Date dueDate) {
        Calendar checkoutDateCalendar = RentalUtil.convertDateToCalendar(firstChargeDate);
        Calendar dueDateCalendar = RentalUtil.convertDateToCalendar(dueDate);

        int startingYear = checkoutDateCalendar.get(Calendar.YEAR);
        int endingYear = dueDateCalendar.get(Calendar.YEAR);
        AtomicInteger numHolidays = new AtomicInteger(0);

        IntStream.rangeClosed(startingYear, endingYear).forEach(year -> {
            Date observedFourthOfJuly = calculateObservedIndependenceDayForYear(year);
            Date laborDay = calculateLaborDayForYear(year);
            if (isDateInRange(observedFourthOfJuly, firstChargeDate, dueDate)) {
                numHolidays.getAndIncrement();
            }
            if (isDateInRange(laborDay, firstChargeDate, dueDate)) {
                numHolidays.getAndIncrement();
            }
        });

        return numHolidays.get();
    }

    /**
     * Because 4th of July can fall on the weekend, we only want the actual observed date of the holiday.
     * If the holiday is on Saturday, Friday becomes the observed date. If it's on Sunday, Monday becomes the observed date.
     *
     * @param year - The year in which we are checking
     * @return Date that indicates the observed holiday for the given year
     */
    private static Date calculateObservedIndependenceDayForYear(int year) {
        GregorianCalendar c = new GregorianCalendar(year, Calendar.JULY, 4);

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DATE, 1);
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            c.add(Calendar.DATE, -1);
        }
        return c.getTime();
    }

    /**
     * Labor day is not on a set day. Instead, it is the first Monday of each September. The Calendar class has
     * an easy-to-use API to calculate the first Monday of a given month + year, so we'll use that here.
     *
     * @param year - the year in which we are calculating the holiday
     * @return Date that indicates labor day for the given year
     */
    private static Date calculateLaborDayForYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        // Setting calendar to September, but Calendar.MONTH is zero-indexed, so 8 instead of 9
        c.set(Calendar.MONTH, 8);
        c.set(Calendar.YEAR, year);
        return c.getTime();
    }

    private static boolean isDateInRange(Date dateToCheck, Date startDate, Date endDate) {
        return (dateToCheck.after(startDate) && dateToCheck.before(endDate)) || dateToCheck.equals(startDate) || dateToCheck.equals(endDate);
    }

    /**
     * @param firstChargeDate - The first possible date we will charge the renter
     * @param dueDate         - Date the item is due back, which is a normally charged day
     * @return total number of days that meet the given criteria
     */
    public static int calculateNumWeekendDays(Date firstChargeDate, Date dueDate) {
        AtomicInteger numWeekendDays = new AtomicInteger();
        LocalDate start = RentalUtil.convertDateToLocalDate(firstChargeDate);
        LocalDate end = RentalUtil.convertDateToLocalDate(dueDate);
        start.datesUntil(end.plusDays(1)).forEach(day -> {
            if (day.getDayOfWeek() == DayOfWeek.SATURDAY
                    || day.getDayOfWeek() == DayOfWeek.SUNDAY) {
                numWeekendDays.getAndIncrement();
            }
        });
        return numWeekendDays.get();
    }

    /**
     * With the currently present products, we are always charging on weekdays, but a time
     * may come when a new tool is exempt on weekdays, so we already have that in place.
     *
     * @param firstChargeDate - The first possible date we will charge the renter
     * @param dueDate         - Date the item is due back, which is a normally charged day
     * @return total number of days that meet the given criteria
     */
    public static int calculateNumWeekDays(Date firstChargeDate, Date dueDate) {
        AtomicInteger numWeekendDays = new AtomicInteger();
        LocalDate start = RentalUtil.convertDateToLocalDate(firstChargeDate);
        LocalDate end = RentalUtil.convertDateToLocalDate(dueDate);
        start.datesUntil(end.plusDays(1)).forEach(day -> {
            if (day.getDayOfWeek() == DayOfWeek.MONDAY
                    || day.getDayOfWeek() == DayOfWeek.TUESDAY
                    || day.getDayOfWeek() == DayOfWeek.WEDNESDAY
                    || day.getDayOfWeek() == DayOfWeek.THURSDAY
                    || day.getDayOfWeek() == DayOfWeek.FRIDAY
            ) {
                numWeekendDays.getAndIncrement();
            }
        });
        return numWeekendDays.get();
    }

    public static Tool assembleToolFromToolCode(String toolCode) {
        String toolType = toolCode.substring(0, 3);
        char toolBrand = toolCode.charAt(toolCode.length() - 1);
        return Tool.builder()
                .type(Tool.Type.findByTypeCode(toolType))
                .brand(Tool.Brand.findByBrandCode(toolBrand))
                .build();
    }
}
