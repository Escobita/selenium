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

package com.serli.helium.test.replay;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test the replay handler
 * @author Kevin Pollet
 */

public class ReplayHandlerTest {

	
	@Test
	public void testText() throws Exception{
	
		URLConnection connection = new URL("http://localhost:12345/replay/?test=text&text=toto%20tata").openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("content-type","text");
		connection.setRequestProperty("Connection","close");
		
		//Send test
		connection.getOutputStream().write("toto tata\r\ntutu".getBytes() );
				
		//Get response
		InputStream stream2 = connection.getInputStream();
		StringBuffer buffer = new StringBuffer(); 
		
		do{
			buffer.append( (char)stream2.read() );
		}while( stream2.available() > 0 );

		//Test response
		Assert.assertEquals("TRUE", buffer.toString() );
	}
	
	@Test
	public void testPicture() throws Exception{
		
		String txt =  URLEncoder.encode("This is a lot of 12","UTF-8");
		String select = URLEncoder.encode("24:77:263:46","UTF-8");
		
	
		URLConnection connection = new URL("http://localhost:12345/replay/?test=picture&selection="+select+"&lang=fra&text="+txt).openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Content-type","image");
		connection.setRequestProperty("Connection","close");
				
		
		//Send test
		InputStream stream = ReplayHandlerTest.class.getResourceAsStream("resource/text.tif");		
		while( stream.available() > 0 )
			connection.getOutputStream().write( stream.read() );
				
		//Get response
		InputStream stream2 = connection.getInputStream();
		StringBuffer buffer = new StringBuffer(); 
				
		do{
			buffer.append( (char)stream2.read() );
		}while( stream2.available() > 0 );

		//Test response
		Assert.assertEquals("TRUE", buffer.toString() );
	}
	
	@Test
	public void testPdf() throws MalformedURLException, IOException{
	
		String test = "Software bugs have enormous costs: time, money, frustrations, and even lives. How\ndo we alleviate as much of these pains as possible? C\n";
		test = URLEncoder.encode(test,"UTF-8");
		
		String select= URLEncoder.encode("152:349:369:18;152:336:216:18","UTF-8");
			
		URLConnection connection = new URL("http://localhost:12345/replay/?test=pdf&selection="+select+"&page=1&text="+test).openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Connection","close");
		connection.setRequestProperty("Content-type","pdf");		
		
		//Send test
		InputStream stream = ReplayHandlerTest.class.getResourceAsStream("resource/Pdf.pdf");		

		int read = -1;
		
		do{
		
			read = stream.read();			
			connection.getOutputStream().write( read );
			
		} while( read != -1 );
		
		//Get response
		InputStream stream2 = connection.getInputStream();
		StringBuffer buffer = new StringBuffer(); 

		do{
			buffer.append( (char)stream2.read() );
		}while( stream2.available() > 0 );

		//Test response
		Assert.assertEquals("TRUE", buffer.toString() );
	}
	
}
