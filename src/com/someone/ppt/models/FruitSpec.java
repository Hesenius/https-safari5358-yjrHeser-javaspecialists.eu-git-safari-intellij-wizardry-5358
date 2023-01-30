package com.someone.ppt.models;

class FruitSpec {

    public static final String[] fields = {
            "BarCode", "RunNumber", "PUC", "Commodity", "Variety", "Mark", "Pool", "Grade",
            "Pack", "TargetMarket", "SizeCount", "PrintCount", "Remark1",
            "Remark2", "Remark3", "Shift", "PickingReference", "Inventory", "GrowerId", "DateCode",
            "Organization", "Country",
            "PackhouseId", "GTIN"
    };

    public enum Field {
        BAR_CODE("BarCode"),
        RUN_NUMBER("RunNumber"),
        PUC("PUC"),
        COMMODITY("Commodity"),
        VARIETY("Variety"),
        MARK("Mark"),
        POOL("Pool"),
        GRADE("Grade"),
        PACK("Pack"),
        TARGET_MARKET("TargetMarket"),
        SIZE_COUNT("SizeCount"),
        PRINT_COUNT("PrintCount"),
        REMARK1("Remark1"),
        REMARK2("Remark2"),
        REMARK3("Remark3"),
        SHIFT("Shift"),
        PICKING_REFERENCE("PickingReference"),
        INVENTORY("Inventory"),
        GROWER_ID("GrowerId"),
        DATE_CODE("DateCode"),
        ORGANIZATION("Organization"),
        COUNTRY("Country"),
        PACKHOUSE_ID("PackhouseId"),
        GTIN("GTIN");

        private final String name;

        Field(String name) {
            this.name = name;
        }
    }

    public static int findIndex(final String columnName) {
        int index = 0;

        while (index < fields.length) {
            if (columnName.equalsIgnoreCase(fields[index])) {
                break;
            }
            index++;
        }

        if (index == fields.length) {
            return -1;
        } else {
            return index;
        }
    }
}
