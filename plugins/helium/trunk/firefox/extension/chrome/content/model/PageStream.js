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
 * The Page stream class is the Helium model. The model store all the current 
 * page stream. He was capable to send stream data to Helium RCP application.
 * 
 * @author Kevin Pollet
 */

/**
 * The Helium model who hold the actual pageStream
 * 
 * @param spy the helium spy
 */

function PageStream( spy , rcp_checker){
	
	this.stream_list = [];
	this.page_status = PageStatus.LOADED;
	this.test_receiver = new TestReceiver();
	
	this.uri_list = [];	
	this.launch_date = null;
	this.rcp_checker = rcp_checker;
	
	spy.addStreamSpyListener( this ); //register a spy listener
	rcp_checker.addRcpListener( this ); //register a rcp listener
	
	if( typeof PageStream.initialized == "undefined" ){
			
			/**
			 * Add a stream object to the Helium model
			 * 
			 * @param stream_obj the stream object
			 */		
		
			PageStream.prototype.addStreamObject = function( stream_obj ){ this.stream_list.push( stream_obj ); };	
	
			/**
			 * Remove a stream object from the Helium model
			 * 
			 * @param stream_obj the stream object or the stream_obj Url
			 */
			
			PageStream.prototype.removeStreamObject = function( stream_obj ){
			
				var index = -1;
				var url = (typeof stream_obj == string) ? stream_obj : stream_obj.getUrl();
					
				for( var i = 0 ; index == -1 && i < this.stream_list.length ; i++ ){
				
					if( url ==  this.stream_list[i].getUrl() )
						index = i;				
				}			
				
				if( index != -1 ){
							
					for( var i = index ; i < (this.stream_list.length-1) ; i++)
						this.stream_list[i] = this.stream_list[i+1];
				
					this.stream_list.pop();//erase the last case
				}
				
			};
	
			/**
			 * Clear the Helium model
			 */
			
			PageStream.prototype.removeAllStreamObject = function(){ this.stream_list.clear(); };
		
			PageStream.prototype.removeAllWaitingStream = function(){ this.uri_list.clear(); };
			
			PageStream.prototype.getStreamList = function(){ return this.stream_list; };
		
			PageStream.prototype.getStatus = function(){ return this.page_status; };
		
			PageStream.prototype.setStatus = function( status ){ this.page_status = status; };
		
			PageStream.prototype.getWebStartLaunchDate = function(){ return this.launch_date; };
			
			PageStream.prototype.LaunchWebRcp = function(){  
				
					window.frames["webstart"].location.href= "http://forge.serli.com/helium/helium.jnlp"; //"http://release.seleniumhq.org/helium/JavaWebStart/helium.jnlp";
					this.launch_date = new Date();
			};
		
			PageStream.prototype.stopWebRcpLaunching = function(){
				
				this.uri_list.clear();
				this.launch_date = null;
			};
			
			/**
			 * Get a stream object
			 * 
			 * @param url the stream url
			 * @return the stream object or null if none
			 */
			
			PageStream.prototype.getStreamObject = function( url ){
		
				var stream_obj = null;
				
				for( var i = 0 ; stream_obj == null && i < this.stream_list.length ; i++ ){
				
					if( url == this.stream_list[i].getUrl() ){
						stream_obj = this.stream_list[i];
					}
				}
							
				return stream_obj;
			};
			
			/**
			 * Send a stream object to the RCP application if it is launched
			 * 
			 * @param url the stream url
			 */
			
			PageStream.prototype.sendStream = function( url ){
			
				if ( this.rcp_checker.isRunning() ){
					
					var server_name = HEPreferences.getServerName();
					var server_port = HEPreferences.getServerPort();
					
					var s_url = "http://"+server_name+":"+server_port+"/test/?url="+escape(url);
					var object = this.getStreamObject( url );
					
					var headers = {};
					headers["content-type"] = object.getCType();
					
					var callback = JSUtil.bind( this.test_receiver, this.test_receiver.handle );
					NetworkUtil.sendAsyncData( "post", s_url, headers, callback  , object.getData() );
					
				}else{
					
					this.uri_list.push(url);			
				}
				
			};
			
			/*
			 * Listen Rcp checker event
			 */
			
			PageStream.prototype.rcpStateChanged = function( state ){ 
				
				if( state ){
						
					for ( var i = 0 ; i < this.uri_list.length ; i++ ){
						this.sendStream( this.uri_list[i] );
					}
						
					this.uri_list.clear();
					this.launch_date = null;
				}
				
			};
			
			/*
			 * Listen the spy event
			 */
			
			PageStream.prototype.spyStarted = function(event){
				
				this.removeAllStreamObject();			
			};
		
			PageStream.prototype.spyStopped = function(event){};
		
			PageStream.prototype.newStream = function(event){
				
				//DO not add RCP application request
				var server_name = HEPreferences.getServerName();
				var server_port = HEPreferences.getServerPort();
			
				if( this.getStreamObject( event.getUrl() ) == null && 
				    event.getUrl().indexOf(server_name+":"+server_port) == -1 && event.getUrl() != ""  ){
				
					var object = new StreamObject( event.getUrl(), event.getCType(), event.getData() );
					this.addStreamObject( object );					
				}				
			};
		
			PageStream.prototype.newPage = function(event){
				
				this.removeAllStreamObject();
				this.removeAllWaitingStream();
				this.setStatus( PageStatus.LOADING );
				
				//Kill RCP APP open editor 
				var server_name = HEPreferences.getServerName();
				var server_port = HEPreferences.getServerPort();
				var s_url = "http://"+server_name+":"+server_port+"/test/?command=close";
				
				NetworkUtil.sendAsyncData( "get", s_url, null, null, null );		
			};
		
			PageStream.prototype.pageLoaded = function( event ){
		
				this.setStatus( PageStatus.LOADED );
			};
		
		
		PageStream.initialized = true;
	}
	
};
