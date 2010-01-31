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
 * This class provides useful methods for Network communication
 * 
 * @author Kevin Pollet
 */
 
 
function NetworkUtil(){}

//Create ASynchronous AJAX request
NetworkUtil.sendAsyncData = function( method, url, headers, callback, data){
	
	var http_request = new XMLHttpRequest();
	http_request.open( method, url, true );

	if( headers != null ){
		for( var name in headers ){
			http_request.setRequestHeader( name , headers[name] );		
		}
	}	
	
	if( callback != null )
		http_request.onreadystatechange = function(){ callback( this );  };
	
	if( method.toLowerCase() == "get" ) http_request.sendAsBinary( null );
	else http_request.sendAsBinary( data );
	
};



//Create a Synchronous AJAX request
NetworkUtil.sendSyncData = function( method, url, headers, data ){

	var http_request = new XMLHttpRequest();
	http_request.open( method, url, false );
			
	if( headers != null ){
	
		for( var name in headers )
			http_request.setRequestHeader( name, headers[name] );
	}	
		
	if( method.toLowerCase() == "get" ) http_request.sendAsBinary( null );
	else http_request.sendAsBinary( data );	
		
	return http_request;	
};

