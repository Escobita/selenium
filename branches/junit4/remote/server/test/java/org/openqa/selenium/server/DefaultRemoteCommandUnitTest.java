package org.openqa.selenium.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;


public class DefaultRemoteCommandUnitTest {

    @Test public void parseNoJs() {
        DefaultRemoteCommand drc = new DefaultRemoteCommand("foo", "bar", "baz");
        RemoteCommand parsed = DefaultRemoteCommand.parse(drc.toString());
        assertEquals(drc, parsed);
    }
    
    @Test public void parsePiggyBackedJs() {
        DefaultRemoteCommand drc = new DefaultRemoteCommand("foo", "bar", "baz", "2+2");
        RemoteCommand parsed = DefaultRemoteCommand.parse(drc.toString());
        assertEquals(drc, parsed);
    }
    
    @Test public void evil() {
        DefaultRemoteCommand drc = new DefaultRemoteCommand("\\\"\'\b\n\r\f\t\u2000", "bar", "baz");
        RemoteCommand parsed = DefaultRemoteCommand.parse(drc.toString());
        assertEquals(drc, parsed);
    }
    
    @Test public void unicode() {
        RemoteCommand parsed = DefaultRemoteCommand.parse("json={command:\"\\u2000\",target:\"bar\",value:\"baz\"}");
        DefaultRemoteCommand drc = new DefaultRemoteCommand("\u2000", "bar", "baz");
        assertEquals(drc, parsed);
    }
    
    @Test public void blankStringNoJs() {
        DefaultRemoteCommand drc = new DefaultRemoteCommand("", "", "");
        RemoteCommand parsed = DefaultRemoteCommand.parse(drc.toString());
        assertEquals(drc, parsed);
    }
    
    @Test public void blankStringPiggyBackedJs() {
        DefaultRemoteCommand drc = new DefaultRemoteCommand("", "", "", "");
        RemoteCommand parsed = DefaultRemoteCommand.parse(drc.toString());
        assertEquals(drc, parsed);
    }

    @Test public void equalReturnsFalseWhenComparedWithNull() {
        assertFalse(new DefaultRemoteCommand("", "", "").equals(null));
    }

    @Test public void equalReturnsFalseWhenCommandsDoNotMatch() {
        assertFalse(
            new DefaultRemoteCommand("a command", "", "").equals(
            new DefaultRemoteCommand("another command", "", "")
        ));
    }

    @Test public void equalReturnsFalseWhenFieldsDoNotMatch() {
        assertFalse(
            new DefaultRemoteCommand("", "a field", "").equals(
            new DefaultRemoteCommand("", "another field", "")
        ));
    }

    @Test public void equalReturnsFalseWhenValuesDoNotMatch() {
        assertFalse(
            new DefaultRemoteCommand("", "", "a value").equals(
            new DefaultRemoteCommand("", "", "another value")
        ));
    }

    @Test public void equalReturnsTrueWhenCommandsFieldsAndValuesDoMatch() {
        assertEquals(
            new DefaultRemoteCommand("a command", "a field", "a value"),
            new DefaultRemoteCommand("a command", "a field", "a value")
        );
    }

    @Test public void hascodeIsDifferentWhenCommandsDoNotMatch() {
        assertNotSame(new DefaultRemoteCommand("a command", "", "").hashCode(), new DefaultRemoteCommand("another command", "", "").hashCode());
    }

    @Test public void hascodeIsDifferentWhenFieldsDoNotMatch() {
        assertNotSame(new DefaultRemoteCommand("", "a field", "").hashCode(), new DefaultRemoteCommand("", "another field", "").hashCode());
    }

    @Test public void hascodeIsDifferentWhenValuesDoNotMatch() {
        assertNotSame(new DefaultRemoteCommand("", "", "a value").hashCode(), new DefaultRemoteCommand("", "", "another value").hashCode());
    }

    @Test public void hascodeIsIdenticalWhenCommandsFieldsAndValuesDoMatch() {
        assertEquals(
            new DefaultRemoteCommand("a command", "a field", "a value").hashCode(),
            new DefaultRemoteCommand("a command", "a field", "a value").hashCode()
        );
    }


}
