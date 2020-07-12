package com.google.sps.data;

public class Comment {
    private final long id;
    private final long timestamp;
    private final String author;
    private final String message;

    public Comment(long id, long timestamp, String author, String message) {
        this.id = id;
        this.timestamp = timestamp;
        this.author = author;
        this.message = message;
    }
}
