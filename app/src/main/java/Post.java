package com.example.rohan.hw9;

/**
 * Created by rohan on 4/15/17.
 */

public class Post {
    private String message;
    private String created_time;

    public Post(String message, String created_time) {
        this.message = message;
        this.created_time = created_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

}
