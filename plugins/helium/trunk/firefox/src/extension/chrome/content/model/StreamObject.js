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
 * The stream object class.
 * A stream object represents an HTTP response.
 * 
 * @author Kevin Pollet
 */


function StreamObject( url, ctype, data ){
	
	this.url = url;
	this.data = data;
	this.data_length = data.length;
	this.ctype = ctype;
	
	if( typeof StreamObject.initialized == "undefined" ){
		
			StreamObject.prototype.getUrl = function (){ return this.url; };
				
			StreamObject.prototype.getCType = function (){ return this.ctype; };
		
			StreamObject.prototype.getData = function (){ return this.data; };
		
			StreamObject.prototype.getDataSize = function (){ return this.data_length; };		
		
			StreamObject.prototype.toString =function(){ return this.url; };
			
		StreamObject.initialized = true;
	}	
}
