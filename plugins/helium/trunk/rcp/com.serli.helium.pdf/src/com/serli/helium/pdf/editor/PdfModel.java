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

package com.serli.helium.pdf.editor;

import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.core.modelfactory.internal.xmltest.Param;
import com.serli.helium.core.modelfactory.internal.xmltest.Test;
import com.serli.helium.core.modelfactory.internal.xmltest.UniqueException;
import com.serli.helium.pdf.PdfActivator;
import com.serli.helium.pdf.constants.IPdfConstants;

/**
 * The pdf test creator model.
 * 
 * @author Kevin Pollet
 */

public class PdfModel extends AbstractTestModel {

/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	public final static String TEXT_PROPERTY = "text"; 
	
	private int num_page;
	private Rectangle[] selection;
	private String extracted_text;
	
	private PropertyChangeSupport support;
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The pdf model default constructor
	 */
	
	public PdfModel() {
		
		this.num_page = -1;
		this.selection = null;
		this.extracted_text = null;
		this.support = new PropertyChangeSupport(this);
	}

/*----------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener( listener );
		
		this.support.addPropertyChangeListener(listener);
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
		
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		
		this.support.removePropertyChangeListener(listener);
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Add a test text on the pdf stream
	 * text content
	 * 
	 * @param txt the text to recognize
	 */

	public void addTestText( String txt ){
		
		try {
			
			Test test = new Test("text");
			
			Param url = new Param("url", this.getRequest().getParameter("url"), false );
			Param text = new Param("text", txt, false );
		
			test.addParameter(url);
			test.addParameter(text);
			
			this.getTestSequence().addTest(test);
			
		} catch (UniqueException e){  
			
			ILog log = PdfActivator.getDefault().getLog();
			log.log( new Status( IStatus.ERROR, IPdfConstants.PLUGIN_ID, e.getMessage(), e) );
		}		
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Add a test pdf text on the pdf
	 */

	public void addTestPdfText(){
		
		try {
			
			Test test = new Test("pdf");
			
			Param url = new Param("url", this.getRequest().getParameter("url"), false );
			Param page = new Param("page", String.valueOf(num_page), false );
			
				//CREATE SELECTION
				String select = "";
				
				for( int i=0 ; i < selection.length ; i++){
				   select += selection[i].x+":"+selection[i].y+":"+selection[i].width+":"+selection[i].height;
					
				   if ( i != (selection.length-1) )
					select += ";";	
				}
						
			Param selection = new Param("selection", select, false );
			Param text = new Param("text", this.extracted_text, false );
		
			test.addParameter(url);
			test.addParameter(page);
			test.addParameter(selection);
			test.addParameter(text);
			
			this.getTestSequence().addTest(test);
			
		} catch (UniqueException e){  
			
			ILog log = PdfActivator.getDefault().getLog();
			log.log( new Status( IStatus.ERROR, IPdfConstants.PLUGIN_ID, e.getMessage(), e) );
		}		
		
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/

	/**
	 * Set the last extraction
	 * 
	 * @param num_page the pdf page
	 * @param txt the text to verify
	 * @param selection the text selection
	 */
	
	public void setExtraction( int num_page, String txt, Rectangle[] selection ){
	
		String old_extract = this.extracted_text;
		
		this.num_page = num_page;
		this.selection = selection;
		this.extracted_text = txt;
		
		this.support.firePropertyChange( PdfModel.TEXT_PROPERTY, old_extract, this.extracted_text );
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/

	/**
	 * Remove the last extraction from the model
	 */
	
	public void clearExtraction(){
		
		this.num_page = -1;
		this.selection = null;
		this.extracted_text = null;
	}

/*----------------------------------------------------------------------------------------------------------------------------*/
		
	/**
	 * Get the page number
	 * 
	 * @return the page number
	 */
	
	public int getNumPage() {
		
		return num_page;
	}

/*----------------------------------------------------------------------------------------------------------------------------*/
		
	/**
	 * Get the last selection
	 * 
	 * @return the selection
	 */
	
	public Rectangle[] getSelection() {
		
		return selection;
	}

/*----------------------------------------------------------------------------------------------------------------------------*/
		
	/**
	 * Get the last extracted text
	 * 
	 * @return the last extracted text
	 */
	
	public String getExtractedText() {
		
		return extracted_text;
	}
	
/*----------------------------------------------------------------------------------------------------------------------------*/
	
	
}
