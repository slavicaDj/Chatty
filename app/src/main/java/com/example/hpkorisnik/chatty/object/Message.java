package com.example.hpkorisnik.chatty.object;

/**
 * Created by HP KORISNIK on 18-Apr-17.
 */

public class Message {

    private String id;
    private String fromId;
    private String text;
    private String timestamp;
    private String toldId;

    private String fromName;

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    private String toName;

    public Message() {}

    public Message(String id, String fromId, String text, String timestamp, String toldId) {
        this.id = id;
        this.fromId = fromId;
        this.text = text;
        this.timestamp = timestamp;
        this.toldId = toldId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getToldId() {
        return toldId;
    }

    public void setToldId(String toldId) {
        this.toldId = toldId;
    }

    @Override
    public String toString() {
        return "From: " + fromName + "\n" +
                "To: " + toName + "\n" +
                "Text: "  + text;
    }
}
