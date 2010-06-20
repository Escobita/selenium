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
 * Helium Tab JSON object like Selenium LogView, ...
 * Look at Selenium "editor.js" file.
 *
 * @author Kevin Pollet
 */

var heliumTab = {
    name : "helium",

    show : function() {

        window.document.getElementById(this.name + "_view").hidden = false;
        window.document.getElementById(this.name + "_tab").setAttribute("selected", "true");
    },

    hide : function() {
        
        window.document.getElementById(this.name + "_view").hidden = true;
        window.document.getElementById(this.name + "_tab").removeAttribute("selected");
    }
};