package com.ved.framework.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configure {
    private static List<String> url;
    private static int code;

    private Configure() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setUrl(int code,String... url){
        List<String> urlList = new ArrayList<>();
        if (url.length > 0){
            urlList.addAll(Arrays.asList(url));
        }
        Configure.url = urlList;
        Configure.code = code;
    }

    public static List<String> getUrl() {
        if (url != null && url.size() > 0) {
            return url;
        }
        throw new NullPointerException("should be set in net url");
    }

    public static int getCode() {
        return code;
    }
}
