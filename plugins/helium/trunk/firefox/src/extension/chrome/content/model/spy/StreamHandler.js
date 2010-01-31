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
 * The Stream handler class. A stream handler is responsible to
 * capture the response data
 * 
 * @author Kevin Pollet
 */
 
 
function StreamHandler( stream_spy ){

	this.spy = stream_spy;	
};

StreamHandler.prototype = {

	old_listener : null,	
	data : "",
	url : "",
	ctype : "",
	
	onStartRequest : function (aRequest, aContext) { 

	
			this.old_listener.onStartRequest( aRequest, aContext );
			
			var httpChannel = aRequest.QueryInterface( Components.interfaces.nsIHttpChannel );
			
			this.url = httpChannel.URI.asciiSpec;
			this.ctype =  httpChannel.getResponseHeader("Content-Type");	
	},

	onDataAvailable : function ( aRequest, aContext, aStream, aSourceOffset, aLength) { 
				
			//Create a storage stream
			//for send stream to the browser
			var storage_stream = Components.classes["@mozilla.org/storagestream;1"].createInstance();
			var istorage_stream = storage_stream.QueryInterface(Components.interfaces.nsIStorageStream);

			istorage_stream.init(8192, aLength, null);
			var storage_output_stream = istorage_stream.getOutputStream(0);

			//Create a binary stream
			var binary_stream = Components.classes["@mozilla.org/binaryinputstream;1"].createInstance();		
			var binary_input_stream = binary_stream.QueryInterface(Components.interfaces.nsIBinaryInputStream);
			binary_input_stream.setInputStream( aStream )
			
			// ReadTheSteam
			var temp = binary_input_stream.readBytes( aLength ); 
			this.data = this.data + temp;//add the stream to the data object

			storage_output_stream.write( temp, aLength );

			this.old_listener.onDataAvailable( aRequest, aContext, istorage_stream.newInputStream(0), aSourceOffset, aLength);		
		},

	onStopRequest : function (aRequest, aContext, aStatus) { 
		
			this.old_listener.onStopRequest( aRequest, aContext, aStatus );
			this.spy.dataCapturated( this.url, this.ctype, this.data );
	},	
					
	setOldListener : function ( listener ){  
						
		this.old_listener = listener;  
	}	
		
};
