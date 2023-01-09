package com.someone.ppt.models;

public class Layout {

    public static final String[] fields = {
        "PackhouseName", "LineName", "DropName", "TableName", "StationName"
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
