package com.someone.util;

    /*
     * @(#)Ping.java        1.2 01/12/13
     * Connect to each of a list of hosts and measure the time required to complete
     * the connection.  This example uses a selector and two additional threads in
     * order to demonstrate non-blocking connects and the multithreaded use of a
     * selector.
     *
     * Copyright 2001-2002 Sun Microsystems, Inc. All Rights Reserved.
     *
     * Redistribution and use in source and binary forms, with or
     * without modification, are permitted provided that the following
     * conditions are met:
     *
     * -Redistributions of source code must retain the above copyright
     * notice, this  list of conditions and the following disclaimer.
     *
     * -Redistribution in binary form must reproduct the above copyright
     * notice, this list of conditions and the following disclaimer in
     * the documentation and/or other materials provided with the
     * distribution.
     *
     * Neither the name of Sun Microsystems, Inc. or the names of
     * contributors may be used to endorse or promote products derived
     * from this software without specific prior written permission.
     *
     * This software is provided "AS IS," without a warranty of any
     * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
     * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
     * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
     * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF  OR
     * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR
     * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
     * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
     * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
     * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
     * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
     * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
     *
     * You acknowledge that Software is not designed, licensed or
     * intended for use in the design, construction, operation or
     * maintenance of any nuclear facility.
     */

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

class Ping {
    // The default daytime port
    private static int DAYTIME_PORT = 13;

    // The port we'll actually use
    private static int port = DAYTIME_PORT;

    public static void main(final String[] args) throws InterruptedException,
        IOException {
        // If the first argument is a string of digits then we take that
        // to be the port number to use
        //	if (Pattern.matches("[0-9]+", args[0])) {
        //	    port = Integer.parseInt(args[0]);
        //	    firstArg = 1;
        //	}
        for (int portCounter = 500; portCounter < 1500; portCounter++) {
            port = portCounter;

            // Create the threads and start them up
            final Printer printer = new Printer();
            printer.start();

            final Connector connector = new Connector(printer);
            connector.start();

            // Create the targets and add them to the connector
            final LinkedList targets = new LinkedList();

            Target t = new Target("172.16.1.196");
            targets.add(t);
            connector.add(t);


            // Wait for everything to finish
            Thread.sleep(2000);
            connector.shutdown();
            connector.join();

            // Print status of targets that have not yet been shown
            for (final Iterator i = targets.iterator(); i.hasNext(); ) {
                t = (Target) i.next();

                if (!t.shown) {
                    t.show();
                }
            }
        }
    }

    // Representation of a ping target
    //
    static class Target {
        InetSocketAddress address;
        SocketChannel channel;
        Exception failure;
        long connectStart;
        long connectFinish = 0;
        private boolean shown = false;

        private Target(final String host) {
            try {
                address = new InetSocketAddress(InetAddress.getByName(host),
                    port);
            } catch (final IOException x) {
                failure = x;
            }
        }

        void show() {
            final String result;

            if (connectFinish != 0) {
                result = Long.toString(connectFinish - connectStart) + "ms";
            } else if (failure != null) {
                result = failure.toString();
            } else {
                result = "Timed out";
            }

            System.out.println(address + " : " + result);
            shown = true;
        }
    }

    // Thread for printing targets as they're heard from
    //
    static class Printer extends Thread {
        private LinkedList pending = new LinkedList();

        private Printer() {
            setName("Printer");
            setDaemon(true);
        }

        void add(final Target t) {
            synchronized (pending) {
                pending.add(t);
                pending.notify();
            }
        }

        public void run() {
            try {
                for (; ; ) {
                    Target t = null;
                    synchronized (pending) {
                        while (pending.size() == 0)
                            pending.wait();

                        t = (Target) pending.removeFirst();
                    }

                    t.show();
                }
            } catch (final InterruptedException x) {
                return;
            }
        }
    }

    // Thread for connecting to all targets in parallel via a single selector
    //
    private static class Connector extends Thread {
        private Selector sel;
        private Printer printer;

        // List of pending targets.  We use this list because if we try to
        // register a channel with the selector while the connector thread is
        // blocked in the selector then we will block.
        //
        private LinkedList pending = new LinkedList();
        private volatile boolean shutdown = false;

        private Connector(final Printer pr) throws IOException {
            printer = pr;
            sel = Selector.open();
            setName("Connector");
        }

        // Initiate a connection sequence to the given target and add the
        // target to the pending-target list
        //
        private void add(final Target t) {
            SocketChannel sc = null;

            try {
                // Open the channel, set it to non-blocking, initiate connect
                sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.connect(t.address);


                // Record the time we started
                t.channel = sc;
                t.connectStart = System.currentTimeMillis();

                // Add the new channel to the pending list
                synchronized (pending) {
                    pending.add(t);
                }


                // Nudge the selector so that it will process the pending list
                sel.wakeup();
            } catch (final IOException x) {
                if (sc != null) {
                    try {
                        sc.close();
                    } catch (final IOException xx) {
                    }
                }

                t.failure = x;
                printer.add(t);
            }
        }

        // Process any targets in the pending list
        //
        private void processPendingTargets() throws IOException {
            synchronized (pending) {
                while (pending.size() > 0) {
                    final Target t = (Target) pending.removeFirst();

                    try {
                        // Register the channel with the selector, indicating
                        // interest in connection completion and attaching the
                        // target object so that we can get the target back
                        // after the key is added to the selector's
                        // selected-key set
                        t.channel.register(sel, SelectionKey.OP_CONNECT, t);
                    } catch (final IOException x) {
                        // Something went wrong, so close the channel and
                        // record the failure
                        t.channel.close();
                        t.failure = x;
                        printer.add(t);
                    }
                }
            }
        }

        // Process keys that have become selected
        //
        private void processSelectedKeys() throws IOException {
            for (final Iterator i = sel.selectedKeys().iterator(); i.hasNext(); ) {
                // Retrieve the next key and remove it from the set
                final SelectionKey sk = (SelectionKey) i.next();
                i.remove();

                // Retrieve the target and the channel
                final Target t = (Target) sk.attachment();
                final SocketChannel sc = (SocketChannel) sk.channel();

                // Attempt to complete the connection sequence
                try {
                    if (sc.finishConnect()) {
                        sk.cancel();
                        t.connectFinish = System.currentTimeMillis();
                        sc.close();
                        printer.add(t);
                    }
                } catch (final IOException x) {
                    sc.close();
                    t.failure = x;
                    printer.add(t);
                }
            }
        }

        // Invoked by the main thread when it's time to shut down
        //
        private void shutdown() {
            shutdown = true;
            sel.wakeup();
        }

        // Connector loop
        //
        public void run() {
            for (; ; ) {
                try {
                    final int n = sel.select();

                    if (n > 0) {
                        processSelectedKeys();
                    }

                    processPendingTargets();

                    if (shutdown) {
                        sel.close();

                        return;
                    }
                } catch (final IOException x) {
                    x.printStackTrace();
                }
            }
        }
    }
}
