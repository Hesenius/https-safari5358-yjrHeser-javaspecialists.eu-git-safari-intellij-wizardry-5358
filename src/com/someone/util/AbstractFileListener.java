package com.someone.util;

import java.io.*;
import java.util.*;

/**
 * Check if there is files to process in the specified directories.
 */
public abstract class AbstractFileListener {
    protected DirFilter dirFilter;
    protected ArrayList files;

    protected AbstractFileListener() {
        dirFilter = new DirFilter();
        files = new ArrayList();
    }

    protected boolean hasEvent() {
        if ((files.size() == 0)) {
            checkDirectory();
        }

        return (files.size() > 0);
    }

    /**
     * Populates the files ArrayList.
     */
    protected abstract void checkDirectory();

    public static FileFilter getFilter() {
        return new DirFilter();
    }

    public static class DirFilter implements FileFilter {
        public boolean accept(final File pathname) {
            return (pathname.isFile() && pathname.canWrite());
        }

    }

}

