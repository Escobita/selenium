package org.openqa.selenium;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestResults {
    private static Map<String, Map<TestConfig, Boolean>> results = new ConcurrentHashMap<String, Map<TestConfig, Boolean>>();

    public static Map<String, Map<TestConfig, Boolean>> getResults() {
        return results;
    }

    public static void putResult(String name, boolean pass, TestConfig tc) {
        Map<TestConfig, Boolean> set = results.get(name);
        if (set == null) {
            set = new ConcurrentHashMap<TestConfig, Boolean>();
            results.put(name, set);
        }

        set.put(tc, pass);
    }
}
