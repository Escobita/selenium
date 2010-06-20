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
 * Stream spy is the spy core who add listener to the Firefox stream
 * The stream spy emits events to prevent when a page load or when a response was
 * captured.
 * 
 * @author Kevin Pollet
 */
  
function StreamSpy(){
	
	this.spy_listener = [];
	this.running = false;
	this.recording = false;
	
	var obs_service = Components.classes["@mozilla.org/observer-service;1"].getService( Components.interfaces.nsIObserverService ); //Stream observer service
	var cache_service = Components.classes["@mozilla.org/network/cache-service;1"].getService(Components.interfaces.nsICacheService); //Cache observer service
	
	if( typeof StreamSpy.initialized == "undefined" ){
		
			/**
			 * If the a listener is already registered this method have no
			 * effect.
			 * 
			 * @param listener the stream spy listener
			 */
		
			StreamSpy.prototype.addStreamSpyListener = function( listener ){
		
				var exist = false;
			
				for( var i = 0 ; !exist && i < this.spy_listener.length ; i++ ){ 

					if( this.spy_listener[i] == listener ){
						exist=true;
					}
				}
			
				if( !exist ) this.spy_listener.push( listener );
			
			};
		
			/**
			 * Remove the stream spy listener
			 * 
			 * @param listener the listener 
			 */
			
			StreamSpy.prototype.removeStreamSpyListener = function( listener ){
		
				var index = -1;
		
				for( var i = 0 ; index == -1 && i < this.spy_listener.length ; i++ ){ 
		
					if( this.spy_listener[i] == listener )
						index = i;
				}
		
				if( index != -1 ){
				
					for( var i = index ; i < (this.spy_listener.length-1) ; i++ )
						this.spy_listener[i] = this.spy_listener[i+1];						
					
				
					this.spy_listener.pop();
				}
		
			};
		
			/**
			 * Start the spy for recording the page stream.
			 * This method have no effect if the spy is already running
			 */
						
			StreamSpy.prototype.start = function(){

				if( !this.running ){
															
						this.running = true;
				
						//Clear cache
						cache_service.evictEntries(Components.interfaces.nsICache.STORE_ON_DISK);
						cache_service.evictEntries(Components.interfaces.nsICache.STORE_IN_MEMORY);						
						//Register listener
				
						obs_service.addObserver( this, "StartDocumentLoad" , false);
						obs_service.addObserver( this, "EndDocumentLoad" , false);	
					
						//Call Listener

						var event = new StreamSpyEvent( this );
					
						for( var i = 0 ; i < this.spy_listener.length ; i++)
							this.spy_listener[i].spyStarted( event );
					
				}
			};
	
			/**
			 * Stop the spy
			 * This method have no effect if the spy doesn't run
			 */
			
			StreamSpy.prototype.stop = function(){

				if( this.running ){
					
					this.running = false;
					
					//Remove stream listener					
					
					if( this.recording ){
					
						this.recording = false;
						
						obs_service.removeObserver( this, "http-on-examine-response");
						//obs_service.removeObserver( this, "http-on-examine-cached-response");
					}
					
					
					obs_service.removeObserver( this, "StartDocumentLoad");
					obs_service.removeObserver( this, "EndDocumentLoad");	
						
					//Call Listener
						
					var event = new StreamSpyEvent(this);
						
					for( var i=0 ; i < this.spy_listener.length ; i++)
						this.spy_listener[i].spyStopped( event );
				}
				
			};
		
			/**
			 * Get the running state of the spy
			 * 
			 * @return true if the spy is running
			 */
			
			StreamSpy.prototype.isRunning = function(){ return this.running; }
			
			/**
			 *  This method is called by Firefox when a response arrives, when a new
			 *  document starts loading or when document is loaded
			 *  
			 *  @param aSubject the XPCom interface
			 *  @param aTopic the event type
			 *  @param aData optional parameter
			 */			
			
			StreamSpy.prototype.observe = function( aSubject, aTopic, aData ){
		
				if( aTopic == "StartDocumentLoad"){

					//Clear cache
					
					cache_service.evictEntries(Components.interfaces.nsICache.STORE_ON_DISK);
					cache_service.evictEntries(Components.interfaces.nsICache.STORE_IN_MEMORY);
				
					
					if( !this.recording ){
						
						this.recording = true;
									
						//Page is loading snif stream
					
						obs_service.addObserver( this, "http-on-examine-response" , false );
						//obs_service.addObserver( this, "http-on-examine-cached-response" , false );
						
					}	
						
					//Call listener	
					
					var event = new StreamSpyEvent(this);
					
					for( var i=0 ; i < this.spy_listener.length ; i++)
						this.spy_listener[i].newPage( event );
	
	
				}else if( aTopic == "EndDocumentLoad" ){

					
					if( this.recording ){
					
						this.recording = false
						
						//Page is loaded
						
							obs_service.removeObserver( this, "http-on-examine-response");
							//obs_service.removeObserver( this, "http-on-examine-cached-response");
										
						//Clear cache
						
							cache_service.evictEntries(Components.interfaces.nsICache.STORE_ON_DISK);
							cache_service.evictEntries(Components.interfaces.nsICache.STORE_IN_MEMORY);
					}	
						
					//Send event
					
						var event = new StreamSpyEvent(this);
				
						for( var i=0 ; i < this.spy_listener.length ; i++)
							this.spy_listener[i].pageLoaded( event );
	
	
				}else if( aTopic == "http-on-examine-response" || aTopic == "http-on-examine-cached-response" ){
					
					//Read data
					var trace = aSubject.QueryInterface( Components.interfaces.nsITraceableChannel );
					
					var stream_handler = new StreamHandler( this );
					var old_listener = trace.setNewListener( stream_handler );
					stream_handler.setOldListener( old_listener );
				}	
			
			};	
		
			/**
			 * Call when a stream handler finish sniffing a response and
			 * send an event
			 * 
			 * @param the request url
			 * @param ctype the stream content-type
			 * @param data response data
			 */
			
			StreamSpy.prototype.dataCapturated = function( url, ctype, data ){
				
					//Call Listener
				
						var event = new StreamSpyEvent(this, url, ctype, data);
							
						for( var i=0 ; i < this.spy_listener.length ; i++)
							this.spy_listener[i].newStream( event );
				
			};		
			
		StreamSpy.initialized = true;
	}
	
}
