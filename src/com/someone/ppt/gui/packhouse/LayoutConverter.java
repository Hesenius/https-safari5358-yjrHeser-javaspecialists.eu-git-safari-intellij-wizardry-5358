package com.someone.ppt.gui.packhouse;

import com.someone.ppt.models.*;
import org.jdom.*;

public class LayoutConverter {
    public static Element convertLayout(final String[][] matrixLayout) {
        String packhouse = null;
        String line = null;
        String drop = null;
        String table = null;
        String station = null;

        final Element layout = new Element("CMS");
        Element packhouseElement = null;
        Element lineElement = null;
        Element dropElement = null;
        Element tableElement = null;
        Element stationElement = null;

        for (int i = 0; i < matrixLayout.length; i++) {
            if (!matrixLayout[i][0].equals(packhouse)) {
                packhouse = matrixLayout[i][0];

                packhouseElement = new Element(Layout.fields[0]);
                packhouseElement.addContent(new Element("Name").addContent(
                    packhouse));
                layout.addContent(packhouseElement);

                line = null;
                drop = null;
                table = null;
                station = null;
            }

            if (!matrixLayout[i][1].equals(line)) {
                line = matrixLayout[i][1];
                drop = null;
                table = null;
                station = null;

                lineElement = new Element(Layout.fields[1]);
                lineElement.addContent(new Element("Name").addContent(line));
                packhouseElement.addContent(lineElement);
            }

            if (!matrixLayout[i][2].equals(drop)) {
                drop = matrixLayout[i][2];
                table = null;
                station = null;

                dropElement = new Element(Layout.fields[2]);
                dropElement.addContent(new Element("Name").addContent(drop));
                lineElement.addContent(dropElement);
            }

            if (!matrixLayout[i][3].equals(table)) {
                table = matrixLayout[i][3];
                station = null;

                tableElement = new Element(Layout.fields[3]);
                tableElement.addContent(new Element("Name").addContent(table));
                dropElement.addContent(tableElement);
            }

            if (!matrixLayout[i][4].equals(station)) {
                station = matrixLayout[i][4];

                stationElement = new Element(Layout.fields[4]);
                stationElement.addContent(new Element("Name").addContent(
                    station));
                tableElement.addContent(stationElement);
            }
        }

        return layout;
    }
}
