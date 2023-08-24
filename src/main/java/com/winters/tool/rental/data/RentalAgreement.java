package com.winters.tool.rental.data;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Builder
public @Data class RentalAgreement {
    Tool tool;
    // Total number of days the tool is being rented - should always be at least one
    int numDaysRented;
    // The date in which the rental is beginning. This day is not charged to the renter.
    Date checkOutDate;
    // The date in which the rental is due to be returned. A rental is not late until we have reached at least one day AFTER this due date
    Date dueDate;
    // Daily cost of the piece of equipment being rented
    BigDecimal dailyRentalCharge;
    // Count of chargeable days, from day after checkout through & including due date, excluding "no charge" days as specified by the tool type
    int chargeDays;
    // Gross rental amount before discounts
    BigDecimal preDiscountCharge;
    // Discount amount represented as a whole number - i.e. 20 = 20% discount
    int discountPercent;
    // Total discount amount represented in dollars. Rounded half up to nearest penny
    BigDecimal discountAmount;
    // preDiscountCharge - discountAmount = finalCharge
    BigDecimal finalCharge;

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        String toolName = tool.getType().getFullName();
        String formattedToolName = toolName.substring(0, 1).toUpperCase() + toolName.substring(1).toLowerCase();
        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return "Tool Data: " + System.lineSeparator()
                + "\tType: " + formattedToolName + ", Brand: " + tool.getBrand() + ", Code: " + tool.getCode() + System.lineSeparator()
                + "Rental data: " + System.lineSeparator()
                + "\tRental Days: " + getNumDaysRented() + ", Checkout Date: " + dateFormat.format(getCheckOutDate()) + ", Return Date: " + dateFormat.format(getDueDate()) + System.lineSeparator()
                + "\tDaily Charge: " + usdFormat.format(getDailyRentalCharge()) + ", Days Charged: " + getChargeDays() + System.lineSeparator()
                + "Charge Data: " + System.lineSeparator()
                + "\tPre Discount Total: " + usdFormat.format(getPreDiscountCharge()) + ", Discount Percentage: " + getDiscountPercent() + "%, Discount Amount: " + usdFormat.format(getDiscountAmount()) + System.lineSeparator()
                + "__________________________________________________________" + System.lineSeparator()
                + "Total: " + usdFormat.format(getFinalCharge());
    }
}
