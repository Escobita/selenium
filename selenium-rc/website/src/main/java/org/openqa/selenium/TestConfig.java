package org.openqa.selenium;

public class TestConfig {
    Browser browser;
    OS os;

    public TestConfig(Browser browser, OS os) {
        this.browser = browser;
        this.os = os;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestConfig that = (TestConfig) o;

        return browser == that.browser && os == that.os;
    }

    public int hashCode() {
        int result;
        result = browser.hashCode();
        result = 31 * result + os.hashCode();
        return result;
    }
}
