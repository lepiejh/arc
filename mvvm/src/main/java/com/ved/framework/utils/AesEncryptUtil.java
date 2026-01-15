package com.ved.framework.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtil {
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    // Default 16-byte key and IV (128-bit AES)
    private static final String DEFAULT_KEY = "1234567890abcdef"; // Exactly 16 ASCII characters
    private static final String DEFAULT_IV = "1234567890abcdef"; // Exactly 16 ASCII characters

    public static String encrypt(String data, String key, String iv) {
        try {
            // 1. 验证输入
            if (TextUtils.isEmpty(data)) {
                return data;
            }

            // 2. 验证key和IV
            byte[] keyBytes = validateKey(key);
            byte[] ivBytes = validateIV(iv);

            // 3. 执行加密
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encrypted = cipher.doFinal(data.getBytes(CHARSET));

            // 4. 生成URL安全的Base64（无padding）
            return android.util.Base64.encodeToString(encrypted, android.util.Base64.NO_WRAP)
                    .replace('+', '-')
                    .replace('/', '_')
                    .replace("=", "");

        } catch (Exception e) {
            KLog.e("Encryption failed. Input: '" + data +
                    "', Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    public static String desEncrypt(String data, String key, String iv) {
        try {
            // 1. 增强输入检查
            if (TextUtils.isEmpty(data)) {
                KLog.w("Empty input string");
                return data;
            }

            // 2. Base64预处理
            String processedBase64 = data.trim()
                    .replaceAll("\\s+", "")
                    .replaceAll("[^A-Za-z0-9+/=_-]", "")
                    .replace('-', '+')
                    .replace('_', '/');

            // 3. 精确补全padding
            int padding = (4 - processedBase64.length() % 4) % 4;
            processedBase64 += "====".substring(0, padding);

            // 4. Base64解码
            byte[] encryptedData;
            try {
                encryptedData = android.util.Base64.decode(processedBase64, Base64.NO_WRAP);
            } catch (IllegalArgumentException e) {
                KLog.e("Base64 decode failed. Processed: " + processedBase64);
                return null;
            }

            // 5. 验证块大小
            if (encryptedData.length % 16 != 0) {
                KLog.e("Invalid ciphertext length: " + encryptedData.length+" ,encryptedData : "+ Arrays.toString(encryptedData));
                return null;
            }

            // 6. 验证密钥和IV
            byte[] keyBytes = validateKey(key);
            byte[] ivBytes = validateIV(iv);

            // 7. 执行解密
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] original = cipher.doFinal(encryptedData);
            return new String(original, CHARSET).trim();

        } catch (BadPaddingException e) {
            KLog.e("Key/IV mismatch or corrupted data");
            return null;
        } catch (IllegalBlockSizeException e) {
            KLog.e("Invalid block size. Possible causes: " +
                    "1. Not encrypted data, 2. Corrupted data, 3. Wrong algorithm");
            return null;
        } catch (Exception e) {
            KLog.e("Decryption failed. Error: " + e.getClass().getSimpleName() +
                    ": " + e.getMessage());
            return null;
        }
    }

    // Helper method to ensure 16-byte key
    private static byte[] validateKey(String key) throws Exception {
        byte[] keyBytes = key.getBytes(CHARSET);
        if (keyBytes.length != 16) {
            throw new IllegalArgumentException("Key must be exactly 16 bytes (128-bit)");
        }
        return keyBytes;
    }

    // Helper method to ensure 16-byte IV
    private static byte[] validateIV(String iv) throws Exception {
        byte[] ivBytes = iv.getBytes(CHARSET);
        if (ivBytes.length != 16) {
            throw new IllegalArgumentException("IV must be exactly 16 bytes");
        }
        return ivBytes;
    }

    public static String encrypt(String data) {
        return encrypt(data, DEFAULT_KEY, DEFAULT_IV);
    }

    public static String desEncrypt(String data) {
        return desEncrypt(data, DEFAULT_KEY, DEFAULT_IV);
    }
}
