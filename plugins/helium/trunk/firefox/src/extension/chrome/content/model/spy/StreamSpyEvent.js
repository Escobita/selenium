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
 * The stream spy event object.
 * 
 * @author Kevin Pollet
 */
 
 
function StreamSpyEvent( source, url, ctype, data ){

	this.source = source;
	this.url = (typeof url == "undefined") ? null : url;
	this.ctype = (typeof ctype == "undefined") ? null : ctype;
	this.data = (typeof data == "undefined") ? null : data;
	
	if( typeof StreamSpyEvent.initialized == "undefined" ){
	
			/**
			 * Get the event source
			 * 
			 * @return the event source
			 */
		
			StreamSpyEvent.prototype.getSource = function(){ return this.source; };
		
			/**
			 * Get the new stream Url
			 * 
			 * @return the url
			 */
			
			StreamSpyEvent.prototype.getUrl = function(){ return this.url; };
		
			/**
			 * Get the new stream content-type
			 * 
			 * @return the headers
			 */
			
			StreamSpyEvent.prototype.getCType = function(){ return this.ctype; };
		
			/**
			 * Get the new stream data
			 * 
			 * @return the new stream data
			 */
			
			StreamSpyEvent.prototype.getData = function(){ return this.data; };
			
				
		StreamSpyEvent.initialized = true;
	}
	
}
