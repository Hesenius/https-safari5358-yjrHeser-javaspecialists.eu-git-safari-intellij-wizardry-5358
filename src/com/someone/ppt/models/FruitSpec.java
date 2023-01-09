package com.someone.ppt.models;

class FruitSpec {

    public static final String[] fields = {
        "BarCode", "RunNumber", "PUC", "Commodity", "Variety", "Mark", "Pool", "Grade",
        "Pack", "TargetMarket", "SizeCount", "PrintCount", "Remark1",
        "Remark2", "Remark3", "Shift", "PickingReference", "Inventory", "GrowerId", "DateCode",
        "Organization", "Country",
        "PackhouseId", "GTIN"
    };

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
