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

package org.openqa.selenium.server;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.util.StringUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Paul Hammant
 * @version $Revision: 674 $
 */
public class SeleniumDriverResourceHandler extends ResourceHandler {

    private final Map<String, SeleneseQueue> queues = new HashMap<String, SeleneseQueue>();

    private String getParam(HttpRequest req, String name) {
        List parameterValues = req.getParameterValues(name);
        if (parameterValues == null) {
            return null;
        }
        return (String) parameterValues.get(0);
    }

    public void handle(String s, String s1, HttpRequest req, HttpResponse res) throws HttpException, IOException {
        res.setField(HttpFields.__ContentType, "text/plain");
        setNoCacheHeaders(res);

        OutputStream out = res.getOutputStream();
        ByteArrayOutputStream buf = new ByteArrayOutputStream(1000);
        Writer writer = new OutputStreamWriter(buf, StringUtil.__ISO_8859_1);
        String seleniumStart = getParam(req, "seleniumStart");
        String commandResult = getParam(req, "commandResult");
        String commandRequest = getParam(req, "commandRequest");
        String sessionId = getParam(req, "sessionId");

        if (commandResult != null || (seleniumStart != null && seleniumStart.equals("true"))) {
            //System.out.println("commandResult = " + commandResult);

            SeleneseQueue queue = getQueue(sessionId);
            SeleneseCommand sc = queue.handleCommandResult(commandResult);
            //System.out.println("Sending next command: " + sc.getCommandString());
            writer.flush();
            writer.write(sc.getCommandString());
            for (int pad = 998 - buf.size(); pad-- > 0;) {
                writer.write(" ");
            }
            writer.write("\015\012");
            writer.flush();
            buf.writeTo(out);

            req.setHandled(true);
        } else if (commandRequest != null) {
            res.setContentType("text/plain");
            String[] values = commandRequest.split("\\|");
            String commandS = "";
            String field = "";
            String value = "";
            if (values.length > 1) {
                commandS = values[1];
            }

            if (values.length > 2) {
                field = values[2];
            }

            if (values.length > 3) {
                value = values[3];
            }

            //System.out.println("commandRequest = " + commandRequest);
            SeleneseQueue queue = getQueue(sessionId);
            String results = queue.doCommand(commandS, field, value);
            System.out.println("Got result: " + results);
            try {
                res.getOutputStream().write(results.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // finally, if the command was testComplete, remove the queue
            if ("testComplete".equals(commandS)) {
                clearQueue(sessionId);
            }

            req.setHandled(true);
        } else {
            //System.out.println("Unexpected: " + req.getRequestURL() + "?" + req.getQuery());
            req.setHandled(false);
        }
    }

    private SeleneseQueue getQueue(String sessionId) {
        synchronized (queues) {
            SeleneseQueue queue = queues.get(sessionId);
            if (queue == null) {
                queue = new SeleneseQueue();
                queues.put(sessionId, queue);
            }

            return queue;
        }
    }

    public void clearQueue(String sessionId) {
        synchronized(queues) {
            queues.remove(sessionId);
        }
    }

    private void setNoCacheHeaders(HttpResponse res) {
        res.setField(HttpFields.__CacheControl, "no-cache");
        res.setField(HttpFields.__Pragma, "no-cache");
        res.setField(HttpFields.__Expires, "-1");
    }
}
