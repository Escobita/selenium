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
 * Function of the HELIUM extension.
 * This permits to Selenium user to add test command
 * and replay it.
 *
 * @author Kevin Pollet
 */

Selenium.prototype.assertTextInStream = function(url, text){
	 
    var res = "FALSE";
    var host = prefs.getChar("servername");
    var port = prefs.getInt("serverport");
	 
    if( url != "" && url.indexOf("url=") != -1 ){
	  
        var s_url = url.split("=")[1];
        var txt = text.replace(/%0A/g,"\n");
	 	 		 
        //GET THE TEXT STREAM
	 
        var streamObj = heliumModel.getStreamObject( s_url );
		 	 
        if( streamObj != null ){
	 			
            try{
	 			
                var request = NetworkUtil.sendSyncData("post","http://"+host+":"+port+"/replay/?test=text&text="+encodeURIComponent( txt ), null, streamObj.getData() );
		 
                if (request.readyState == 4) res = request.responseText;
	 				
            }catch( e ){
                throw new Error("The Rcp application is not launched");
            }
	 			
	 			
        }else if( s_url.indexOf("pdf") != -1 ){
	 		
            //DOWNLOAD PDF
	 			
            var ioserv = Components.classes["@mozilla.org/network/io-service;1"].getService(Components.interfaces.nsIIOService);
            var channel = ioserv.newChannel(s_url, 0, null);
            var stream = channel.open();

            if ( channel instanceof Components.interfaces.nsIHttpChannel && channel.responseStatus == 200 ){
		 	 
                var bstream = Components.classes["@mozilla.org/binaryinputstream;1"].createInstance(Components.interfaces.nsIBinaryInputStream);
                bstream.setInputStream(stream);

                var size = 0;
                var file_data = "";
		 			 
                while(size = bstream.available()) {
		 				 
                    file_data += bstream.readBytes(size);
                }
	 			
                //TEST
		 			 
                try{
			 			
                    var request = NetworkUtil.sendSyncData("post","http://"+host+":"+port+"/replay/?test=text&text="+encodeURIComponent( txt ), null, file_data );
			 
                    if (request.readyState == 4) res = request.responseText;
		 				
                }catch( e ){
                    throw new Error("The Rcp application is not launched");
                }
		 			
            }
        }
	 		
    }
			 
    //VERIFY TEST RESULT
		 
    Assert.matches("TRUE", res );
}
 
Selenium.prototype.assertTextInPDF = function( params, text ){
	 
    var res = "FALSE";
    var host = prefs.getChar("servername");
    var port = prefs.getInt("serverport");
	 
    if( params != "" && params.indexOf("url=") != -1 ){
	  
        var url="";
        var param_list=""
        var txt = text.replace(/%0A/g,"\n");
	
        //CREATE REQUEST URL AND RETRIEVE STREAM URL
	 
        var paramtab = params.split("&");
	 	
        for( var i = 1 ;  i < paramtab.length ; i++ ){
		 
            if( paramtab[i].indexOf("url=") != -1 ) url = paramtab[i].split("=")[1];
            else{
	 			
                var equal = paramtab[i].split("=");
                param_list = param_list +"&"+equal[0]+"="+encodeURIComponent(equal[1]);
            }
        }
	 	
        //DOWNLOAD PDF
	 	 	
        var ioserv = Components.classes["@mozilla.org/network/io-service;1"].getService(Components.interfaces.nsIIOService);
        var channel = ioserv.newChannel(url, 0, null);
        var stream = channel.open();

        if ( channel instanceof Components.interfaces.nsIHttpChannel && channel.responseStatus == 200 ){
	 	 
            var bstream = Components.classes["@mozilla.org/binaryinputstream;1"].createInstance(Components.interfaces.nsIBinaryInputStream);
            bstream.setInputStream(stream);

            var size = 0;
            var file_data = "";
	 			 
            while(size = bstream.available()) {
	 				 
                file_data += bstream.readBytes(size);
            }
	 			 
            if( file_data != "" ){
	 			 
                try{
	 				 
                    var request = NetworkUtil.sendSyncData("post","http://"+host+":"+port+"/replay/?test=pdf"+param_list+"&text="+encodeURIComponent(txt), null, file_data );
					 
                    if (request.readyState == 4)
                        res = request.responseText;
	 				 
                }catch( e ){
                    throw new Error("The Rcp application is not launched");
                }
	 				 
            }
	 			 
        }
	 		 
    }
	 	
    //VERIFY TEST RESULT
	
    Assert.matches("TRUE", res );
}
 
Selenium.prototype.assertTextInPicture = function( params, text ){
	 
    var res = "FALSE";
    var host = prefs.getChar("servername");
    var port = prefs.getInt("serverport");
	 
    if( params != "" ){
	 	 
        var url="";
        var param_list="";
        var txt = text.replace(/%0A/g,"\n");
		
        //Encode parameter
		 
        var paramtab = params.split("&");
		 	
        for( var i = 1 ;  i < paramtab.length ; i++ ){
			 
            if( paramtab[i].indexOf("url=") != -1 ) url = paramtab[i].split("=")[1];
            else{
		 			
                var equal = paramtab[i].split("=");
                param_list = param_list +"&"+equal[0]+"="+encodeURIComponent( equal[1] );
            }
		 		
        }
		 	
        //Create request URL
		 	
        var req_url = "http://"+host+":"+port+"/replay/?test=picture"+param_list+"&text="+encodeURIComponent(txt);
		 
        //Send replay test
		 	 
        if( url == "" ){ //No url parameter test all picture

            var stream_list = heliumModel.getStreamList();
		 			 		
            for( var i = 0 ; res == "FALSE" && i < stream_list.length ; i++){
		 			
                if( stream_list[i].getCType().indexOf("image/") != -1 ){
		 				
                    try{
		 				
                        var request = NetworkUtil.sendSyncData("post",req_url, null, stream_list[i].getData() );
		 					 				
                        if ( request.readyState == 4 && request.responseText == "TRUE" )
                            res = "TRUE";
		 					
                    }catch( e ){
                        throw new Error("The Rcp application is not launched");
                    }
		 				
                }
		 			
            }
		 		
		 		
        }else{
		 		
            var streamObj = heliumModel.getStreamObject( url );
			 	 		
            if( streamObj != null ){
				 
                try{
			 			
                    var request = NetworkUtil.sendSyncData("post",req_url, null, streamObj.getData() );
			 
                    if (request.readyState == 4) res = request.responseText;
		 				
                }catch( e ){
                    throw new Error("The Rcp application is not launched");
                }
		 			
            }
		 		
		 		
        }
		 	
		 	
    }
		 
    //Verify test result
	 	
    Assert.matches("TRUE", res );
	 	
}
 
 