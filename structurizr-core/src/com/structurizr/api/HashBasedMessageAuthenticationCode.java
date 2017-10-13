package com.structurizr.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

final class HashBasedMessageAuthenticationCode {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private String apiSecret;

    HashBasedMessageAuthenticationCode(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    String generate(String content) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(apiSecret.getBytes(), HMAC_SHA256_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(content.getBytes());
        return DatatypeConverter.printHexBinary(rawHmac).toLowerCase();
    }

}
