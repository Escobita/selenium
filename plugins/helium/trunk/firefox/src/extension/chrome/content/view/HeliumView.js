/**
 *  Copyright 2009 - SERLI
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * The HeliumView Class. This Class use the JSON object
 * description.
 *
 * @author Kevin Pollet
 */

var heliumView = {
    m_model : null,

    /**
     * Initialisation.
     */
    init : function() {
        var strings = window.document.getElementById("helium_strings");
        var text = strings.getString("statusNotOk");

        this.setStatusBarTextIcon(text, "chrome://helium/skin/classic/circle_red.png");
    },

    /**
     * Model.
     */
    setModel : function(model) {
        this.m_model = model;
        var instance = this;

        //Adds listener
        JSUtil.observe(this.m_model, "clearStreamObjectList", function(arguments) {
            instance.clearStreamList();
            instance.setBtnSendEnabled(false);
        });

        JSUtil.observe(this.m_model, "setStatus", function(arguments) {
            instance.setPageLoading(arguments[0] == PageStatus.LOADING);
        });

        JSUtil.observe(this.m_model.getRcpChecker(), "setRunning", function(arguments){
            var strings = window.document.getElementById("helium_strings");

            if (arguments[0]) {
                instance.setStatusBarTextIcon(strings.getString("statusOk"),
                        "chrome://helium/skin/classic/circle_green.png");
            } else {
                instance.setStatusBarTextIcon(strings.getString("statusNotOk"),
                        "chrome://helium/skin/classic/circle_red.png");
            }
        });

        JSUtil.observe(this.m_model, "addStreamObject", function(arguments){
            instance.addItemToStreamList(arguments[0].getUrl());
        });
    },

    /**
     * Controller.
     */
    addController : function(controller) {
        window.controllers.appendController(controller);
    },

    removeController : function(controller) {
        window.controllers.removeController(controller);
    },

    /**
     * Page loading status.
     */
    setPageLoading : function(isLoading) {
        var loadingObject = window.document.getElementById('loading');
        loadingObject.hidden = !isLoading;
    },

    /**
     * Send button.
     */
    setBtnSendEnabled : function(enable) {
        var button = window.document.getElementById('helium_send');
        button.disabled = !enable;
    },

    /**
     * Stream List
     */
    addItemToStreamList : function(item_text) {
        var list = window.document.getElementById('helium_list');
        var item = list.appendItem(item_text);
        item.tooltip = "helium_tooltip";
    },

    getStreamListSelectedIndex : function() {
        var list = window.document.getElementById('helium_list');
        return list.selectedIndex;
    },

    getStreamListSelectedItem : function() {
        var list = window.document.getElementById('helium_list');
        return list.selectedItem;
    },

    getStreamListItemCount : function() {
        var list = window.document.getElementById('helium_list');
        return list.itemCount;
    },

    clearStreamList : function() {
        var list = window.document.getElementById('helium_list');
        while (list.itemCount >= 1) {
            list.removeItemAt(0);
        }
        
    },

    /**
     * ToolTip.
     */
    showTooltip : function() {

        var item = window.document.tooltipNode; //Item tootltip information
        var streamobj = this.m_model.getStreamObject(item.getAttribute("label"));

        //Set tooltip stream content-type and size
        window.document.getElementById("ctype").value = streamobj.getCType();
        window.document.getElementById("size").value = streamobj.getDataSize()+" byte(s)";

        //Show image if this is an image stream
        if (streamobj.getCType().indexOf("image") != -1) {

            var type = streamobj.getCType().substring(streamobj.getCType().indexOf("/") + 1);
            var img = document.getElementById("html_image");

            img.onload = function (){

                var w = img.naturalWidth;
                var h = img.naturalHeight;

                var imsize = document.getElementById("imsize");
                imsize.value = w + "x" + h;

                var iconic = document.getElementById("iconic");
                iconic.src = img.src;

                //Calculate image dimension
                if (w >= h && w > 200) {
                    iconic.width  = 200 + "px";
                    iconic.height = Math.round( ((h*200)/w) ) +"px";
                } else if (h > 200) {
                    iconic.width  = Math.round( ((w*200)/h) ) + "px";
                    iconic.height = 200 + "px";
                } else {
                    iconic.width  = "";
                    iconic.height = "";
                }

                document.getElementById("helium_tooltip_img").hidden = false;
            };


            img.src = "data:image/" + type + ";base64," + btoa(streamobj.getData());

        } else {
            document.getElementById("helium_tooltip_img").hidden = true;
        }
    },

    /**
     * Status Bar.
     */
    setStatusBarText : function(text) {
        if (typeof text == "string") {
            var status_label = window.document.getElementById('status_text');
            status_label.value = text;
        }
    },

    setStatusBarTextIcon : function(text, icon_url) {
        if (typeof text == "string") {
            var status_label = window.document.getElementById('status_text');
            status_label.value = text;
        }

        if (typeof icon_url != "undefined") {
            var status_icon = window.document.getElementById('status_icon');
            status_icon.src = icon_url;
        }
    },

    /**
     * RCP function.
     */
    showRCPDialog : function() {
        if (!heliumView.m_model.getRcpChecker().isRunning()) {

            var strings = document.getElementById("helium_strings");
            var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);

            var check = {
                value: false
            };
            
            var flags = prompts.BUTTON_TITLE_IS_STRING * prompts.BUTTON_POS_0 +
            prompts.BUTTON_TITLE_IS_STRING * prompts.BUTTON_POS_1 +
            prompts.BUTTON_TITLE_IS_STRING * prompts.BUTTON_POS_2;

            var button = prompts.confirmEx(window, "Question", strings.getString("JWSTimeAlert"), flags, strings.getString("buttonRetry"),
                strings.getString("buttonWait"),
                strings.getString("buttonCancel"), null,check);

            switch (button) {
                case 0 :
                    heliumView.m_model.launchWebRcp();
                    heliumView.setStatusBarTextIcon(strings.getString("statusLoading"), "chrome://helium/skin/classic/loading.gif");
                    break;
                case 1 :
                    setTimeout(heliumView.showRCPDialog, 60000);
                    break;
                case 2 :
                    heliumView.m_model.stopWebRcpLaunching();
                    heliumView.setStatusBarTextIcon(strings.getString("statusNotOk"), "chrome://helium/skin/classic/circle_red.png");
                    break;
            }

        }

    }

};




