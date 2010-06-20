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
 * The Helium model. The model store all the current
 * page stream. it's capable to send stream data to
 * Helium RCP application.
 *
 * @author Kevin Pollet
 */

var heliumModel = {
    m_streamList : [],
    m_pageStatus : PageStatus.LOADED,
    m_testReceiver : new TestReceiver(),
    m_waitingSendStreamList : [],
    m_launchDate : null,
    m_spy : new StreamSpy(),
    m_rcpChecker : new RcpChecker(),

    /**
     * Init the model.
     */
    init : function() {
        this.m_spy.addStreamSpyListener(this);
        this.m_rcpChecker.addRcpListener(this);

        this.m_spy.start();
        this.m_rcpChecker.start();
    },

    /**
     * Destroy the model.
     */
    destroy : function() {
        this.m_spy.stop();
        this.m_rcpChecker.stop();
    },

    getRcpChecker : function() {
        return this.m_rcpChecker;
    },

    getStreamSpy : function() {
        return this.m_spy;
    },

    /**
     * Page Status
     */
    getStatus : function() {
        return this.m_pageStatus;
    },

    setStatus : function(pageStatus) {
        this.m_pageStatus = pageStatus;
    },

    /**
     * Stream List
     */
    /**
     * Get the stream object list.
     * @return the stream object list
     */
    getStreamList : function() {
        return this.m_streamList;
    },

    /**
     * Get a Stream object in the list.
     * @param url the stream url
     */
    getStreamObject : function(url) {
        var result = null;

        for (var i=0; result == null && i < this.m_streamList.length; i++) {
            if (url == this.m_streamList[i].getUrl()) {
                result = this.m_streamList[i];
            }
        }

        return result;
    },

    /**
     * Add a StreamObject to the list.
     * @param streamObject the stream object
     */
    addStreamObject : function(streamObject) {
        this.m_streamList.push(streamObject);
    },

    /**
     * Remove a StreamObject from the list.
     * @param streamObject the stream object or the stream url
     * @return the stream removed
     */
    removeStreamObject : function(streamObject) {
        var object = null;
        var url = null;

        if (streamObject instanceof StreamObject) {
            url = streamObject.getUrl();
        } else if (typeof streamObject == "string") {
            url = streamObject;
        }

        //If there is an url to search
        if (url !=null) {
            var index = -1;

            for (var i=0; object == null && i < this.m_streamList.length; i++) {
                if (url == this.m_streamList[i].getUrl()) {
                    object = this.m_streamList[i];
                    index = i;
                }
            }

            //The stream object was found
            if (index != -1) {
                for (var j=index; j < (this.m_streamList.length-1); j++) {
                    this.m_streamList[j] = this.m_streamList[j+1];
                }

                //Erase the last case
                this.m_streamList.pop();
            }
        }

        return object;
    },

    /**
     * Clear the StreamObject list.
     */
    clearStreamObjectList : function() {
        this.m_streamList.clear();
    },

    /**
     * Waiting Send Stream List
     */
    getWaitingSendStreamList : function() {
        return this.m_waitingSendStreamList;
    },

    clearWaitingSendStreamList : function() {
        this.m_waitingSendStreamList.clear();
    },

    /**
     * RCP
     */
    getWebStartLaunchDate : function() {
        return this.m_launchDate;
    },

    launchWebRcp : function() {
        window.frames["webstart"].location.href = "http://forge.serli.com/helium/helium.jnlp";
        this.m_launchDate = new Date();
    },

    stopWebRcpLaunching : function() {
        this.clearWaitingSendStreamList();
        this.m_launchDate = null;
    },

    sendStream : function(url) {
        if (this.m_rcpChecker.isRunning()){
            var serverName = prefs.getChar("servername");
            var serverPort = prefs.getInt("serverport");

            var s_url = "http://" + serverName + ":" + serverPort + "/test/?url=" + escape(url);
            var object = this.getStreamObject(url);
            var headers = {"content-type" : object.getCType()};

            var callback = JSUtil.bind(this.m_testReceiver, this.m_testReceiver.handle);
            NetworkUtil.sendAsyncData("POST", s_url, headers, callback, object.getData());
            
        }else{
            this.m_waitingSendStreamList.push(url);
        }
    },

    /**
     * Listener
     */
    spyStarted : function(event) {
        this.clearStreamObjectList();
    },

    spyStopped : function(event) {
    },

    newStream : function(event) {
        //Do Not RCP Request
        var serverName = prefs.getChar("servername");
        var serverPort = prefs.getInt("serverport");

        if (event.getUrl() != "" &&
            this.getStreamObject(event.getUrl()) == null &&
            event.getUrl().indexOf(serverName + ":" + serverPort) == -1) {

            this.addStreamObject(new StreamObject(event.getUrl(), event.getCType(), event.getData()));
        }
    },

    newPage : function(event) {
        this.clearStreamObjectList();
        this.clearWaitingSendStreamList();
        this.setStatus(PageStatus.LOADING);

        //Notify the RCP application that a new page is loading
        var serverName = prefs.getChar("servername");
        var serverPort = prefs.getInt("serverport");

        NetworkUtil.sendAsyncData("GET", "http://" + serverName + ":" + serverPort + "test/?command=close");
    },

    pageLoaded : function(event) {
        this.setStatus(PageStatus.LOADED);
    },

    rcpStateChanged : function(state) {
        if (state) { //The Rcp application runs
            for (var i = 0; i < this.m_waitingSendStreamList.length; i++) {
                this.sendStream(this.m_waitingSendStreamList[i]);
            }

            this.clearWaitingSendStreamList();
            this.m_launchDate = null;
        }
    }

};
