package com.ved.framework.utils;

import com.tencent.mmkv.MMKV;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;

public final class MMKVUtils {
    private static final Map<String, MMKVUtils> sSPMap = new HashMap<>();
    private final MMKV mmkv;

    public static MMKVUtils getInstance() {
        return getInstance("");
    }

    public static MMKVUtils getInstance(@Nullable String spName) {
        if (isSpace(spName)) spName = "mmkv_utils";
        MMKVUtils sp = sSPMap.get(spName);
        if (sp == null) {
            sp = new MMKVUtils(spName);
            sSPMap.put(spName, sp);
        }
        return sp;
    }

    private MMKVUtils(@Nullable final String spName) {
        mmkv = MMKV.mmkvWithID(spName, MMKV.MULTI_PROCESS_MODE);
    }

    private static boolean isSpace(@Nullable final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void put(@Nullable String key, @Nullable Object value) {
        if (key == null) return;

        if (value instanceof String) {
            mmkv.encode(key, (String) value);
        } else if (value instanceof Integer) {
            mmkv.encode(key, (Integer) value);
        } else if (value instanceof Boolean) {
            mmkv.encode(key, (Boolean) value);
        } else if (value instanceof Float) {
            mmkv.encode(key, (Float) value);
        } else if (value instanceof Long) {
            mmkv.encode(key, (Long) value);
        } else if (value instanceof Double) {
            mmkv.encode(key, (Double) value);
        } else if (value instanceof byte[]) {
            mmkv.encode(key, (byte[]) value);
        } else if (value != null) {
            mmkv.encode(key, value.toString());
        }
    }

    public Object get(@Nullable String key, @Nullable Object defaultValue) {
        if (key == null) return defaultValue;

        if (defaultValue instanceof String) {
            return mmkv.decodeString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return mmkv.decodeInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return mmkv.decodeBool(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return mmkv.decodeFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return mmkv.decodeLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Double) {
            return mmkv.decodeDouble(key, (Double) defaultValue);
        } else if (defaultValue instanceof byte[]) {
            return mmkv.decodeBytes(key);
        }
        return null;
    }

    public void putInt(String key, int value) {
        mmkv.encode(key, value);
    }

    public int getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    public int getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public void putBoolean(String key, boolean value) {
        mmkv.encode(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return mmkv.decodeBool(key, false);
    }

    public void putLong(String key, long value) {
        mmkv.encode(key, value);
    }

    public long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    public long getLong(String key) {
        return mmkv.decodeLong(key, 0L);
    }

    public void putFloat(String key, float value) {
        mmkv.encode(key, value);
    }

    public float getFloat(String key, float defaultValue) {
        return mmkv.decodeFloat(key, defaultValue);
    }

    public float getFloat(String key) {
        return mmkv.decodeFloat(key, 0f);
    }

    public void putDouble(String key, double value) {
        mmkv.encode(key, value);
    }

    public double getDouble(String key, double defaultValue) {
        return mmkv.decodeDouble(key, defaultValue);
    }

    public double getDouble(String key) {
        return mmkv.decodeDouble(key, 0.0);
    }

    public void putString(String key, String value) {
        mmkv.encode(key, value);
    }

    public String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    public String getString(String key) {
        return mmkv.decodeString(key, "");
    }

    public void putStringSet(String key, Set<String> value) {
        mmkv.encode(key, value);
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return mmkv.decodeStringSet(key, defaultValue);
    }

    public Set<String> getStringSet(String key) {
        return mmkv.decodeStringSet(key, Collections.emptySet());
    }

    public void putBytes(String key, byte[] value) {
        mmkv.encode(key, value);
    }

    public byte[] getBytes(String key) {
        return mmkv.decodeBytes(key);
    }

    public byte[] getBytes(String key, byte[] defaultValue) {
        return mmkv.decodeBytes(key, defaultValue);
    }

    // ==================== 其他功能方法 ====================

    public boolean contains(String key) {
        return mmkv.containsKey(key);
    }

    public void remove(String key) {
        mmkv.removeValueForKey(key);
    }

    public void removeValuesForKeys(String[] keys) {
        mmkv.removeValuesForKeys(keys);
    }

    public void clear() {
        mmkv.clearAll();
    }

    public Map<String, ?> getAll() {
        return mmkv.getAll();
    }

    public String[] allKeys() {
        return mmkv.allKeys();
    }

    public long totalSize() {
        return mmkv.totalSize();
    }

    public long actualSize() {
        return mmkv.actualSize();
    }
}
