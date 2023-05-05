package com.tai.api_chatgpt.model;

public class Message {
    public static String SENT_BY_HUMAN = "human";
    public static String SENT_BY_AI = "ai";
    public String message;
    public String sentBy;

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public String getSentBy() {
        return sentBy;
    }
}
