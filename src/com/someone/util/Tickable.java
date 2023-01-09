package com.someone.util;

/**
 * This interface describes a client that may be polled.
 */
public interface Tickable {

    /**
     * Fire an event.
     */
    public void fire();

    /**
     * Return the name of the client.
     */
    public String getName();
}





