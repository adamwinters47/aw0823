package com.winters.tool.rental.services;

import com.winters.tool.rental.data.RentalAgreement;
import com.winters.tool.rental.data.RentalRequest;
import com.winters.tool.rental.data.Tool;
import com.winters.tool.rental.data.ToolCodeInfo;
import org.springframework.stereotype.Service;

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
        if(!validationErrors.isEmpty()) {
            throw new Exception("Rental Request is not valid. Please fix the following errors: " + String.join(", ", validationErrors));
        }
        // Check for holidays

        //Generate Agreement
        Tool rentedTool = assembleToolFromToolCode(req.getToolCode());

        return RentalAgreement.builder()
                .tool(rentedTool)
                .numDaysRented(req.getNumDaysToRent())
                .checkOutDate(req.getCheckoutDate())
                .dueDate(deriveRentalDueDate(req.getCheckoutDate(), req.getNumDaysToRent()))
                .dailyRentalCharge(rentedTool.getType().getDailyCharge())
                .build();
    }

    private Date deriveRentalDueDate(Date checkoutDate, int numDaysToRent) {
        Calendar c = Calendar.getInstance();
        c.setTime(checkoutDate);
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
