/*
 * Copyright 2004 ThoughtWorks, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.thoughtworks.selenium.funnel;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import java.io.IOException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.1 $
 */
public class MockOutputStreamTest extends TestCase {
    public void testShouldPassOnWriteExpectedContent() throws IOException {
        MockOutputStream out = new MockOutputStream("hello");
        out.write("hello".getBytes());
        out.verify();
    }

    public void testShouldFailOnWriteUnexpectedContent() throws IOException {
        MockOutputStream out = new MockOutputStream("hello");
        out.write("bonjour".getBytes());
        try {
            out.verify();
        } catch (AssertionFailedError expected) {
            assertEquals("expected:<hello> but was:<bonjour>", expected.getMessage());
            return;
        }
        fail();
    }
}
