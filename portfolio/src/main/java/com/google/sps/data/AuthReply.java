package com.google.sps.data;

public class AuthReply {
    private final boolean isLoggedIn;
    private final String authURL;

    public AuthReply(boolean isLoggedIn, String authURL) {
        this.isLoggedIn = isLoggedIn;
        this.authURL = authURL;
    }
}
