package com.synternet.pubsubws;

/** Simple message container mirroring the TypeScript type. */
public class Message {
    private String subject;
    private String data;

    public Message(String subject, String data) {
        this.subject = subject;
        this.data = data;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
