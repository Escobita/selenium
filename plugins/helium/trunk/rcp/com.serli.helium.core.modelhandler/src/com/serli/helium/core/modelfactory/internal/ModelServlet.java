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
package com.serli.helium.core.modelfactory.internal;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;

/**
 * This servlet receives request to create test (When the test is created (by the user)
 * she responds to the client) and to close all model when the user loads a new page
 * in the Firefox browser.
 * 
 * @author Kevin Pollet
 */

public class ModelServlet extends HttpServlet {

/*---------------------------------------------------------------------------------------------------------------------*/	
	
	private static final long serialVersionUID = 1L;

/*---------------------------------------------------------------------------------------------------------------------*/
	
	/**
	 * The default servlet constructor 
	 */
	
	public ModelServlet() {}

/*---------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Handle Http Get request
	 * 
	 * @param req the incoming Http request
	 * @param resp the servlet Http response
	 */
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String command = req.getParameter("command");
	
		if( command == null ) resp.sendError( HttpServletResponse.SC_BAD_REQUEST );
		else if ( command.equals("close") ){
	
			ModelListenerManager.getInstance().fireClose();
			
			resp.setStatus( HttpServletResponse.SC_OK );
			resp.setContentLength(0);
			resp.getWriter().flush();
		}	
		
	}
	
/*---------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Handle Http Post request
	 * 
	 * @param req the incoming Http request
	 * @param resp the servlet Http response
	 */
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		if ( req.getContentLength() == -1 ) resp.sendError( HttpServletResponse.SC_LENGTH_REQUIRED );
		else if ( req.getParameter("url") == null || req.getHeader("content-type") == null ) resp.sendError( HttpServletResponse.SC_BAD_REQUEST,"Parameter url and Content-type is necessary" );
		else{
			
			
			Bal bal = new Bal(); //create a BAL for this request
			
			AbstractTestModel model = ModelFactory.getInstance().createModel(req, bal); //create a model corresponding
																						//to the post request content-type
			
			if( model != null ){	
				
				String message = bal.getMessage(); //wait the model response

				Charset charset = Charset.forName("UTF-8"); //send an UTF-8 encoded response
		
				resp.setContentType("text/xml; charset=UTF-8");
				resp.setContentLength( message.getBytes( charset ).length );
				resp.getOutputStream().write( message.getBytes( charset ) );
			
			}else resp.sendError( HttpServletResponse.SC_NOT_IMPLEMENTED );						
		
		}
		
	}

/*---------------------------------------------------------------------------------------------------------------------*/		
	
}
