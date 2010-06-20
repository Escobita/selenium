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
 * The test receiver object is capable to receive
 * Helium RCP application response and to put it
 * in the Selenium scenario.
 * 
 * @author Kevin Pollet
 */
function TestReceiver() {
    
	if (typeof TestReceiver.initialized == "undefined") {

		TestReceiver.prototype.handle = function(response){
			
			if (response.readyState == 4 && response.status != 200 ) {
              alert("Status code : "+response.status+"\n"+response.statusText);

            } else if (response.readyState == 4) {
				
				var xml = response.responseXML;
				
				if (typeof xml != "undefined" && xml.childNodes.length == 1) {
					var seq = xml.childNodes[0];
					var testArray = seq.getElementsByTagName("test");

                    for (var i = 0; i < testArray.length; i++){
                        var type = testArray[i].getAttribute("type");
                        switch(type) {
                            case "text" : this.parseTextResponse(testArray[i]); break;
                            case "pdf" : this.parsePDFResponse(testArray[i]); break;
                            case "picture" : this.parsePictureResponse(testArray[i]); break;
                        }
                    }
                }
			}
		};		

		TestReceiver.prototype.parsePDFResponse = function(test){
			var params = test.getElementsByTagName("param");
			var paramsString = "";			
			var text = "";

			for (var i=0; i < params.length; i++){
				if (params[i].getAttribute("name") != "text") {
                  paramsString += "&" + params[i].getAttribute("name") + "=" + params[i].textContent;
                } else {
                  text = params[i].textContent.replace(/\n/g,"%0A");
                }
			}
			
			var command = new Command("verifyTextInPDF",paramsString, text);
			var index = window.editor.treeView.getRecordIndex();
			
			window.editor.treeView.insertAt(index,command);			
			window.editor.treeView.rowInserted(index);
		};

		TestReceiver.prototype.parsePictureResponse = function(test){
			var params = test.getElementsByTagName("param");			
			var paramsString = "";			
			var text = "";

			for (var i=0; i < params.length; i++){
				
				if (params[i].getAttribute("name") != "text") {
                  paramsString += "&" + params[i].getAttribute("name") + "=" + params[i].textContent;
                } else {
                  text = params[i].textContent.replace(/\n/g,"%0A");  
                }
			}
			
			var command = new Command("verifyTextInPicture",paramsString, text);
			var index = window.editor.treeView.getRecordIndex();
			
			window.editor.treeView.insertAt(index,command);			
			window.editor.treeView.rowInserted(index);
		};

		TestReceiver.prototype.parseTextResponse = function(test) {
			var params = test.getElementsByTagName("param");			
			var paramsString = "";			
			var text = "";

			for (var i=0; i < params.length; i++){
				if (params[i].getAttribute("name") != "text") {
                  paramsString += "&" + params[i].getAttribute("name") + "=" + params[i].textContent;
                } else {
                  text = params[i].textContent.replace(/\n/g,"%0A");  
                }
			}
			
			var command = new Command("verifyTextInStream", paramsString, text);
			var index = window.editor.treeView.getRecordIndex();
			
			window.editor.treeView.insertAt(index, command);
			window.editor.treeView.rowInserted(index);			
		};

		TestReceiver.initialized = true;
	}
}