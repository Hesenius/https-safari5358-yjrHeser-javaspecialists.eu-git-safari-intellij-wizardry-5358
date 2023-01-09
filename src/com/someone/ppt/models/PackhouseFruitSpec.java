package com.someone.ppt.models;

class PackhouseFruitSpec {

    public static String[] fields;

    static {
        fields = new String[Layout.fields.length + FruitSpec.fields.length];
        int count = 0;
        int i;
        for (i = 0; i < Layout.fields.length; i++) {
            fields[count++] = Layout.fields[i];
        }
        for (i = 0; i < FruitSpec.fields.length; i++) {
            fields[count++] = FruitSpec.fields[i];
        }
    }

    public PackhouseFruitSpec() {
        super();
    }

}
