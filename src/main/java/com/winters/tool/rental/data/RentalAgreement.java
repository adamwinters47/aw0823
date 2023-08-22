package com.winters.tool.rental.data;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public @Data class RentalAgreement {
    Tool tool;
    // Total number of days the tool is being rented - should always be at least one
    int numDaysRented;
    Date checkOutDate;
    // The date in which the rental is due to be returned. A rental is not late until we have reached at least one day AFTER this due date
    Date dueDate;
    double dailyRentalCharge;
    // A total number of the days when a charge is eligible - If there is a 5-day rental, but one day is a holiday, this value would be 4
    int chargeDays;
    // Gross rental amount before discounts
    BigDecimal preDiscountCharge;
    // Discount amount represented as decimal - i.e. 0.25 = 25% discount
    BigDecimal discountPercent;
    // Total discount amount represented in dollars. Rounded half up to nearest penny
    BigDecimal discountAmount;
    // preDiscountCharge - discountAmount = finalCharge
    BigDecimal finalCharge;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tool Data: " + System.lineSeparator()
                + "Type: " + tool.getType().getFullName() + ", Brand: " + tool.getBrand() + ", Code: " + tool.getCode() + System.lineSeparator()
                + "Rental data: " + System.lineSeparator()
                + "Rental Days: " + getNumDaysRented() + ", Checkout Date: " + getCheckOutDate() + ", Return Date: " + getDueDate() + System.lineSeparator()
                + "Daily Charge: " + tool.getType().getDailyCharge() + ", Days Charged: " + getChargeDays() + System.lineSeparator()
                + "Charge Data: " + System.lineSeparator()
                + "Pre Discount Total: " + getPreDiscountCharge() + ", Discount Percentage: " + getDiscountPercent() + ", Discount Amount: " + getDiscountAmount() + System.lineSeparator()
                + "__________________________________________________________" + System.lineSeparator()
                + "Total: " + getFinalCharge());
        return "Tool Data: " + System.lineSeparator()
                + "\tType: " + tool.getType() + ", Brand: " + tool.getBrand() + ", Code: " + tool.getCode() + System.lineSeparator()
                + "Rental data: " + System.lineSeparator()
                + "\tRental Days: " + getNumDaysRented() + ", Checkout Date: " + getCheckOutDate() + ", Return Date: " + getDueDate() + System.lineSeparator()
                + "\tDaily Charge: " + getDailyRentalCharge() + ", Days Charged: " + getChargeDays() + System.lineSeparator()
                + "Charge Data: " + System.lineSeparator()
                + "\tPre Discount Total: " + getPreDiscountCharge() + ", Discount Percentage: " + getDiscountPercent() + ", Discount Amount: " + getDiscountAmount() + System.lineSeparator()
                + "__________________________________________________________" + System.lineSeparator()
                + "Total: " + getFinalCharge();
    }
}
