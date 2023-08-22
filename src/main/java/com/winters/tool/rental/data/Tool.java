package com.winters.tool.rental.data;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
public @Data class Tool {
    Type type;
    Brand brand;

    public enum Type {
        // TODO: Add 3rd thing here for daily charge
        CHAINSAW("CHN", "CHAINSAW", BigDecimal.valueOf(1.49)),
        LADDER("LDR", "LADDER", BigDecimal.valueOf(1.99)),
        JACKHAMMER("JAK", "JACKHAMMER", BigDecimal.valueOf(2.99));

        private final String fullName;
        private final String typeCode;
        private final BigDecimal dailyCharge;

        Type(String typeCode, String fullName, BigDecimal dailyCharge) {
            this.typeCode = typeCode;
            this.fullName = fullName;
            this.dailyCharge = dailyCharge;
        }

        public String getTypeCode() {
            return typeCode;
        }
        public String getFullName() {
            return fullName;
        }
        public BigDecimal getDailyCharge() {
            return dailyCharge;
        }
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
