/**
 * Copyright 2009 - SERLI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The stream object class. A stream object
 * represents an HTTP exchange.
 *
 * @author Kevin Pollet
 */

 /**
 * Stream Object Constructor.
 *
 * @param url the url of the stream
 * @param ctype the content type of the url
 * @param data the data of the stream
 */
function StreamObject(url, ctype, data) {
    this.m_url = url;
    this.m_data = data;
    this.m_ctype = ctype;
	
    if (typeof StreamObject.initialized == "undefined") {

        /**
         * Get the stream url.
         * @return the stream url
         */
        StreamObject.prototype.getUrl = function() {
            return this.m_url;
        };

        /**
         * Get the stream content type.
         * @return the content type
         */
        StreamObject.prototype.getCType = function() {
            return this.m_ctype;
        };

        /**
         * Get the stream data.
         * @return the stream data
         */
        StreamObject.prototype.getData = function() {
            return this.m_data;
        };

        /**
         * Get the stream data size.
         * @return the stream data size
         */
        StreamObject.prototype.getDataSize = function() {
            return this.m_data.length;
        };

        /**
         * Get the string representation of the stream (url).
         * @return the string representation
         */
        StreamObject.prototype.toString = function() {
            return this.m_url;
        };

        //Initialize the object only one time
        StreamObject.initialized = true;
    }
}
