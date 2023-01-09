package com.someone.ppt.server;

public class Line {

    private int eventID;
    private int serverID;
    private int lineID;
    private String ip;
    private int port;
    private String description;
    private String packhouseName;
    private String lineName;

    public Line(final String packhouseName, final String lineName) {
        this.packhouseName = packhouseName;
        this.lineName = lineName;
        lineID = Integer.parseInt(lineName);
    }

    public String getDescription() {
        return description;
    }

    public int getEventID() {
        return eventID;
    }

    public String getIp() {
        return ip;
    }

    public int getLineID() {
        return lineID;
    }

    public int getPort() {
        return port;
    }

    public int getServerID() {
        return serverID;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setEventID(final int eventID) {
        this.eventID = eventID;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public void setLineID(final int lineID) {
        this.lineID = lineID;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public void setServerID(final int serverID) {
        this.serverID = serverID;
    }

    public String getPackhouseName() {
        return packhouseName;
    }

    public void setPackhouseName(final String packhouseName) {
        this.packhouseName = packhouseName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(final String lineName) {
        this.lineName = lineName;
    }

}
