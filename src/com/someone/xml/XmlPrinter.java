package com.someone.xml;

import org.jdom.*;
import org.jdom.output.*;

import java.io.*;

class XmlPrinter {

    public void print(final Element xmlElement) {
        final StringWriter writer = new StringWriter();

        try {
            final XMLOutputter outputter = new XMLOutputter();

            outputter.output(xmlElement, writer);
        } catch (final Exception xe) {
            xe.printStackTrace();
        }

        System.out.println(writer.toString() + "\n");
    }

}
