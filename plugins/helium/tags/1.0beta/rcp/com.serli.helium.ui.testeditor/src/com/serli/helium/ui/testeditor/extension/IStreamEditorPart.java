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

package com.serli.helium.ui.testeditor.extension;

import org.eclipse.ui.IEditorPart;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;

/**
 * The stream editor part interface
 * 
 * @author Kevin Pollet
 */

public interface IStreamEditorPart extends IEditorPart {

	/**
	 * A method to get the stream editor model
	 * 
	 * @return The editor model
	 */
	
	public AbstractTestModel getModel(); 

}
