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

package com.serli.helium.picture.editor.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import com.serli.helium.picture.editor.PictureModel;
import com.serli.helium.picture.editor.PictureStreamEditorPart;

/**
 * 
 * The extract property tester
 * 
 * @author Kevin Pollet
 */

public class ExtractPropertyTester extends PropertyTester {

/*-----------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The ExtractPropertyTester default constructor
	 */
	
	public ExtractPropertyTester() {}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public boolean test(Object receiver, String property, Object[] args,Object expectedValue) {
		
		boolean res = false;
				
		if( property.equals("Is_Empty") ){
		
			PictureStreamEditorPart part = (PictureStreamEditorPart)receiver;
			PictureModel model = (PictureModel)part.getModel();
			
			
			res = model.getLastExtractedText() == null || model.getLastExtractedText().isEmpty();
			
		}		
		
		return res;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
	
}
