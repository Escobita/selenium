package org.openqa.selenium;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestResults {
    private static Map<String, Map<TestConfig, String>> results = new ConcurrentHashMap<String, Map<TestConfig, String>>();

    public static Map<String, Map<TestConfig, String>> getResults() {
        return results;
    }

    public static void putResult(String name, String result, TestConfig tc) {
        Map<TestConfig, String> set = results.get(name);
        if (set == null) {
            set = new ConcurrentHashMap<TestConfig, String>();
            results.put(name, set);
        }

        set.put(tc, result);
    }
}
