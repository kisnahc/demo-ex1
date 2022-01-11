package com.kisnah.demoex1;

public class HostInfo {

    private String hostName;
    private boolean isAlive;

    public HostInfo() {
    }

    public HostInfo(String hostName) {
        this.hostName = hostName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    @Override
    public String toString() {
        return "HostInfo{" +
                "hostName='" + hostName + '\'' +
                ", isAlive=" + isAlive +
                '}';
    }
}
