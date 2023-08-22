package com.winters.tool.rental.services;

import com.winters.tool.rental.data.RentalAgreement;
import com.winters.tool.rental.data.RentalRequest;
import com.winters.tool.rental.data.Tool;
import com.winters.tool.rental.data.ToolCodeInfo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.springframework.aot.hint.TypeReference.listOf;

@Service
public class RentalService {

    public RentalAgreement checkout(RentalRequest req) throws Exception {

        // Validate Rental info
        List<String> validationErrors = validateRentalRequest(req);
        // if list is not empty, throw exception with info
        if(!validationErrors.isEmpty()) {
            throw new Exception("Rental Request is not valid. Please fix the following errors: " + String.join(", ", validationErrors));
        }
        Tool rentedTool = assembleToolFromToolCode(req.getToolCode());
        Date dueDate = deriveRentalDueDate(req.getCheckoutDate(), req.getNumDaysToRent());
        int chargeDays = deriveChargeDays(req.getCheckoutDate(), dueDate, req.getNumDaysToRent());
        BigDecimal dailyCharge = rentedTool.getType().getDailyCharge();
        BigDecimal preDiscountCharge = dailyCharge.multiply(BigDecimal.valueOf(chargeDays)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountPercentageAsDecimal = BigDecimal.valueOf(req.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = preDiscountCharge.multiply(discountPercentageAsDecimal).setScale(2, RoundingMode.HALF_UP);
        return RentalAgreement.builder()
                .tool(rentedTool)
                .numDaysRented(req.getNumDaysToRent())
                .checkOutDate(req.getCheckoutDate())
                .dueDate(dueDate)
                .dailyRentalCharge(dailyCharge)
                .chargeDays(chargeDays)
                .preDiscountCharge(preDiscountCharge)
                .discountPercent(req.getDiscountPercent())
                .discountAmount(discountAmount)
                .finalCharge(preDiscountCharge.subtract(discountAmount))
                .build();
    }

    /**
     *  Calculates the number of days we will be charging the rental fee, because we do not charge on holidays
     * @param checkoutDate
     * @param dueDate
     * @param numDaysToRent
     * @return
     */
    private int deriveChargeDays(Date checkoutDate, Date dueDate, int numDaysToRent) {
        Calendar checkoutDateCalendar = convertDateToCalendar(checkoutDate);
        Date observedFourthOfJuly = calculateObservedIndependenceDayForYear(checkoutDateCalendar.get(Calendar.YEAR));
        Date laborDay = calculateLaborDayForYear(checkoutDateCalendar.get(Calendar.YEAR));

        int totalChargeDays = numDaysToRent;
        // TODO: What if rental period is between two years / longer than one year?

        if(isDateInRange(observedFourthOfJuly, checkoutDate, dueDate)) {
            totalChargeDays--;
        }
        if(isDateInRange(laborDay, checkoutDate, dueDate)) {
            totalChargeDays--;
        }

        return totalChargeDays;
    }

    private boolean isDateInRange(Date dateToCheck, Date startDate, Date endDate) {
        return dateToCheck.after(startDate) && dateToCheck.before(endDate);
    }
    private Date calculateLaborDayForYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        // Setting calendar to September, but Calendar.MONTH is zero-indexed, so 8 instead of 9
        c.set(Calendar.MONTH, 8);
        c.set(Calendar.YEAR, year);
        return c.getTime();
    }

    /**
     * Because 4th of July can fall on the weekend, we only want the actual observed date of the holiday.
     * If the holiday is on Saturday, Friday becomes the observed date. If it's on Sunday, Monday becomes the observed date.
     * @param  year The year in which we are checking
     * @return Date that indicates the observed holiday
     */
    private Date calculateObservedIndependenceDayForYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        // Setting calendar to July, but Calendar.MONTH is zero-indexed, so 6 instead of 7
        c.set(Calendar.MONTH, 6);
        // But for some reason DATE is not zero-indexed, so we are staying at 4 here
        c.set(Calendar.DATE, 4);
        if(c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DATE, 1);
        } else if (c.get(Calendar.DAY_OF_WEEK) ==  Calendar.SATURDAY) {
            c.add(Calendar.DATE, -1);
        }
        return c.getTime();
    }

    private Calendar convertDateToCalendar(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    private Date deriveRentalDueDate(Date checkoutDate, int numDaysToRent) {
        Calendar c = convertDateToCalendar(checkoutDate);
        c.add(Calendar.DATE, numDaysToRent);
        return c.getTime();
    }

    private Tool assembleToolFromToolCode(String toolCode) {
        ToolCodeInfo toolCodeInfo = extractToolCodeInfoFromToolCode(toolCode);
        return Tool.builder()
                .brand(toolCodeInfo.getBrand())
                .type(toolCodeInfo.getType())
                .build();
    }

    /**
     * We need to be sure that the data being passed into the checkout function is valid. But we don't just want to display
     * one error at a time until the requesting person finally gets the request correct. We are going to assemble a list
     * of validation reasons and display them in one go to avoid having to make multiple requests with only one issue
     * fixed each time.
     * @param req A rental Request that contains the data to be validated
     * @return    The list of errors that ocurred when attempting to validate the request data
     */
    private List<String> validateRentalRequest(RentalRequest req) {
        List<String> validationErrors = new ArrayList<>();
        if (req.getNumDaysToRent() < 1) {
            validationErrors.add("Number of rental days must be ast least one. Request had a value of " + req.getNumDaysToRent());
        }
        if (req.getDiscountPercent() < 0 || req.getDiscountPercent() > 100) {
            validationErrors.add("Discount percentage must be between 0 and 100. Request had a value of " + req.getDiscountPercent());
        }
        if (req.getToolCode().length() != 4) {
            validationErrors.add("Tool Codes should only be 4 characters long (ex: CHNS). Request had a value of " + req.getToolCode());
        } else {
            ToolCodeInfo toolCodeInfo = extractToolCodeInfoFromToolCode(req.getToolCode());

            if(toolCodeInfo.getType() == null) {
                validationErrors.add("No Tool Type found for requested type. Ensure the Tool Code requested has a valid tool type for the first three characters. Request had a value of " + req.getToolCode());
            }
            if(toolCodeInfo.getBrand() == null) {
                validationErrors.add("No Brand type found for the requested type. Ensure the Tool Code requested has a valid brand code for the last character. Request had a value of " + req.getToolCode());
            }
        }
        return validationErrors;
    }

    private ToolCodeInfo extractToolCodeInfoFromToolCode(String toolCode) {
        String toolType = toolCode.substring(0, 2);
        char toolBrand = toolCode.charAt(toolCode.length() - 1);
        return ToolCodeInfo.builder()
                .toolType(toolType)
                .type(Tool.Type.findByTypeCode(toolType))
                .toolBrand(toolBrand)
                .brand(Tool.Brand.findByBrandCode(toolBrand))
                .build();
    }
}
