package com.synternet.pubsubws;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;

/** Configuration holder for NATS connection and subscription. */
public class NatsConfig {
    private String url;
    private Connection connection;
    private Dispatcher dispatcher;
    private String subject;

    public NatsConfig(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
