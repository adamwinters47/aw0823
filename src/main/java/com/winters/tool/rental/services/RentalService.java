package com.winters.tool.rental.services;

import com.winters.tool.rental.util.RentalUtil;
import com.winters.tool.rental.data.RentalAgreement;
import com.winters.tool.rental.data.RentalRequest;
import com.winters.tool.rental.data.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RentalService {

    public RentalAgreement checkout(RentalRequest req) throws Exception {

        // Validate Rental info
        List<String> validationErrors = validateRentalRequest(req);
        // if list is not empty, throw exception with info
        if (!validationErrors.isEmpty()) {
            throw new Exception("Rental Request is not valid. Please fix the following errors: " + String.join(", ", validationErrors));
        }
        Tool rentedTool = RentalUtil.assembleToolFromToolCode(req.getToolCode());
        Date dueDate = deriveRentalDueDate(req.getCheckoutDate(), req.getNumDaysToRent());

        Tool.Type toolType = rentedTool.getType();
        BigDecimal dailyCharge = toolType.getDailyCharge();
        int totalDiscountDays = calculateNumDiscountDays(toolType, req.getCheckoutDate(), dueDate);
        // In situations where a rental is has multiple days that do not qualify for a charge, we want to ensure we charge for at least the one day
        int chargeDays = Math.max(1, req.getNumDaysToRent() - totalDiscountDays);
        BigDecimal preDiscountCharge = dailyCharge.multiply(BigDecimal.valueOf(chargeDays)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountPercentageAsDecimal = BigDecimal.valueOf(req.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal discountAmount = preDiscountCharge.multiply(discountPercentageAsDecimal).setScale(2, RoundingMode.HALF_UP);

        RentalAgreement agreement = RentalAgreement.builder().tool(rentedTool).numDaysRented(req.getNumDaysToRent()).checkOutDate(req.getCheckoutDate()).dueDate(dueDate).dailyRentalCharge(dailyCharge).chargeDays(chargeDays).preDiscountCharge(preDiscountCharge).discountPercent(req.getDiscountPercent()).discountAmount(discountAmount).finalCharge(preDiscountCharge.subtract(discountAmount)).build();

        System.out.println(agreement);

        return agreement;
    }

    private int calculateNumDiscountDays(Tool.Type toolType, Date checkoutDate, Date dueDate) {
        Calendar firstChargeDay = RentalUtil.convertDateToCalendar(checkoutDate);
        firstChargeDay.add(Calendar.DATE, 1);
        Date firstChargeDate = firstChargeDay.getTime();
        int numHolidaysToDiscount = toolType.isChargedOnHolidays() ? 0 : RentalUtil.calculateNumHolidays(firstChargeDate, dueDate);
        int numWeekendDaysToDiscount = toolType.isChargedOnWeekends() ? 0 : RentalUtil.calculateNumWeekendDays(firstChargeDate, dueDate);
        int numWeekDaysToDiscount = toolType.isChargedOnWeekdays() ? 0 : RentalUtil.calculateNumWeekDays(firstChargeDate, dueDate);
        return numHolidaysToDiscount + numWeekendDaysToDiscount + numWeekDaysToDiscount;
    }

    private Date deriveRentalDueDate(Date checkoutDate, int numDaysToRent) {
        Calendar c = RentalUtil.convertDateToCalendar(checkoutDate);
        c.add(Calendar.DATE, numDaysToRent);
        return c.getTime();
    }

    /**
     * We need to be sure that the data being passed into the checkout function is valid. But we don't just want to display
     * one error at a time until the requesting person finally gets the request correct. We are going to assemble a list
     * of validation reasons and display them in one go to avoid having to make multiple requests with only one issue
     * fixed each time.
     *
     * @param req A rental Request that contains the data to be validated
     * @return The list of errors that ocurred when attempting to validate the request data
     */
    private List<String> validateRentalRequest(RentalRequest req) {
        List<String> validationErrors = new ArrayList<>();
        if (req.getNumDaysToRent() < 1) {
            validationErrors.add("Number of rental days must be at least one. Request had a value of " + req.getNumDaysToRent());
        }
        if (req.getDiscountPercent() < 0 || req.getDiscountPercent() > 100) {
            validationErrors.add("Discount percentage must be between 0 and 100. Request had a value of " + req.getDiscountPercent());
        }
        if (req.getToolCode().length() != 4) {
            validationErrors.add("Tool Codes should only be 4 characters long (ex: CHNS). Request had a value of " + req.getToolCode());
        } else {
            Tool tool = RentalUtil.assembleToolFromToolCode(req.getToolCode());

            if (tool.getType() == null) {
                validationErrors.add("No Tool Type found for requested type. Ensure the Tool Code requested has a valid tool type for the first three characters. Request had a value of " + req.getToolCode());
            }
            if (tool.getBrand() == null) {
                validationErrors.add("No Brand type found for the requested type. Ensure the Tool Code requested has a valid brand code for the last character. Request had a value of " + req.getToolCode());
            }
        }
        return validationErrors;
    }

}
