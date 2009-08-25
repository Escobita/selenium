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
package com.serli.helium.core.modelfactory.extension;

/**
 * Extensions who implements this interface
 * are called when a model is created by the 
 * model factory.
 * 
 * @author Kevin Pollet
 */

public interface IModelListener {

	/**
	 * This method is called when the servlet creates a model
	 * 
	 * @param model_id the id of this model
	 * @param model the model
	 */
	
	public void modelCreated( String model_id, Object model );

	/**
	 * This method is called when the servlet receives a close request.
	 * ex: When the user loads a new page in Firefox.
	 */
	
	public void modelClose();
	
}
