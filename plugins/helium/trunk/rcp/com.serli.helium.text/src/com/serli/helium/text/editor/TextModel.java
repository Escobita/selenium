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

package com.serli.helium.text.editor;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.core.modelfactory.internal.xmltest.Param;
import com.serli.helium.core.modelfactory.internal.xmltest.Test;
import com.serli.helium.text.TextActivator;
import com.serli.helium.text.constants.ITextEditorConstants;

public class TextModel extends AbstractTestModel {

/*---------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * the model default constructor 
	 */
	
	public TextModel() {}

/*---------------------------------------------------------------------------------------------------------*/	

	/**
	 * Add a text test verification
	 * 
	 * @param text the text to verify
	 */
	
	public void addTestText( String text ){
				
		try {
			
			 Test t = new Test( "text" );
			 t.addParameter( new Param("url", this.getRequest().getParameter("url"), false ) );
			 t.addParameter( new Param("text", text, false ) );
						 
			 this.getTestSequence().addTest( t );
			 
		} catch (Exception e) { 
			
			ILog log = TextActivator.getDefault().getLog();
			log.log( new Status( IStatus.ERROR, ITextEditorConstants.PLUGIN_ID, e.getMessage(), e) );
		}
		
				  
	}	
	
/*---------------------------------------------------------------------------------------------------------*/
	
}
