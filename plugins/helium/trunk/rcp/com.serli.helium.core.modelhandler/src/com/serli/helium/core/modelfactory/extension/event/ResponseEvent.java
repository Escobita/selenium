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

package com.serli.helium.core.modelfactory.extension.event;

import java.util.EventObject;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;

/**
 * The ResponseEvent class. This event is fire when the model send a response
 * to the client.
 * 
 * @author Kevin Pollet
 */

public class ResponseEvent extends EventObject {

/*-------------------------------------------------------------------------------------------------------------------------------------------*/	

	private static final long serialVersionUID = 1L;

	private String response;
	
/*-------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Construct a stream event object
	 * 
	 * @param source the model
	 * @param response the response send
	 */
	
	public ResponseEvent( AbstractTestModel source, String response ) {
		super(source);
	
		this.response = response;
	}

/*-------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 *@see EventObject#getSource() 
	 */
	
	@Override
	public AbstractTestModel getSource() {
			
		return (AbstractTestModel)super.getSource();
	}

	
/*-------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the response send to the
	 * client
	 * 
	 * @return the response or null if none
	 */
	
	public String getResponse(){
	
		return this.response;
	}
	
/*-------------------------------------------------------------------------------------------------------------------------------------------*/	

}
