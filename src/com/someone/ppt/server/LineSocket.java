package com.someone.ppt.server;

import java.io.*;
import java.net.*;

class LineSocket implements Protocol {
    private String ip;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public LineSocket(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
    }

    // precondition: connected
    // postcondition: still connected
    public void sendFile(final FileEvent event) throws IOException, SocketException {
        transmit(event.getStartCommand());

        //    	System.out.println("Send: " + event.getStartCommand());
        final BufferedReader reader = new BufferedReader(
            new FileReader(event.getFile()));

        String line;

        while ((line = reader.readLine()) != null) {
            transmit(INCOMING_DATA + line);

//        	try {
//        		Thread.sleep(1000);
//        	} catch (Exception e) {
//        	}
        }

        transmit(event.getEndCommand());


        //    	System.out.println("Send close: " + event.getEndCommand());
        reader.close();
    }


    public String transmitTest(final String command) throws SocketException {
        //        System.out.println("writing: " + command);
        out.write(command + "\n");
        out.flush();

        final String result = receiveTest();
        return result;
    }

    private String receiveTest() {
        BufferedWriter writer = null;
        final StringWriter stringWriter = new StringWriter();
        try {

            writer = new BufferedWriter(stringWriter);

            final char[] line = new char[512];
            int bytesRead = 512;

            while (bytesRead == line.length) {
                bytesRead = in.read(line);

                final String l = new String(line, 0, bytesRead);

//				System.out.println("Read: " + bytesRead + ": (" + l+")");
                writer.write(l);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (final IOException e) {
            }
        }
        return stringWriter.toString();
    }


    public void transmit(final String command) throws SocketException {
        //        System.out.println("writing: " + command);
        out.write(command + "\n");
        out.flush();

        final String result = receive();

        //        System.out.println("receiving: " + result +"..");
        if (result.equals(ACK)) {
            return;
        } else if (result.equals(NAK)) {
            transmit(command);
        } else {
            System.out.println("(Received: " + result +
                "), ACK OR NAK must follow transmit");
            throw new SocketException("(Received: " + result +
                "), ACK OR NAK must follow transmit");
        }
    }

    private String receive() throws SocketException {
        try {

            final char[] buff = new char[4];
            in.read(buff);

            // Strip 0 character needed by C code
            final char[] chop = new char[1];
            in.read(chop);

            return new String(buff);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new SocketException("Could not read from socket.");
        }
    }

//    private void sendNAK() {
//        out.write(NAK + "\n");
//    }

    // precondition: connected
    // postcondition: connected
    public void receiveFile(final File targetFile, final String fileCommand)
        throws IOException, SocketException {
//        System.out.println("receiving file..");
//		transmit("[93");
        transmit(fileCommand);

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(targetFile));

            final char[] line = new char[512];
            int bytesRead = 1;

            while (bytesRead > 0) {
//                				System.out.println("Reading...");
                bytesRead = in.read(line);

                final String l = new String(line, 0, bytesRead);

//                				System.out.println("Read: " + bytesRead + ": " + l+"\n");
                if (bytesRead >= 5) {
                    final String end = new String(line, bytesRead - 5, 5);

//                    					System.out.println("End: " + end);
                    if (end.startsWith("[99")) {
                        bytesRead = 0;
                    } else {
                        writer.write(l + "\n");
                    }
                }


//                				System.out.println("Sending: " + ACK + "\n");
                out.write(ACK);
                out.flush();

            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public void openSocket() throws UnknownHostException, IOException {
//		System.out.println("open socketa " + new java.util.Date());
        socket = new Socket();
        try {
            socket.connect(new java.net.InetSocketAddress(ip, port), 10000);
        } catch (final IOException e) {
//			System.out.println(new java.util.Date());
            throw e;
        }

//        socket = new Socket(ip, port);

        //		System.out.println("create input reader");
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        //		System.out.println("create output writer");
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void closeSocket() throws SocketException {
        try {
            out.write(EOF);
            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (final Exception e) {
            throw new SocketException("Could not close socket: " +
                e.getMessage());
        }
    }
}
