package com.synternet.pubsubws;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.MessageHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/** Convenience API resembling the TypeScript SDK. */
public class PubSub {

    /** Connects to the NATS server using WebSocket URL and JWT authentication. */
    public static Connection connect(NatsConfig config, String jwt, String nkey) throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(config.getUrl())
                .authHandler(new JwtNkeyAuthHandler(jwt, nkey))
                .build();
        Connection nc = Nats.connect(options);
        config.setConnection(nc);
        return nc;
    }

    /** Subscribe to a subject delivering messages to the callback. */
    public static void subscribe(NatsConfig config, String subject, Consumer<List<Message>> onMessages,
                                 Consumer<Throwable> onError) {
        if (config.getConnection() == null) {
            onError.accept(new IllegalStateException("Connection not established"));
            return;
        }
        MessageHandler handler = msg -> {
            List<Message> m = new ArrayList<>();
            m.add(new Message(msg.getSubject(), new String(msg.getData())));
            onMessages.accept(m);
        };
        Dispatcher d = config.getConnection().createDispatcher(handler);
        d.subscribe(subject);
        config.setDispatcher(d);
        config.setSubject(subject);
    }

    /** Publish a string payload to a subject. */
    public static void publish(NatsConfig config, String subject, String data) {
        if (config.getConnection() != null) {
            config.getConnection().publish(subject, data.getBytes());
        }
    }

    /** Request data and wait for a single response. */
    public static CompletableFuture<String> request(NatsConfig config, String subject, byte[] data) {
        CompletableFuture<String> future = new CompletableFuture<>();
        if (config.getConnection() == null) {
            future.completeExceptionally(new IllegalStateException("Connection not established"));
            return future;
        }
        try {
            io.nats.client.Message reply = config.getConnection().request(subject, data, Duration.ofSeconds(5));
            future.complete(new String(reply.getData()));
        } catch (InterruptedException | IOException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    /** Cleanly unsubscribe and close connection. */
    public static void unsubscribe(NatsConfig config) {
        if (config.getDispatcher() != null && config.getSubject() != null) {
            config.getDispatcher().unsubscribe(config.getSubject());
            config.setDispatcher(null);
            config.setSubject(null);
        }
        if (config.getConnection() != null) {
            try {
                config.getConnection().close();
            } catch (InterruptedException e) {
                // ignore
            }
            config.setConnection(null);
        }
    }
}
