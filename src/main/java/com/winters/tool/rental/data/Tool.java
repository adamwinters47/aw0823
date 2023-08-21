package com.winters.tool.rental.data;

import lombok.Data;

public @Data class Tool {
    Type type;
    Brand brand;
    String code;

    enum Type {
        CHAINSAW("CHN", "CHAINSAW"),
        LADDER("LDR", "LADDER"),
        JACKHAMMER("JAK", "JACKHAMMER");

        private final String fullName;
        private final String typeCode;

        Type(String typeCode, String fullName) {
            this.typeCode = typeCode;
            this.fullName = fullName;
        }

        public String getTypeCode() {
            return typeCode;
        }
        public String getFullName() {
            return fullName;
        }
    }

    enum Brand {
        STIHL,
        WERNER,
        DEWALT,
        RIDGID
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
