package com.ved.framework.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {
    private static final String CHARSET = "UTF-8";
    private static final String ALGORITHM = "DESede/CBC/PKCS5Padding";
    private static final String TRANSFORMATION = "DESede";

    public static String encryptDES(String encryptString, String encryptKey, byte[] iv) {
        try {
            if (TextUtils.isEmpty(encryptString)) {
                return encryptString;
            }

            byte[] keyBytes = Arrays.copyOf(encryptKey.getBytes(CHARSET), 24);
            SecretKeySpec key = new SecretKeySpec(keyBytes, TRANSFORMATION);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            byte[] encryptedData = cipher.doFinal(encryptString.getBytes(CHARSET));

            // 使用URL安全的Base64编码便于传输
            return Base64.encodeToString(encryptedData, Base64.NO_WRAP)
                    .replace('+', '-')
                    .replace('/', '_')
                    .replace("=", "");

        } catch (Exception e) {
            KLog.e("Encryption failed. Input: '" + encryptString +
                    "', Error: " + e.getMessage());
            return null;
        }
    }

    public static String decryptDES(String decryptString, String decryptKey, byte[] iv) {
        // 1. 空值检查
        if (TextUtils.isEmpty(decryptString)) {
            KLog.w("Empty input string");
            return decryptString;
        }

        try {
            // 2. Base64预处理（增强版）
            String processedBase64 = preprocessBase64(decryptString);
            if (processedBase64 == null) {
                KLog.e("Invalid Base64 format after preprocessing");
                return null;
            }

            // 3. Base64解码
            byte[] byteMi;
            try {
                byteMi = Base64.decode(processedBase64, Base64.NO_WRAP);
            } catch (IllegalArgumentException e) {
                KLog.e("Base64 decoding failed. Processed: " + processedBase64);
                return null;
            }

            // 4. 验证密文长度
            if (byteMi.length % 8 != 0) { // DESede块大小是8字节
                KLog.e("Invalid ciphertext length: " + byteMi.length+"  ,byteMi : "+ Arrays.toString(byteMi));
                return null;
            }

            // 5. 准备密钥和IV
            byte[] keyBytes = normalizeKey(decryptKey);
            if (keyBytes == null) {
                KLog.e("Key normalization failed");
                return null;
            }

            // 6. 执行解密
            return performDecryption(byteMi, keyBytes, iv);

        } catch (Exception e) {
            KLog.e("Decryption failed. Input: " + abbreviate(decryptString) +
                    ", Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    // 辅助方法：Base64预处理
    private static String preprocessBase64(String input) {
        if (input == null) return null;

        // 移除所有非Base64字符
        String cleaned = input.trim()
                .replaceAll("[^A-Za-z0-9+/=_-]", "")
                .replace('-', '+')
                .replace('_', '/');

        // 验证有效性
        if (cleaned.isEmpty()) return null;

        // 补全padding
        switch (cleaned.length() % 4) {
            case 2: cleaned += "=="; break;
            case 3: cleaned += "="; break;
        }

        return cleaned;
    }

    // 辅助方法：密钥标准化
    private static byte[] normalizeKey(String key) {
        if (key == null) return null;

        try {
            byte[] keyBytes = key.getBytes(CHARSET);
            return Arrays.copyOf(keyBytes, 24); // Triple DES需要24字节
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // 辅助方法：执行解密操作
    private static String performDecryption(byte[] ciphertext, byte[] keyBytes, byte[] iv)
            throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyBytes, TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        byte[] decryptedData = cipher.doFinal(ciphertext);
        return new String(decryptedData, CHARSET).trim();
    }

    // 辅助方法：字符串缩写（用于日志）
    private static String abbreviate(String s) {
        if (s == null) return "null";
        if (s.length() <= 20) return "'" + s + "'";
        return "'" + s.substring(0, 10) + "..." + s.substring(s.length()-10) + "' (len=" + s.length() + ")";
    }

    public static String encrypt(String data) {
        return encryptDES(data, "123456789e12345abcdefQhYJ5FHgkro", new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
    }

    public static String desEncrypt(String data) {
        return decryptDES(data, "123456789e12345abcdefQhYJ5FHgkro", new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
    }
}
