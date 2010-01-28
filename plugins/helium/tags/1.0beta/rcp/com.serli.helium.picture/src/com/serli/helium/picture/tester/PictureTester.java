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

package com.serli.helium.picture.tester;

import java.awt.Rectangle;
import java.util.HashMap;

import com.serli.helium.core.replayhandler.extension.IStreamTester;
import com.serli.helium.core.replayhandler.extension.exception.TesterException;
import com.serli.helium.moteur.TestFactory;
import com.serli.helium.moteur.picture.ITestPictureText;
import com.serli.helium.moteur.picture.PictureTextTestFactory;
import com.serli.helium.moteur.picture.engine.Language;
import com.serli.helium.moteur.picture.exception.TestPictureException;

/**
 * The picture tester class
 * 
 * @author Kevin Pollet
 */

public class PictureTester implements IStreamTester {

/*-----------------------------------------------------------------------------------------------------------*/	

	/**
	 * The picture tester default constructor
	 */
	
	public PictureTester(){}
	
/*-----------------------------------------------------------------------------------------------------------*/
	
	@Override
	public boolean test(byte[] stream, HashMap<String, String> param) throws TesterException {
		
		boolean res = false;
		
		String selection = param.get("selection"); //x:y:width:height;x:y:width:height
		String lang = param.get("lang");
		String text = param.get("text");
				
		if( text != null && selection != null && selection.matches("((\\d)*:){3}(\\d)*") ){
		
		  try {	
			  
				//Selection
				String[] coords = selection.split(":");
				int[] coord = new int[4]; 
								
				for( int i = 0 ; i < coord.length ; i++ )
				coord[i] = Integer.parseInt( coords[i] );
													
				//Language
				Language language = Language.English;
							
				if( lang != null ){
							
					if( lang.equals( Language.Dutch.getIsoCode() ) ) language=Language.Dutch;
					else if( lang.equals( Language.French.getIsoCode() ) ) language=Language.French;
					else if( lang.equals( Language.German.getIsoCode() ) ) language=Language.German;
					else if( lang.equals( Language.Italian.getIsoCode() ) ) language=Language.Italian;
					else if( lang.equals( Language.Portuguese.getIsoCode() ) ) language=Language.Portuguese;
					else if( lang.equals( Language.Spanish.getIsoCode() ) ) language=Language.Spanish;
								
				}				
							
				PictureTextTestFactory factory = TestFactory.getInstance().createPictureTextTestFactory();
				ITestPictureText analyser = factory.createTesseractPictureAnalyzer( language );
												
				res = analyser.test( stream , new Rectangle( coord[0], coord[1], coord[2], coord[3] ) , text );
			
				
		  }catch( TestPictureException e ){ throw new TesterException(e.getMessage()); }
		  
			
		}else throw new TesterException("Bad pdf tester parameter" );
		
				
		return res;
	}
	
/*-----------------------------------------------------------------------------------------------------------*/
	

}
