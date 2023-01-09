package com.someone.util;

import java.util.*;

/**
 * This class polls a client class that implements the Tickable interface.
 */
public class Timer {

    private Tickable client;
    private long millisToWait;
    private Clock timer;
    private Thread clockThread;

    /**
     * Setup and start a timer.
     *
     * @param client       the client to poll
     * @param millisToWait the polling interval.
     */
    public Timer(final Tickable client, final long millisToWait) {
        this.client = client;
        this.millisToWait = millisToWait;
        timer = new Clock();
        clockThread = new Thread(timer, client.getName() + " Timer");

        clockThread.start();
    }

    /**
     * Force the timer to poll the client.
     */
    public void reset() {
        clockThread.interrupt();
    }

    /**
     * Stop the timer.
     */
    public void stop() {
        timer.stop();
    }

    /**
     * This class fires an event on a client at a predetermed interval plus a
     * random time value.
     */
    private class Clock implements Runnable {
        private boolean running;
        private Random equalizer;

        /**
         * Constructor
         */
        private Clock() {
            equalizer = new Random();
            running = true;
        }

        /**
         * Run
         */
        public void run() {
            while (running) {
                client.fire();

                try {
                    Thread.sleep(millisToWait);
                } catch (final InterruptedException e) {
                    continue;    // do not fire client when interrupted
                }
            }
        }

        private void stop() {
            running = false;
        }

    }

}





