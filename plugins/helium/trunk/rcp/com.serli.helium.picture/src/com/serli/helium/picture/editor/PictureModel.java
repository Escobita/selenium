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

package com.serli.helium.picture.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.media.jai.RenderedImageAdapter;
import javax.swing.ImageIcon;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Rectangle;

import com.serli.helium.core.modelfactory.extension.AbstractTestModel;
import com.serli.helium.core.modelfactory.internal.xmltest.Param;
import com.serli.helium.core.modelfactory.internal.xmltest.Test;
import com.serli.helium.core.modelfactory.internal.xmltest.UniqueException;
import com.serli.helium.moteur.picture.engine.Language;
import com.serli.helium.moteur.picture.engine.TesseractEngine;
import com.serli.helium.picture.PictureActivator;
import com.serli.helium.picture.constants.IPictureConstants;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;



public class PictureModel extends AbstractTestModel {

/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	public final static String TEXT_PROPERTY = "text"; 
	
	private TesseractEngine engine;
		
	private String 	  text;
	private Rectangle selection;
	
	private PropertyChangeSupport change_support;
		
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * The default constructor
	 */
	
	public PictureModel() {
		
		this.text = null;
		this.selection = null;
		this.engine = new TesseractEngine(); 
		
		this.change_support = new PropertyChangeSupport( this );
	}

/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void addPropertyChangeListener( PropertyChangeListener listener ){
		super.addPropertyChangeListener( listener );
		
		this.change_support.addPropertyChangeListener( listener );
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public void removePropertyChangeListener( PropertyChangeListener listener ){
		super.removePropertyChangeListener( listener );
		
		this.change_support.removePropertyChangeListener( listener );
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Change the engine model language
	 * 
	 * @param lang the language
	 */
	
	public void setLanguage( Language lang ){
	
		this.engine.setLanguage( lang );
		
		if( selection != null )
			this.extractText(lang, selection );		
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Extract the selected zone in the picture with the current selected
	 * language
	 * 
	 * @param selection the image selection
	 */
	
	public void extractText( Rectangle selection ){	
		
		this.selection = selection;
				
		BufferedImage picture = this.createFromByte( this.getData() );
				
		try {
			
			String old_text = this.text;
			
			this.text = engine.recognizeText( picture, new java.awt.Rectangle(selection.x, selection.y, selection.width, selection.height ) );
			this.change_support.firePropertyChange( PictureModel.TEXT_PROPERTY , old_text , this.text );
			
		} catch (Exception e) {	
			
			ILog log = PictureActivator.getDefault().getLog();
			log.log( new Status( IStatus.ERROR, IPictureConstants.PLUGIN_ID, e.getMessage(), e) );
		}
		
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Extract the selected zone in the picture
	 * 
	 * @param lang the language used for extraction
	 * @param selection the selection to be extracted
	 */
	
	public void extractText( Language lang, Rectangle selection ){
					
		engine.setLanguage( lang );
		this.extractText(selection);
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	

	
	/**
	 * Add a test text on the picture stream
	 * text content
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
			
			ILog log = PictureActivator.getDefault().getLog();
			log.log( new Status( IStatus.ERROR, IPictureConstants.PLUGIN_ID, e.getMessage(), e) );
		}
		
		
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Add a test text on the picture stream
	 * text content
	 */

	public void addPictureTestText(){
		
		try {
		
			Test test = new Test("picture");
			
			Param url = new Param("url", this.getRequest().getParameter("url"), true );
			Param lang = new Param("lang", this.engine.getLanguage().getIsoCode() , true );
			Param selection = new Param("selection", this.selection.x+":"+this.selection.y+":"+this.selection.width+":"+this.selection.height, false );
			Param text = new Param("text", this.text, false );
			
			test.addParameter(url);
			test.addParameter(lang);
			test.addParameter(selection);
			test.addParameter(text);
		
			this.getTestSequence().addTest(test);
			
		} catch (UniqueException e){

			ILog log = PictureActivator.getDefault().getLog();
			log.log( new Status( IStatus.ERROR, IPictureConstants.PLUGIN_ID, e.getMessage(), e) );
		}
		
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the last extracted text
	 * 
	 * @return the last extracted text, null if none
	 */
	
	public String getLastExtractedText(){
		
		return this.text;
	}
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Remove the last extracted
	 * text
	 */
	
	public void removeLastExtractedText(){
		
		String old_text = this.text;
		
		this.text = null;
		this.selection = null;
		
		this.change_support.firePropertyChange( PictureModel.TEXT_PROPERTY , old_text , this.text );
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * A useful method to create a bufferedImage from byte
	 * 
	 * @param array the image data
	 */
	
	private BufferedImage createFromByte( byte[] array ){ //JPG,PNG,GIF,BMP
		
		BufferedImage res = null;
	
		if ( (array[0] == 0x4D && array[1] == 0x4D) || (array[0] == 0x49 && array[1] == 0x49) ){
			
			ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", new ByteArrayInputStream(array), new TIFFDecodeParam() );
			
			try {
				
				RenderedImage render_ima = decoder.decodeAsRenderedImage();
				
				RenderedImageAdapter adapter = new RenderedImageAdapter( render_ima );
				BufferedImage tmp  = adapter.getAsBufferedImage();
								
				res = new BufferedImage(
		                tmp.getWidth(null),
		                tmp.getHeight(null),
		                BufferedImage.TYPE_INT_RGB );
				
				
				Graphics g = res.createGraphics();
				
				g.setColor( Color.WHITE );
				
				g.fillRect(0, 0, tmp.getWidth(null),tmp.getHeight(null));

		        g.drawImage(tmp, 0, 0, null);
		        
		    	g.dispose();
				
			} catch (IOException e) {
				
				ILog log = PictureActivator.getDefault().getLog();
				log.log( new Status( IStatus.ERROR, IPictureConstants.PLUGIN_ID, e.getMessage(), e) );
			}						
			
		}else{
		
			//Create image
			
			ImageIcon image= new ImageIcon( array );
			res = new BufferedImage( image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_RGB );
		
			//Draw on the image	
			
			Graphics2D g = res.createGraphics();
				g.setColor( Color.WHITE );
				g.fillRect( 0, 0, image.getIconWidth(), image.getIconHeight() );
				g.drawImage( image.getImage(), 0, 0, null );
			
		}			
			
					
		return res;
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
}
