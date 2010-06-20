/**
 *  Copyright 2009 - SERLI
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * This class provides useful methods
 * for Network communication.
 * 
 * @author Kevin Pollet
 */
function NetworkUtil(){   
}

//Create ASynchronous AJAX request
NetworkUtil.sendAsyncData = function(method, url, headers, callback, data){
	
    var http_request = new XMLHttpRequest();
    http_request.open( method, url, true );

    if (headers != null) {
        for (var name in headers) {
            http_request.setRequestHeader(name, headers[name]);
        }
    }
	
    if (callback != null) {
        http_request.onreadystatechange = function() {
            callback(this);
        };
    }
	
    if (method.toLowerCase() == "get") {
        http_request.sendAsBinary(null);
    } else {
        http_request.sendAsBinary( data );
    }
	
};

//Create a Synchronous AJAX request
NetworkUtil.sendSyncData = function(method, url, headers, data){

    var http_request = new XMLHttpRequest();
    http_request.open( method, url, false );
			
    if (headers != null) {
        for (var name in headers) {
            http_request.setRequestHeader(name, headers[name]);
        }
    }
		
    if (method.toLowerCase() == "get") {
        http_request.sendAsBinary(null);
    } else {
        http_request.sendAsBinary( data );
    }
		
    return http_request;
};
