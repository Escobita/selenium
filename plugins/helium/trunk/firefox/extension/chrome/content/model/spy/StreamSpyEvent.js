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
	
};
