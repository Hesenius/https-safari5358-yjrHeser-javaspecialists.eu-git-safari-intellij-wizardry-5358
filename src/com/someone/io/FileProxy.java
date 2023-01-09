package com.someone.io;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * Helper class to simplify development of Web Start applications
 */
public class FileProxy {
    private static ClassLoader classLoader;

    static {
        final FileProxy prox = new FileProxy();
        classLoader = prox.getClass().getClassLoader();
    }

    public static File getFile(final File file) {
        return file;
    }

    private static BufferedReader getFileReader(final File file) {
        BufferedReader result = null;

        try {
            result = new BufferedReader(new FileReader(file));
        } catch (final FileNotFoundException e) {
        }

        return result;
    }

    public static BufferedReader getFileReader(final String fileName) {
        return getFileReader(new File(fileName));
    }

    private static BufferedWriter getFileWriter(final File file) {
        BufferedWriter result = null;

        try {
            result = new BufferedWriter(new FileWriter(file));
        } catch (final IOException e) {
        }

        return result;
    }

    public static BufferedWriter getFileWriter(final String fileName) {
        return getFileWriter(new File(fileName));
    }

    public static Reader getResourceReader(final String resourceName) {
        BufferedReader result = null;

        try {
            result = new BufferedReader(new FileReader(resourceName));
        } catch (final FileNotFoundException e) {
        }

        return result;
    }

    public static File chooseFile(final Component parent) {
        final JFileChooser chooser = new JFileChooser();
        final int returnVal;
        chooser.setSelectedFile(new File("D:/temp/cis/cisdata"));
        returnVal = chooser.showOpenDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public static File saveFile(final Component parent) {
        final JFileChooser chooser = new JFileChooser();
        final int returnVal;
        returnVal = chooser.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public static URL getURL(final String location) {
        return classLoader.getResource(location);
    }
}
