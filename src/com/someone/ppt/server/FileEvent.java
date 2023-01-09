package com.someone.ppt.server;

import java.io.*;


class FileEvent {
    private File file;
    private int lineNumber;
    private String startCommand;
    private String endCommand;

    public FileEvent(final File eventFile) throws Exception {
        file = eventFile;
        parseEvent();
    }

    private void parseEvent() throws Exception {
        lineNumber = Integer.parseInt(file.getName().substring(3, 5));
        final String fileType = file.getName().substring(0, 3).toUpperCase();
        final String fileEnd = file.getName().substring(6, 9).toUpperCase();
        final String startString = "START_" + fileType + "_" + fileEnd;
        final String endString = "END_" + fileType + "_" + fileEnd;

        try {
//			System.out.println(startString);
//			System.out.println(endString);
            startCommand = Protocol.class.getField(startString).get(this).toString();
            endCommand = Protocol.class.getField(endString).get(this).toString();
        } catch (final Exception e) {
            throw new Exception("Protocol not defined for " + startString + " or " +
                endString);
        }
    }

    public File getFile() {
        return file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getEndCommand() {
        return endCommand;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public static void main(final String[] args) throws Exception {
//		FileEvent e = new FileEvent(new File("FTD02.DAT"));
//		System.out.println(e.endCommand);
//		System.out.println(e.startCommand);


        final String name = "FTD02.DAT";

        System.out.println(name.substring(0, 3));
        System.out.println(name.substring(3, 5));
        System.out.println(name.substring(6, 9));

    }


}
