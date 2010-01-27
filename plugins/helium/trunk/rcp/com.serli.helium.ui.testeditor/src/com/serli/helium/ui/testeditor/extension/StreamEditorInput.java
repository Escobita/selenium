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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;

/**
 * The Stream editor input who wrap
 * the model created by the core model factory
 *
 * @author Kevin Pollet
 */

public class StreamEditorInput implements IEditorInput {

/*--------------------------------------------------------------------------------------------------------------------------------*/	

	private String stream_name;
	private AbstractTestModel model;
	
/*--------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Stream Editor input constructor
	 * 
	 * @param model the editor model
	 */
	
	public StreamEditorInput( AbstractTestModel model ){
			
		this.model = model;
		this.stream_name =  model.getRequest().getParameter("url");
	}	
	
/*--------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public boolean exists() {
		
		return false;
	}

/*--------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public boolean equals(Object obj) {
		
		if( super.equals(obj) ) return true;
		if( !(obj instanceof StreamEditorInput) ) return false;
				
		return false;
	}
	
/*--------------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public ImageDescriptor getImageDescriptor() {
		
		return null;
	}
	
/*--------------------------------------------------------------------------------------------------------------------------------*/	

	public AbstractTestModel getModel(){
		
		return this.model;
	}	

/*--------------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public String getName() {
		
		return this.stream_name;
	}

/*--------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public IPersistableElement getPersistable() {
		
		return null;
	}

/*--------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public String getToolTipText() {
		
		return this.stream_name;
	}

/*--------------------------------------------------------------------------------------------------------------------------------*/	
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		
		return null;
	}

/*--------------------------------------------------------------------------------------------------------------------------------*/	
	

}
