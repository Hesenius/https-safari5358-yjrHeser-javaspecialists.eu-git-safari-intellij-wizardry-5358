package com.someone.util;

import java.io.*;
import java.net.*;

/*
 * Java Network Programming, Second Edition
 * Merlin Hughes, Michael Shoffner, Derek Hamner
 * Manning Publications Company; ISBN 188477749X
 *
 * http://nitric.com/jnp/
 *
 * Copyright (c) 1997-1999 Merlin Hughes, Michael Shoffner, Derek Hamner;
 * all rights reserved; see license.txt for details.
 */
class PseudoPing {
    static final int DEFAULT_PORT = 7;
    private static final int UDP_HEADER = 20 + 8;
    private static final int BACKSPACE = 8;
    private static String host = null;
    private static int port;
    private static int count = 32;
    private static boolean flood = false;
    private static DatagramSocket socket;
    private static byte[] outBuffer;
    private static byte[] inBuffer;
    private static DatagramPacket outPacket;
    private static DatagramPacket inPacket;
    private static int sent = 0;
    private static int received = 0;
    private static long minRTT = 100000;
    private static long maxRTT = 0;
    private static long totRTT = 0;

    public static void main(final String[] args) throws IOException {
        parseArgs(args);
        init();

        for (int i = 0; (i < count) || (count == 0); ++i) {
            final long past = System.currentTimeMillis();
            ping(i, past);

            try {
                pong(i, past);
            } catch (final InterruptedIOException ignored) {
            }
        }

        socket.close();
        printStats();
    }

    private static void parseArgs(final String[] args) {
    	/*
        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith("-")) {
                if (args[i].equals("-c") && (i < args.length - 1)) {
                    count = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-i") && (i < args.length - 1)) {
                    delay = Math.max(10, Integer.parseInt(args[++i]));
                } else if (args[i].equals("-s") && (i < args.length - 1)) {
                    size = Integer.parseInt(args[++i]);
                } else if (args[i].equals("-f")) {
                    flood = true;
                } else {
                    syntaxError();
                }
            } else {
                if (host != null) {
                    syntaxError();
                }

                int colon = args[i].indexOf(":");
                host = (colon > -1) ? args[i].substring(0, colon) : args[i];
                port = ((colon > -1) && (colon < args[i].length() - 1))
                       ? Integer.parseInt(args[i].substring(colon + 1))
                       : DEFAULT_PORT;
            }
        }

        if (host == null) {
            syntaxError();
        } */

        host = "127.0.0.1";
        port = 13;
    }

    static void syntaxError() {
        throw new IllegalArgumentException(
            "Ping [-c count] [-i wait] [-s packetsize] [-f] <hostname>[:<port>]");
    }

    private static void init() throws IOException {
        socket = new DatagramSocket();
        final int size = 64;
        outBuffer = new byte[Math.max(12, size - UDP_HEADER)];
        outPacket = new DatagramPacket(outBuffer, outBuffer.length,
            InetAddress.getByName(host), port);
        inBuffer = new byte[outBuffer.length];
        inPacket = new DatagramPacket(inBuffer, inBuffer.length);
    }

    private static void ping(final int seq, final long past) throws IOException {
        writeInt(seq, outBuffer, 0);
        writeLong(past, outBuffer, 4);
        socket.send(outPacket);
        ++sent;

        if (flood) {
            System.out.write('.');
            System.out.flush();
        }
    }

    private static void writeInt(final int datum, final byte[] dst, final int offset) {
        dst[offset] = (byte) (datum >> 24);
        dst[offset + 1] = (byte) (datum >> 16);
        dst[offset + 2] = (byte) (datum >> 8);
        dst[offset + 3] = (byte) datum;
    }

    private static void writeLong(final long datum, final byte[] dst, final int offset) {
        writeInt((int) (datum >> 32), dst, offset);
        writeInt((int) datum, dst, offset + 4);
    }

    private static void pong(final int seq, final long past) throws IOException {
        long present = System.currentTimeMillis();
        final int tmpRTT = (maxRTT == 0) ? 500 : (int) maxRTT * 2;
        final int delay = 1000;
        final int wait = Math.max(delay, (seq == count - 1) ? tmpRTT : 0);

        do {
            socket.setSoTimeout(Math.max(1, wait - (int) (present - past)));
            socket.receive(inPacket);
            ++received;
            present = System.currentTimeMillis();
            processPong(present);
        } while ((present - past < wait) && !flood);
    }

    private static void processPong(final long present) {
        final int seq = readInt(inBuffer, 0);
        final long when = readLong(inBuffer, 4);
        final long rtt = present - when;

        if (!flood) {
            System.out.println((inPacket.getLength() + UDP_HEADER) +
                " bytes from " +
                inPacket.getAddress().getHostName() +
                ": seq no " + seq + " time=" + rtt + " ms");
        } else {
            System.out.write(BACKSPACE);
            System.out.flush();
        }

        if (rtt < minRTT) {
            minRTT = rtt;
        }

        if (rtt > maxRTT) {
            maxRTT = rtt;
        }

        totRTT += rtt;
    }

    private static int readInt(final byte[] src, final int offset) {
        return (src[offset] << 24) | ((src[offset + 1] & 0xff) << 16) |
            ((src[offset + 2] & 0xff) << 8) | (src[offset + 3] & 0xff);
    }

    private static long readLong(final byte[] src, final int offset) {
        return ((long) readInt(src, offset) << 32) |
            ((long) readInt(src, offset + 4) & 0xffffffffL);
    }

    private static void printStats() {
        System.out.println(sent + " packets transmitted, " + received +
            " packets received, " +
            (100 * (sent - received) / sent) +
            "% packet loss");

        if (received > 0) {
            System.out.println("round-trip min/avg/max = " + minRTT + '/' +
                ((float) totRTT / received) + '/' + maxRTT +
                " ms");
        }
    }
}

/*
public class PseudoPing {
    public static void main(String[] args) {
        try {
            Socket          t   = new Socket("172.16.1.1", 7);
            DataInputStream dis = new DataInputStream(t.getInputStream());
            PrintStream     ps  = new PrintStream(t.getOutputStream());
            ps.println("Hello");

            String str = dis.readLine();

            if (str.equals("Hello")) {
                System.out.println("Alive!");
            } else {
                System.out.println("Dead or echo port not responding");
            }

            t.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
*/
