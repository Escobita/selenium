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
 * This is the Helium Controller.
 * 
 * @author Kevin Pollet
 */

var heliumController = {

    m_model : null,
    m_view : null,

    setView : function(view) {
        this.m_view = view;
    },

    setModel : function(model) {
        this.m_model = model;
    },

    supportsCommand : function(cmd) {

        switch(cmd) {
            case "cmd_send":
            case "cmd_select":
            case "cmd_show_tooltip":

            return true;
        }

        return false;
    },

    isCommandEnabled : function(cmd) {

        switch(cmd) {
            case "cmd_send":
                return this.m_view.getStreamListSelectedItem() != null;

            case "cmd_select":
                return this.m_view.getStreamListItemCount() > 0;

            case "cmd_show_tooltip":
                return true;

        }

        return false;

    },

    doCommand : function(cmd) {

        switch(cmd) {
            //Select an item in the Stream List
            case "cmd_select": {

                this.m_view.setBtnSendEnabled(true);
                
            } break;

            //Send the stream to the RCP application
            case "cmd_send": {
                if (!this.m_model.getRcpChecker().isRunning()) {
                    if (this.m_model.getWebStartLaunchDate() == null) {
                        var strings = window.document.getElementById("helium_strings");
                        this.m_view.setStatusBarTextIcon(strings.getString("statusLoading"), "chrome://helium/skin/classic/loading.gif");

                        this.m_model.launchWebRcp();
                        setTimeout(heliumView.showRCPDialog, 60000);
                    }
                }

                var item = this.m_view.getStreamListSelectedItem();
                if (item != null ){
                    this.m_model.sendStream(item.getAttribute("label"));
                }

            } break;

           //Show the stream tooltip
           case "cmd_show_tooltip": {

                this.m_view.showTooltip();
               
           } break;
        }
    },

    onEvent : function(event) {        
    }

};