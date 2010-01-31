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
 * The helium view JavaScript
 * 
 * @author Kevin Pollet
 */

//Create The Helium variable

window.spy = new StreamSpy(); //create the stream spy
window.rcp_checker = new RcpChecker(); //create the 
window.heliummodel = new PageStream( window.spy, window.rcp_checker); //create the helium model
		
//View model Status listening

	function pageStatus( arguments ){
		
		var loading = document.getElementById("loading");
		var loaded = document.getElementById("loaded");

		if( arguments[0] == PageStatus.LOADED ){
			
			loading.hidden = true;
			loaded.hidden = false;
		
		}else if ( arguments[0] == PageStatus.LOADING ){
		
			loaded.hidden = true;
			loading.hidden = false;
		}	
	}
	
	JSUtil.observe( window.heliummodel, "setStatus", pageStatus  );

//View model data listening

	function newStream( arguments ){

		var list = document.getElementById("helium_list");
		
		var item = list.appendItem( arguments[0].getUrl() );
			item.label = arguments[0].getUrl();
			item.tooltip = "info";
	}
		
	function clear(){
		
		var button = document.getElementById("helium_send"); 
			button.disabled = "true";
			
		var list = document.getElementById("helium_list");
		
		while ( list.itemCount >= 1 )
				list.removeItemAt(0);       
	}
	
	JSUtil.observe( window.heliummodel, "addStreamObject", newStream );
	JSUtil.observe( window.heliummodel, "removeAllStreamObject", clear );

//RCP application state listening	
	
	//a usefull toggle method
	function toggleStatus( status ){ 
		
		var strings = document.getElementById("he_strings");
		
		if( status == "statusLoading" ) document.getElementById("status_icon").src = "chrome://helium/content/view/loading.gif";
		else if( status == "statusOk") document.getElementById("status_icon").src = "chrome://helium/content/view/circle_green.png";
		else if( status == "statusNotOk") document.getElementById("status_icon").src = "chrome://helium/content/view/circle_red.png";
		
		document.getElementById("status_text").value = strings.getString(status);
	}	
	
	function rcpState( args ){

		if( args[0] == true ) toggleStatus("statusOk");
		else toggleStatus("statusNotOk");
	}
	
	JSUtil.observe( window.rcp_checker, "setRunning", rcpState );
	
//Event fired when the tooltip is being shown
	
	function show( event ){
		
		var listitem = document.tooltipNode;
		var streamobj = heliummodel.getStreamObject( listitem.getAttribute("label") );
		
		//Set stream content-type and size
		
			document.getElementById("ctype").value = streamobj.getCType();
			document.getElementById("size").value = streamobj.getDataSize()+" byte(s)";

		//show image if needed

			if( streamobj.getCType().indexOf("image") != -1 ){

				var type = streamobj.getCType().substring( streamobj.getCType().indexOf("/") + 1 );
		
				var img = document.getElementById("html_image");
				img.onload = function (){

						var w = img.naturalWidth;
						var h = img.naturalHeight;
	
						var im = document.getElementById("imsize");	
							im.value = w+"x"+h;
				
						var iconic = document.getElementById("iconic");
							iconic.src = img.src;
							
						//Calculate image dimension
					
							if( w >= h && w > 200 ){
							
								iconic.width  = 200 + "px"; 
								iconic.height = Math.round( ((h*200)/w) ) +"px";
							
							}else if( h > 200 ){
								
								iconic.width  = Math.round( ((w*200)/h) ) + "px"; 
								iconic.height = 200 + "px";
							
							}else{
								
								iconic.width  ="";
								iconic.height ="";
							}	
							
							document.getElementById("miniature").hidden = false;
							
				};
				
				
				img.src = "data:image/"+type+";base64,"+btoa( streamobj.getData() );
				
			}else document.getElementById("miniature").hidden = true;
							
	}
	
//Send stream to the RCP application (Send stream button event)		
	
	function alertJWS(){
		
		if( !window.rcp_checker.isRunning() ){
		
			var strings = document.getElementById("he_strings");
			var prompts = Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
		         
			var check = {value: false};
			var flags = prompts.BUTTON_TITLE_IS_STRING * prompts.BUTTON_POS_0 +	
						prompts.BUTTON_TITLE_IS_STRING * prompts.BUTTON_POS_1 +
						prompts.BUTTON_TITLE_IS_STRING * prompts.BUTTON_POS_2;
				
			var button = prompts.confirmEx(window, "Question", strings.getString("JWSTimeAlert"), flags, strings.getString("buttonRetry"), 
															   										 	 strings.getString("buttonWait"),
															   										 	 strings.getString("buttonCancel"), null,check);
				
			switch( button ){
				
				case 0 : heliummodel.LaunchWebRcp(); toggleStatus( "statusLoading" ); break;
				case 1 : setTimeout( alertJWS, 60000 );  break;
				case 2 : heliummodel.stopWebRcpLaunching(); toggleStatus( "statusNotOk" ); break;
			}
		}
		
	}
	
	function send(){

		if(  !window.rcp_checker.isRunning() ){
		
			if(  heliummodel.getWebStartLaunchDate() == null ){
				
				toggleStatus( "statusLoading" );
				heliummodel.LaunchWebRcp();
				setTimeout( alertJWS, 60000 );
			}	
		}
		
		//send or cache send stream
				
		var listbox = document.getElementById("helium_list");	
		var item = listbox.selectedItem;

		if( item != null ){

			  var uri = item.getAttribute("label");	
			  heliummodel.sendStream( uri );
		}
		
	}
	
//XUL Window Event (load)	
	
window.addEventListener("load", function(){
			
					
								//Lisbox event
								
									var list = document.getElementById("helium_list");
										list.addEventListener("select", function (){  
										
																			var list = document.getElementById("helium_list")
												
																			if( list.selectedItem != null ){
												 
																				var button = document.getElementById("helium_send"); 
																			    	button.setAttribute("disabled","false");
																			}																			
									
																	    } , false );
									
							
								 //Set status
										
									toggleStatus("statusNotOk");
										
									window.spy.start();	
									window.rcp_checker.startCheckRcpState();
									
		
								}, false );

//XUL Window Event (unload)

window.addEventListener("unload", function(){

									  window.spy.stop();
									  window.rcp_checker.stopCheckRcpState();
		
								   }, false );
	
	
