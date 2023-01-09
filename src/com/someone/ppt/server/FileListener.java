package com.someone.ppt.server;


import com.someone.util.*;
import com.someone.util.Timer;

import java.io.*;
import java.util.*;


/**
 * Check if there is files to process in the specified directories. Files are
 * sorted according to their priority.
 */
public class FileListener extends AbstractFileListener implements Tickable {
    private File directory;
    private CmsServer server;

    public FileListener(final CmsServer server, final String directory) {
        new Timer(this, 1000);
        this.directory = new File(directory);
        this.server = server;
    }

    public synchronized void fire() {
        if (hasEvent()) {
            final ArrayList oneLine = new ArrayList();
            final FileEvent firstEvent = (FileEvent) files.remove(0);
            oneLine.add(firstEvent);
            int i;
            final int size = files.size();
            for (i = 0; i < size; i++) {
                final FileEvent event = (FileEvent) files.get(0);
                if (event.getLineNumber() != firstEvent.getLineNumber()) {
                    break;
                }
                oneLine.add(event);
                files.remove(0);
            }
            final FileEvent[] events = new FileEvent[oneLine.size()];
            oneLine.toArray(events);
            server.fireLine(firstEvent.getLineNumber() - 1, events);
        }
    }

    public void checkDirectory() {
        final File[] dirFiles = directory.listFiles(dirFilter);

        try {
            for (int i = 0; i < dirFiles.length; i++) {
                files.add(new FileEvent(dirFiles[i]));
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        Collections.sort(files, fileComparator);
    }

    private static FileComparator fileComparator = new FileComparator();

    /**
     * Compares on attributes start positions in the record.
     */
    private static class FileComparator implements Comparator {
        public int compare(final Object o1, final Object o2) {

            if (((FileEvent) o1).getLineNumber() < ((FileEvent) o2).getLineNumber()) {
                return -1;
            } else {
                return 1;
            }

        }

        public boolean equals(final Object obj) {
            return false;
        }
    }

    /**
     * Return the name of the client.
     */
    public String getName() {
        return "CIS File Listener";
    }

}

