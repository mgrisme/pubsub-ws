package com.synternet.pubsubws;

import io.nats.client.AuthHandler;
import io.nats.nkey.NKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/** AuthHandler that uses a JWT and NKey seed for NATS connections. */
public class JwtNkeyAuthHandler implements AuthHandler {
    private final String jwt;
    private final String seed;

    public JwtNkeyAuthHandler(String jwt, String seed) {
        this.jwt = jwt;
        this.seed = seed;
    }

    @Override
    public byte[] sign(byte[] nonce) {
        try {
            NKey nkey = NKey.fromSeed(seed.toCharArray());
            return nkey.sign(nonce);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to sign nonce", e);
        }
    }

    @Override
    public char[] getID() {
        try {
            NKey nkey = NKey.fromSeed(seed.toCharArray());
            return nkey.getPublicKey();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Failed to get public key", e);
        }
    }

    @Override
    public char[] getJWT() {
        return jwt.toCharArray();
    }
}
