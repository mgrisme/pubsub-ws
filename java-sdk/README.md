# PubSub WebSocket Java SDK

This directory contains a minimal Java reimplementation of the TypeScript SDK provided in this repository. It exposes helper classes to generate JWT tokens and interact with Synternet's NATS servers over WebSockets. The API mirrors the TypeScript version so it can be easily used from SARL.

## Building

A Maven `pom.xml` is provided. Run `mvn package` to build the library. Maven will download the required dependencies from Maven Central.

## Basic Usage

```java
NatsConfig config = new NatsConfig("wss://url.com:443");
UserJwt.JwtWithSeed creds = UserJwt.createAppJwt(developerSeed);
PubSub.connect(config, creds.jwt(), creds.userSeed());

PubSub.subscribe(config, "example.subject", messages -> {
    for (Message m : messages) {
        System.out.println(m.getSubject() + " -> " + m.getData());
    }
}, Throwable::printStackTrace);
```

See the Java source files for more details.
