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
package com.thoughtworks.selenium.proxy;

import junit.framework.TestCase;

import java.io.IOException;

/**
 * @version $Id: RemoveServerNameForSameServerTargetCommandTest.java,v 1.3 2004/11/15 23:37:53 ahelleso Exp $
 */
public class RemoveServerNameForSameServerTargetCommandTest extends TestCase {

    public void testIsARequestModificationCommand() {
        assertTrue(RequestModificationCommand.class.isAssignableFrom(RemoveServerNameForSameServerTargetCommand.class));
    }

    public void testServerNameAndProtocolRemovedFromUrlIfLocalHost() throws IOException {
        String dir = "/site/";
        String host = "localhost:8000";
        String uri = host + dir;
        HTTPRequest httpRequest = new SeleniumHTTPRequest("GET: http://" + uri + HTTPRequest.CRLF);
        RemoveServerNameForSameServerTargetCommand command = new RemoveServerNameForSameServerTargetCommand();
        command.execute(httpRequest);
        assertEquals(dir, httpRequest.getUri());

    }
}
