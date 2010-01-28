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

package com.serli.helium.core.replayhandler.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.serli.helium.commons.logging.service.ILogger;
import com.serli.helium.core.replayhandler.constants.IReplayHandlerConstants;
import com.serli.helium.core.replayhandler.extension.IStreamTester;
import com.serli.helium.core.replayhandler.extension.exception.TesterException;
import com.serli.helium.core.replayhandler.internal.service.Logger;

/**
 * 
 * The replay servlet who receives replay request (ex: replay a test on a PDF or on a picture)
 * This servlet called the plug-in who can replay a test on a certain type in the URL test parameter.
 * 
 * @author Kevin Pollet
 */


public class ReplayServlet extends HttpServlet {

/*---------------------------------------------------------------------------------------------------------------------*/	
	
	private static final long serialVersionUID = 1L;
	
	private HashMap< String, IStreamTester > tester_list;
	
/*---------------------------------------------------------------------------------------------------------------------*/	

	public ReplayServlet(){
		
		this.tester_list = new HashMap< String, IStreamTester >();
	}
	
/*---------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Handle post request
	 * 
	 * @param res the incoming request
	 * @param resp the servlet response
	 */
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {

		String test = req.getParameter("test");
		int content = req.getContentLength();
		
		if ( test == null ) resp.sendError( HttpServletResponse.SC_BAD_REQUEST );
		else if( content == -1 ) resp.sendError( HttpServletResponse.SC_LENGTH_REQUIRED );
		else{
					
			IStreamTester tester = this.tester_list.get( test );
			
			if( tester == null ){ //lazy loading (check extension)
				
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint point = registry.getExtensionPoint( IReplayHandlerConstants.PLUGIN_ID , IReplayHandlerConstants.EXTENSION_POINT_ID );
				IExtension[] extensions = point.getExtensions();
			
				for( int i = 0 ; i < extensions.length ; i++ ){
				
					IConfigurationElement[] elts = extensions[i].getConfigurationElements();
					
					for( int j = 0 ; j < elts.length ; j++){
						
						if( elts[j].getName().equals("streamTester") && elts[j].getAttribute("type").equals(test) ){
						
							try {
								
								tester = (IStreamTester) elts[j].createExecutableExtension( "class" );
								this.tester_list.put( test, tester );
								
							} catch (CoreException e) {  
								
								ILogger logger = Logger.getInstance().getService();
								if( logger != null ) logger.logError( IReplayHandlerConstants.PLUGIN_ID , e.getMessage() , e );
							}
														
						}
						
					}
						
				}
				
			}
			
			
			if( tester != null ){
								
					ByteArrayOutputStream buffer = new  ByteArrayOutputStream();
				
					int i = 0;
					int read = -1;
					
					while( i < content && (read=req.getInputStream().read()) != -1  ){
						
						buffer.write( read );
						i++;						
					}

					if( i != content ) resp.sendError( HttpServletResponse.SC_LENGTH_REQUIRED, "Invalid length" );
					else{
					
						try{
							
							HashMap < String, String > param = new HashMap < String, String >();
							Enumeration<?> e = req.getParameterNames();
					
							while( e.hasMoreElements() ){
						
								String p = (String)e.nextElement();
								String value = req.getParameter( p );
								param.put( p , value );
							}
	
							//call tester
							boolean res = tester.test( buffer.toByteArray() , param );
									
							resp.setStatus(200);
							resp.setContentLength( Boolean.toString(res).length() );
							resp.getWriter().write( Boolean.toString(res).toUpperCase() );
										
						} catch (TesterException e) { 
					
							ILogger logger = Logger.getInstance().getService();
							if( logger != null ) logger.logError( IReplayHandlerConstants.PLUGIN_ID , e.getMessage() , e );
					
							throw new ServletException(e.getMessage(),e);
						}
					
					}
					
			
			}else resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
			
			
		}	
		
		
}
	
/*---------------------------------------------------------------------------------------------------------------------*/	
	
		
}
