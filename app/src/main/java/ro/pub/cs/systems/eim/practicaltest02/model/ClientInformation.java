package ro.pub.cs.systems.eim.practicaltest02.model;

import java.util.Date;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;

public class ClientInformation {

    private String lastQueryTime;

    public ClientInformation() {
        this.lastQueryTime = null;
    }

    public ClientInformation(
            String lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    public void setLastQueryTime(String lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    public String getLastQueryTime() {
        return lastQueryTime;
    }



    @Override
    public String toString() {
        return "Last query time:" + ": " + lastQueryTime.toString();
    }

}