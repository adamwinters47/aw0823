package com.winters.tool.rental.services;

import com.winters.tool.rental.util.RentalUtil;
import com.winters.tool.rental.data.RentalAgreement;
import com.winters.tool.rental.data.RentalRequest;
import com.winters.tool.rental.data.Tool;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RentalServiceTest {

    RentalService rentalService = new RentalService();

    // Test 1
    @Test
    void testGreaterThan100PercentDiscountFailure() {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.SEPTEMBER, 3).getTime();
        String toolCode = "JAKR";
        int numDaysToRent = 6;
        int discountPercent = 101;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        try {
            rentalService.checkout(req);
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains("Discount percentage must be between 0 and 100. Request had a value of " + discountPercent)
            );
        }
    }

    // Test 2
    @Test
    void testLadderThreeDayRentalTenPercentDiscountCheckout() throws Exception {
        Date checkoutDate = new GregorianCalendar(2020, Calendar.JULY, 2).getTime();
        String toolCode = "LADW";
        int numDaysToRent = 3;
        int discountPercent = 10;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);
        RentalAgreement agreement = rentalService.checkout(req);
        int expectedChargeDays = 2;
        Tool expectedTool = Tool.builder()
                .type(Tool.Type.LADDER)
                .brand(Tool.Brand.WERNER)
                .build();
        assertRentalAgreement(req, agreement, expectedTool, expectedChargeDays);
    }

    //Test 3
    @Test
    void testChainsawFiveDayRentalTwentyFivePercentDiscountCheckout() throws Exception {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.JULY, 2).getTime();
        String toolCode = "CHNS";
        int numDaysToRent = 5;
        int discountPercent = 25;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);
        RentalAgreement agreement = rentalService.checkout(req);

        int expectedChargeDays = 3;
        Tool expectedTool = Tool.builder()
                .type(Tool.Type.CHAINSAW)
                .brand(Tool.Brand.STIHL)
                .build();
        assertRentalAgreement(req, agreement, expectedTool, expectedChargeDays);
    }

    // Test 4
    @Test
    void testJackhammerSixDayRentalCheckout() throws Exception {
        Date checkoutDate = new GregorianCalendar(2020, Calendar.SEPTEMBER, 15).getTime();
        String toolCode = "JAKD";
        int numDaysToRent = 6;
        int discountPercent = 0;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        RentalAgreement agreement = rentalService.checkout(req);
        int expectedChargeDays = 4;
        Tool expectedTool = Tool.builder()
                .type(Tool.Type.JACKHAMMER)
                .brand(Tool.Brand.DEWALT)
                .build();
        assertRentalAgreement(req, agreement, expectedTool, expectedChargeDays);
    }

    // Test 5
    @Test
    void testJackhammerNineDayRentalCheckout() throws Exception {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.JULY, 2).getTime();
        String toolCode = "JAKR";
        int numDaysToRent = 9;
        int discountPercent = 0;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        RentalAgreement agreement = rentalService.checkout(req);

        int expectedChargeDays = 5;
        Tool expectedTool = Tool.builder()
                .type(Tool.Type.JACKHAMMER)
                .brand(Tool.Brand.RIDGID)
                .build();
        assertRentalAgreement(req, agreement, expectedTool, expectedChargeDays);
    }

    // Test 6
    @Test
    void testJackhammerFourDayWithFiftyPercentDiscountRentalCheckout() throws Exception {
        Date checkoutDate = new GregorianCalendar(2020, Calendar.JULY, 2).getTime();
        String toolCode = "JAKR";
        int numDaysToRent = 4;
        int discountPercent = 50;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        RentalAgreement agreement = rentalService.checkout(req);

        int expectedChargeDays = 1;
        Tool expectedTool = Tool.builder()
                .type(Tool.Type.JACKHAMMER)
                .brand(Tool.Brand.RIDGID)
                .build();
        assertRentalAgreement(req, agreement, expectedTool, expectedChargeDays);
    }

    @Test
    void testInvalidNumDaysToRentFailure() {
        Date checkoutDate = new GregorianCalendar(2020, Calendar.JULY, 2).getTime();
        String toolCode = "JAKR";
        int numDaysToRent = -5;
        int discountPercent = 50;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        try {
            rentalService.checkout(req);
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains("Number of rental days must be at least one. Request had a value of " + numDaysToRent)
            );
        }
    }

    @Test
    void testDiscountPercentageBelowZeroFailure() {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.SEPTEMBER, 3).getTime();
        String toolCode = "JAKR";
        int numDaysToRent = 6;
        int discountPercent = -5;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        try {
            rentalService.checkout(req);
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains("Discount percentage must be between 0 and 100. Request had a value of " + discountPercent)
            );
        }
    }

    @Test
    void testTooLongToolCodeFailure() {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.SEPTEMBER, 3).getTime();
        String toolCode = "BADTOOLCODE";
        int numDaysToRent = 6;
        int discountPercent = 0;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        try {
            rentalService.checkout(req);
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains("Tool Codes should only be 4 characters long (ex: CHNS). Request had a value of " + toolCode)
            );
        }
    }

    @Test
    void testInvalidToolType() {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.SEPTEMBER, 3).getTime();
        // Invalid tool type, but valid "brand"
        String toolCode = "SCRD";
        int numDaysToRent = 6;
        int discountPercent = 0;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        try {
            rentalService.checkout(req);
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains("No Tool Type found for requested type. Ensure the Tool Code requested has a valid tool type for the first three characters. Request had a value of " + toolCode)
            );
        }
    }

    @Test
    void testInvalidToolBrand() {
        Date checkoutDate = new GregorianCalendar(2015, Calendar.SEPTEMBER, 3).getTime();
        // Valid tool type, but invalid "brand"
        String toolCode = "CHSP";
        int numDaysToRent = 6;
        int discountPercent = 0;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);

        try {
            rentalService.checkout(req);
        } catch (Exception e) {
            assertTrue(
                    e.getMessage().contains("No Brand type found for the requested type. Ensure the Tool Code requested has a valid brand code for the last character. Request had a value of " + toolCode)
            );
        }
    }

    @Test
    void testMultipleYearRentalChargesEachInstanceOfHoliday() throws Exception {
        Date checkoutDate = new GregorianCalendar(2020, Calendar.JULY, 2).getTime();
        String toolCode = "LADW";
        int numDaysToRent = 700;
        int discountPercent = 10;
        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);
        RentalAgreement agreement = rentalService.checkout(req);
        // 700 Days puts us at two years total, so we should have two discounted labor days & two discounted Independence days
        assertEquals(4, agreement.getDiscountDays());
    }

    @Test
    void testNoWeekdayChargeRental() throws Exception {
        Date checkoutDate = new GregorianCalendar(2023, Calendar.AUGUST, 21).getTime();
        String toolCode = "WEEW";
        int numDaysToRent = 9;
        int discountPercent = 0;

        RentalRequest req = assembleRentalRequest(checkoutDate, toolCode, numDaysToRent, discountPercent);
        RentalAgreement agreement = rentalService.checkout(req);

        // We are starting on a Monday & continuing through to the following mid-week. We should only be charged for Saturday & Sunday
        assertEquals(2, agreement.getChargeDays());
    }

    private void assertRentalAgreement(RentalRequest req, RentalAgreement agreement, Tool expectedTool, int expectedChargeDays) {
        Tool agreementTool = agreement.getTool();
        BigDecimal expectedDailyCharge = expectedTool.getType().getDailyCharge();

        assertEquals(req.getToolCode(), agreementTool.getCode());
        assertEquals(expectedTool.getType(), agreementTool.getType());
        assertEquals(expectedTool.getBrand(), agreementTool.getBrand());
        assertEquals(expectedDailyCharge, agreement.getDailyRentalCharge());
        assertEquals(req.getNumDaysToRent(), agreement.getNumDaysRented());
        assertEquals(req.getCheckoutDate(), agreement.getCheckOutDate());

        Calendar c = RentalUtil.convertDateToCalendar(req.getCheckoutDate());
        c.add(Calendar.DATE, req.getNumDaysToRent());
        Date derivedDueDate = c.getTime();

        assertEquals(derivedDueDate, agreement.getDueDate());

        assertEquals(expectedChargeDays, agreement.getChargeDays());
        assertEquals(req.getDiscountPercent(), agreement.getDiscountPercent());

        BigDecimal expectedPreDiscountCharge = expectedDailyCharge.multiply(BigDecimal.valueOf(expectedChargeDays)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedDiscountPercentageAsDecimal = BigDecimal.valueOf(req.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal expectedDiscountAmount = expectedPreDiscountCharge.multiply(expectedDiscountPercentageAsDecimal).setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedDiscountAmount, agreement.getDiscountAmount().setScale(2, RoundingMode.HALF_UP));
        BigDecimal expectedCharge = expectedDailyCharge.multiply(new BigDecimal(expectedChargeDays)).setScale(2, RoundingMode.HALF_UP).subtract(expectedDiscountAmount);
        assertEquals(expectedCharge, agreement.getFinalCharge());
    }

    private RentalRequest assembleRentalRequest(Date checkoutDate, String toolCode, int numDaysToRent, int discountPercent) {
        return RentalRequest.builder()
                .checkoutDate(checkoutDate)
                .numDaysToRent(numDaysToRent)
                .discountPercent(discountPercent)
                .toolCode(toolCode)
                .build();
    }
}