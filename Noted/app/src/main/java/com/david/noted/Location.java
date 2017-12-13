package com.david.noted;

/**
 * Created by david on 13/12/2017.
 */

public class Location {
    private String location;
    private String title;

    public Location(String location, String title){

        this.location = location;
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
