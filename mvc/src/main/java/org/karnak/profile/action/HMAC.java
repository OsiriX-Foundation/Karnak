package org.karnak.profile.action;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static io.swagger.codegen.v3.config.CodegenConfigurator.LOGGER;

public class HMAC {
    private Mac mac;

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String keyPath = "mvc/src/main/resources/karnak_profile_hmac";

    HMAC() {
        String key = readTextFile(this.keyPath);
        initHMAC(key);
    }

    HMAC(String keyPath) {
        String key = readTextFile(keyPath);
        initHMAC(key);
    }

    private void initHMAC(String keyValue) {
        try {
            SecretKeySpec key = new SecretKeySpec((keyValue).getBytes("UTF-8"), this.HMAC_SHA256);
            this.mac = Mac.getInstance(this.HMAC_SHA256);
            this.mac.init(key);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unsupported Encoding exception for the HMACkey", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Invalid algorithm for the HMAC", e);
        } catch (InvalidKeyException e) {
            LOGGER.error("Invalid key for the HMAC init", e);
        }
    }

    private String readTextFile(String keyPath) {
        Path filePath = Paths.get(keyPath);
        String content = "";
        try {
            // readAllBytes ensures that the file is closed when all bytes have been read.
            byte[] data = Files.readAllBytes(filePath);
            content = new String(data);

        } catch (IOException e) {
            LOGGER.error("Cannot read HMACKey file", e);
        }
        return content;
    }
    public byte[] byteHash(String value) {
        byte[] bytes = null;
        try {
            bytes = mac.doFinal(value.getBytes("ASCII"));
        }
        catch(UnsupportedEncodingException e) {
            LOGGER.error("On hashed Value getBytes", e);
        }
        return bytes;
    }
}
