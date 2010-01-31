/* 
* Copyright 2009 SERLI
*
* This file is part of Helium.
*
* Helium is free software: you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free Software Foundation, 
* either version 3 of the License, or(at your option) any later version.
*
* Helium is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with Helium.
*
* If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * The test receiver object is capable to receive Helium RCP application response and to put it
 * in the Selenium scenario
 * 
 * @author Kevin Pollet
 */

function TestReceiver(){

	if( typeof TestReceiver.initialized == "undefined" ){
		
		/*----------------------------------------------------------------------------------------------*/
		
		TestReceiver.prototype.handle = function(response){
			
			if (response.readyState == 4 && response.status != 200 ) alert("Status code : "+response.status+"\n"+response.statusText);
			else if( response.readyState == 4 ){
				
				var xml = response.responseXML;
				
				if( typeof xml != "undefined" && xml.childNodes.length == 1 ){
								
					var seq = xml.childNodes[0];
					var testArray = seq.getElementsByTagName("test");						
							
						for (var i = 0 ; i < testArray.length ; i++){
								
							var type = testArray[i].getAttribute("type"); 
								
							//test on text stream
							if (type == "text") this.parseTextResponse(testArray[i]);
							//test on a PDF stream
							else if (type == "pdf") this.parsePDFResponse(testArray[i]);
							//test on a picture stream
							else if (type == "picture") this.parsePictureResponse(testArray[i]);
								
						}
				
				}
				
			}
			
			
		};		
		
		/*----------------------------------------------------------------------------------------------*/
		
		TestReceiver.prototype.parsePDFResponse = function(test){
			
			var params = test.getElementsByTagName("param");			
			var paramsString = "";			
			var text = "";

			for (var i = 0 ; i < params.length ; i++){
				
				if (params[i].getAttribute("name") != "text") paramsString += "&"+params[i].getAttribute("name")+"="+params[i].textContent;
				else text = params[i].textContent.replace(/\n/g,"%0A");
			}
			
			var command = new Command("verifyTextInPDF",paramsString, text);
			
			var index = window.editor.treeView.getRecordIndex();
			
			window.editor.treeView.insertAt(index,command);			
			window.editor.treeView.rowInserted(index);
			
		};

		/*----------------------------------------------------------------------------------------------*/		
		
		TestReceiver.prototype.parsePictureResponse = function(test){
			
			var params = test.getElementsByTagName("param");			
			var paramsString = "";			
			var text = "";

			for (var i = 0 ; i < params.length ; i++){
				
				if (params[i].getAttribute("name") != "text") paramsString += "&"+params[i].getAttribute("name")+"="+params[i].textContent;				
				else text = params[i].textContent.replace(/\n/g,"%0A");
			}
			
			var command = new Command("verifyTextInPicture",paramsString, text);
			
			var index = window.editor.treeView.getRecordIndex();
			
			window.editor.treeView.insertAt(index,command);			
			window.editor.treeView.rowInserted(index);
			
		};

		/*----------------------------------------------------------------------------------------------*/		
		
		TestReceiver.prototype.parseTextResponse = function(test){
			
			var params = test.getElementsByTagName("param");			
			var paramsString = "";			
			var text = "";

			for (var i = 0 ; i < params.length ; i++){
				
				if (params[i].getAttribute("name") != "text") paramsString += "&"+params[i].getAttribute("name")+"="+params[i].textContent;				
				else text = params[i].textContent.replace(/\n/g,"%0A");
			}
			
			var command = new Command("verifyTextInStream",paramsString, text);
			
			var index = window.editor.treeView.getRecordIndex();
			
			window.editor.treeView.insertAt(index,command);			
			window.editor.treeView.rowInserted(index);			
		
		};			
		
		/*----------------------------------------------------------------------------------------------*/
		
		TestReceiver.initialized = true;
	}	
	
	
}



