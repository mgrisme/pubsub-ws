package com.synternet.pubsubws;

import io.nats.nkey.NKey;
import io.nats.nkey.NKeyType;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Utilities for creating JWT tokens for application access. */
public class UserJwt {

    public static final int JWT_EXPIRATION_HOURS = 2;

    public record JwtWithSeed(String jwt, String userSeed) {}

    public static JwtWithSeed createAppJwt(String developerSeed) throws GeneralSecurityException, IOException {
        return createAppJwt(developerSeed, Instant.now().plus(JWT_EXPIRATION_HOURS, ChronoUnit.HOURS));
    }

    public static JwtWithSeed createAppJwt(String developerSeed, Instant expiration) throws GeneralSecurityException, IOException {
        NKey user = NKey.createUser(new SecureRandom());
        String userSeed = new String(user.getSeed());
        String jwt = generateUserJwt(userSeed, developerSeed, expiration);
        return new JwtWithSeed(jwt, userSeed);
    }

    public static String generateUserJwt(String userSeed, String developerSeed, Instant expiration) throws GeneralSecurityException, IOException {
        NKey user = NKey.fromSeed(userSeed.toCharArray());
        NKey developer = NKey.fromSeed(developerSeed.toCharArray());

        Map<String, Object> payload = new HashMap<>();
        payload.put("jti", UUID.randomUUID().toString());
        payload.put("iat", Instant.now().getEpochSecond());
        payload.put("iss", new String(developer.getPublicKey()));
        payload.put("name", "developer");
        payload.put("sub", new String(user.getPublicKey()));
        payload.put("nats", getNatsConfig());
        payload.put("exp", expiration.getEpochSecond());

        String headerJson = "{\"typ\":\"JWT\",\"alg\":\"ed25519-nkey\"}";
        String payloadJson = JsonUtil.toJson(payload);

        String jwtBase = base64UrlEncode(headerJson.getBytes()) + "." + base64UrlEncode(payloadJson.getBytes());
        byte[] sig = developer.sign(jwtBase.getBytes());
        return jwtBase + "." + base64UrlEncode(sig);
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static Map<String, Object> getNatsConfig() {
        Map<String, Object> nats = new HashMap<>();
        nats.put("pub", new HashMap<>());
        nats.put("sub", new HashMap<>());
        nats.put("subs", -1);
        nats.put("data", -1);
        nats.put("payload", -1);
        nats.put("type", "user");
        nats.put("version", 2);
        return nats;
    }
}
