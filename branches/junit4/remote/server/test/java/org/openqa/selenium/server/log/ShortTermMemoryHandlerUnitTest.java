package org.openqa.selenium.server.log;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * {@link org.openqa.selenium.server.log.ShortTermMemoryHandler} unit test class.
 */
public class ShortTermMemoryHandlerUnitTest {

    @Test public void recordsReturnsAnEmptyArrayWhenNoRecordHasBeenAdded() {
        final ShortTermMemoryHandler handler;

        handler = new ShortTermMemoryHandler(1, Level.FINEST, null);
        assertNotNull(handler.records());
        assertEquals(0, handler.records().length);
    }

    @Test public void recordsReturnsTheAddedRecordWhenASingleOneIsPublished() {
        final ShortTermMemoryHandler handler;
        final LogRecord theLogRecord;

        handler = new ShortTermMemoryHandler(1, Level.FINEST, null);
        theLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(theLogRecord);
        assertNotNull(handler.records());
        assertEquals(1, handler.records().length);
        assertEquals(theLogRecord, handler.records()[0]);
    }

    @Test public void recordsIsEmptyWhenAddedRecordIsLowerThanTheMinimumLevel() {
        final ShortTermMemoryHandler handler;
        final LogRecord theLogRecord;

        handler = new ShortTermMemoryHandler(1, Level.INFO, null);
        theLogRecord = new LogRecord(Level.FINE, "");
        handler.publish(theLogRecord);
        assertNotNull(handler.records());
        assertEquals(0, handler.records().length);
    }

    @Test public void recordsIsEmptyWhenAddedRecordIsEqualToTheMinimumLevel() {
        final ShortTermMemoryHandler handler;
        final LogRecord theLogRecord;

        handler = new ShortTermMemoryHandler(1, Level.INFO, null);
        theLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(theLogRecord);
        assertNotNull(handler.records());
        assertEquals(1, handler.records().length);
        assertEquals(theLogRecord, handler.records()[0]);
    }

    @Test public void recordsReturnsTheTwoAddedRecordWhenATwoRecordsArePublishedAndCapacityIsNotExceeded() {
        final ShortTermMemoryHandler handler;
        final LogRecord firstLogRecord;
        final LogRecord secondLogRecord;

        handler = new ShortTermMemoryHandler(2, Level.FINEST, null);
        firstLogRecord = new LogRecord(Level.INFO, "");
        secondLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(firstLogRecord);
        handler.publish(secondLogRecord);
        assertNotNull(handler.records());
        assertEquals(2, handler.records().length);
        assertEquals(firstLogRecord, handler.records()[0]);
        assertEquals(secondLogRecord, handler.records()[1]);
    }

    @Test public void recordsOnlyReturnsTheLastRecordWhenATwoRecordsArePublishedAndCapacityIsExceeded() {
        final ShortTermMemoryHandler handler;
        final LogRecord firstLogRecord;
        final LogRecord secondLogRecord;

        handler = new ShortTermMemoryHandler(1, Level.FINEST, null);
        firstLogRecord = new LogRecord(Level.INFO, "");
        secondLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(firstLogRecord);
        handler.publish(secondLogRecord);
        assertNotNull(handler.records());
        assertEquals(1, handler.records().length);
        assertEquals(secondLogRecord, handler.records()[0]);
    }

    @Test public void recordsOnlyReturnsTheLastTwoRecordsWhenThreeRecordsArePublishedAndCapacityIsExceeded() {
        final ShortTermMemoryHandler handler;
        final LogRecord firstLogRecord;
        final LogRecord secondLogRecord;
        final LogRecord thirdLogRecord;

        handler = new ShortTermMemoryHandler(2, Level.FINEST, null);
        firstLogRecord = new LogRecord(Level.INFO, "");
        secondLogRecord = new LogRecord(Level.INFO, "");
        thirdLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(firstLogRecord);
        handler.publish(secondLogRecord);
        handler.publish(thirdLogRecord);
        assertNotNull(handler.records());
        assertEquals(2, handler.records().length);
        assertEquals(secondLogRecord, handler.records()[0]);
        assertEquals(thirdLogRecord, handler.records()[1]);
    }

    @Test public void recordsOnlyReturnsTheLastRecordWhenThreeRecordsArePublishedAndCapacityIsOne() {
        final ShortTermMemoryHandler handler;
        final LogRecord firstLogRecord;
        final LogRecord secondLogRecord;
        final LogRecord thirdLogRecord;

        handler = new ShortTermMemoryHandler(1, Level.FINEST, null);
        firstLogRecord = new LogRecord(Level.INFO, "");
        secondLogRecord = new LogRecord(Level.INFO, "");
        thirdLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(firstLogRecord);
        handler.publish(secondLogRecord);
        handler.publish(thirdLogRecord);
        assertNotNull(handler.records());
        assertEquals(1, handler.records().length);
        assertEquals(thirdLogRecord, handler.records()[0]);
    }

    @Test public void afterCloseAllRecordsAreCleared() {
        final ShortTermMemoryHandler handler;
        final LogRecord firstLogRecord;
        final LogRecord secondLogRecord;

        handler = new ShortTermMemoryHandler(2, Level.FINEST, null);
        firstLogRecord = new LogRecord(Level.INFO, "");
        secondLogRecord = new LogRecord(Level.INFO, "");
        handler.publish(firstLogRecord);
        handler.publish(secondLogRecord);
        handler.close();
        assertNotNull(handler.records());
        assertEquals(0, handler.records().length);
    }

    @Test public void formattedRecordsReturnsAnEmptyStringWhenThereIsNoRecord() {
        final ShortTermMemoryHandler handler;

        handler = new ShortTermMemoryHandler(1, Level.INFO, null);
        assertEquals("", handler.formattedRecords());

    }

    @Test public void formattedRecords() {
        final ShortTermMemoryHandler handler;
        final LogRecord firstLogRecord;
        final LogRecord secondLogRecord;
        final Formatter formatter;

        formatter = new Formatter() {
            public String format(LogRecord record) {
                return "[FORMATTED] " + record.getMessage();
            }
        };
        handler = new ShortTermMemoryHandler(2, Level.INFO, formatter);
        firstLogRecord = new LogRecord(Level.INFO, "First log message");
        secondLogRecord = new LogRecord(Level.INFO, "Second log message");
        handler.publish(firstLogRecord);
        handler.publish(secondLogRecord);
        assertEquals("[FORMATTED] First log message\n" +
                     "[FORMATTED] Second log message\n", handler.formattedRecords());

    }

}
