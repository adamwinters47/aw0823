package com.winters.tool.rental.data;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

public @Data class RentalAgreement {
    Tool tool;
    int numDaysRented;
    Date checkOutDate;
    // The date in which the rental is due to be returned. A rental is not late until we have reached at least one day AFTER this due date
    Date dueDate;
    BigDecimal dailyRentalCharge;
    // A total number of the days when a charge is eligible - If there is a 5-day rental, but one day is a holiday, this value would be 4
    int chargeDays;
    // Gross rental amount before discounts
    BigDecimal preDiscountCharge;
    // Discount amount represented as decimal - i.e. 0.25 = 25% discount
    BigDecimal discountPercent;
    // Total discount amount represented in dollars. Rounded half up to nearest penny
    BigDecimal discountAmount;
    // preDiscountCharge - discountAmount
    BigDecimal finalCharge;
}
