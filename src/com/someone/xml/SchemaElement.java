package com.someone.xml;

import org.jdom.*;

public class SchemaElement {

    public static final int CHOICE = 1;
    public static final int SEQUENCE = 2;
    public static final int SIMPLE = 3;
    private boolean selected;
    private SchemaParser parser;
    private Element element;

    private String name;
    private String value;
    private String special;
    private Object parent;
    private Element data;

    private boolean compulsory = true;
    private int type;

    public SchemaElement(final Element element, final int type) {
        this.type = type;
        this.element = element;
        name = element.getAttributeValue("name");
        selected = true;
        final boolean propagate = true;

        if ("Name".equals(name) && parent != null) {
            ((SchemaElement) parent).special = value;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;

    }

    public SchemaElement[] getChoices() {
        if (parser == null) {
            parser = SchemaParser.getInstance();
        }

        return parser.getChoices(this);
    }

    public Element getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        if (value != null) {
            return value;
        } else {
            return "";
        }
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(final boolean isCompulsory) {
        this.compulsory = isCompulsory;
    }

    public void setParent(final Object obj) {
        parent = obj;
    }

    public void setValue(final String value) {
        if (!"".equals(value)) {
            this.value = value;
        }

        if ("Name".equals(name) && parent != null) {
            ((SchemaElement) parent).special = value;
        }
    }

    public String toString() {
        if (special != null) {
            return name + " (" + special + ")";
        }

        if (value != null) {
            return name + " ( " + value + ")";
        } else {
            return name;
        }
    }

    public String xmlOpen() {
        return "<" + name + ">";
    }

    public String xmlFull() {
        return "<" + name + "/>";
    }

    public String xmlClose() {
        return "</" + name + ">";
    }

    private SchemaElement() {
    }

    public Object clone() {
        final SchemaElement obj = new SchemaElement();
        obj.element = (Element) element.clone();
        obj.type = type;
        obj.name = name;
        obj.parser = parser;

        if (value != null) {
            obj.value = "" + value;
        }

        obj.special = special;

        return obj;
    }

    /**
     * Method setSpecial.
     */
    public void setSpecial(final String special) {
        this.special = special;
    }

    public String getSpecial() {
        return special;
    }


    /**
     * Returns the data.
     *
     * @return Element
     */
    public Element getData() {
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data The data to set
     */
    public void setData(final Element data) {
        this.data = data;
    }

}
