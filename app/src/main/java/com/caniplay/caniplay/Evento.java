package com.caniplay.caniplay;

import android.graphics.drawable.Drawable;

import java.util.Date;

/**
 * Created by A on 11/12/2017.
 */

public class Evento {

    protected String eventId;
protected String eventName;
protected Drawable evenImage;
protected Date eventDate;
protected String eventAddress;
protected String eventMembersUserName;
protected String Sport;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Drawable getEvenImage() {
        return evenImage;
    }

    public void setEvenImage(Drawable evenImage) {
        this.evenImage = evenImage;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventMembersUserName() {
        return eventMembersUserName;
    }

    public void setEventMembersUserName(String eventMembersUserName) {
        this.eventMembersUserName = eventMembersUserName;
    }

    public String getSport() {
        return Sport;
    }

    public void setSport(String sport) {
        Sport = sport;
    }
}
