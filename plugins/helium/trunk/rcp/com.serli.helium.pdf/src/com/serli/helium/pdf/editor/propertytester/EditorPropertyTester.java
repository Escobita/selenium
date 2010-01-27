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

package com.serli.helium.pdf.editor.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import com.serli.helium.pdf.editor.PdfStreamEditorPart;

/**
 * The editor property testor
 * 
 * @author Kevin Pollet
 */

public class EditorPropertyTester extends PropertyTester {

/*-----------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The EditorPropertyTester default constructor
	 */
	
	public EditorPropertyTester() {}

/*-----------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		boolean res = false;
		
		
		if( property.equals("ActivePage") ){
			
			PdfStreamEditorPart editor = (PdfStreamEditorPart)receiver; 
			
			res = editor.getActivePage() == (Integer)expectedValue;			
		}	
		
		
		return res;
	}

/*-----------------------------------------------------------------------------------------------------------------------------------*/	
	
}
