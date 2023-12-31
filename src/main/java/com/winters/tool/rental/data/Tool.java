package com.winters.tool.rental.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
public @Data class Tool {
    Type type;
    Brand brand;

    @AllArgsConstructor
    @Getter
    public enum Type {
        CHAINSAW("CHN", "CHAINSAW", BigDecimal.valueOf(1.49), true, false, true),
        LADDER("LAD", "LADDER", BigDecimal.valueOf(1.99), true, true, false),
        JACKHAMMER("JAK", "JACKHAMMER", BigDecimal.valueOf(2.99), true, false, false),
        // Test tool for validating isChargedOnWeekdays = false behavior, costs about tree fiddy
        WEEKDAY_EXEMPT("WEE", "WEEKDAYEXEMPT", BigDecimal.valueOf(3.50), false, true, true);

        private final String typeCode;
        private final String fullName;
        private final BigDecimal dailyCharge;
        private final boolean isChargedOnWeekdays;
        private final boolean isChargedOnWeekends;
        private final boolean isChargedOnHolidays;

        public static Type findByTypeCode(String typeCode) {
            Type result = null;
            for(Type type: values()) {
                if(type.getTypeCode().equalsIgnoreCase(typeCode)) {
                    result = type;
                    break;
                }
            }
            return result;
        }
    }

    public enum Brand {
        STIHL,
        WERNER,
        DEWALT,
        RIDGID;

        public static Brand findByBrandCode(char brandCode) {
            Brand result = null;
            for(Brand brand : values()) {
                if(brand.name().charAt(0) == brandCode) {
                    result = brand;
                    break;
                }
            }
            return result;
        }
    }

    /**
     * The overall rental code is a 4 character code derived from the type of tool being rented plus the brand of the tool
     * i.e. A Stihl Chainsaw being rented would have a tool code of "CHNS"
     */
    public String getCode() {
        return this.type.getTypeCode().concat(getBrandCode());
    }

    /**
     * The Brand code is simply the first character from the Tool brand name
     * i.e. Stihl brand code is "S"
     */
    public String getBrandCode() {
        return this.brand.name().toUpperCase().substring(0, 1);
    }

}
