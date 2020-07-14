package com.google.sps.data;

public class Comment {
    private final long id;
    private final long timestamp;
    private final String authorEmail;
    private final String message;

    public Comment(long id, long timestamp, String authorEmail, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.authorEmail = authorEmail;
        this.message = message;
    }
}
