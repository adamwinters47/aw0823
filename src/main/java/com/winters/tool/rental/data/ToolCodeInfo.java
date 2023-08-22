package com.winters.tool.rental.data;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class ToolCodeInfo {
    String toolType;
    Tool.Type type;
    char toolBrand;
    Tool.Brand brand;
}
