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

/**
 * @author Paul Hammant
 * @version $Revision: 411 $
 */
public class SingleEntryAsyncQueue {

    private Object thing;
    private boolean available;

    public synchronized Object get() {
        int retries = 0;
        while (available == false) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                continue;
            }
            if (available == false & retries > 25) {
                throw new SeleniumCommandTimedOutException();
            }
            retries++;
        }
        available = false;
        return thing;
    }

    public String toString() {
        if (!available)
            return "[empty queue]";

        return "[queue containing '" + thing + "']";
    }

    public synchronized void put(Object thing) {
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        this.thing = thing;
        available = true;
        notifyAll();
    }

}
