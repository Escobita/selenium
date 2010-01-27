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

package com.serli.helium.pdf.tester;

import java.awt.Rectangle;
import java.util.HashMap;

import com.serli.helium.core.replayhandler.extension.IStreamTester;
import com.serli.helium.core.replayhandler.extension.exception.TesterException;
import com.serli.helium.moteur.TestFactory;
import com.serli.helium.moteur.pdf.ITestPDFText;
import com.serli.helium.moteur.pdf.PDFTextTestFactory;
import com.serli.helium.moteur.pdf.exception.TestPDFException;

/**
 * The PdfTester class who implements the IStreamTester class.
 * The class is called when the replay servlet receive a request to replay
 * a test on a pdf stream.
 * 
 * @author Kevin Pollet
 */

public class PdfTester implements IStreamTester {

	@Override
	public boolean test(byte[] stream, HashMap<String, String> param) throws TesterException {
	
		String text = param.get("text");
		String selection = param.get("selection");
		String page = param.get("page");
		boolean res = false;
		
		if( text != null && selection != null && selection.matches("(((\\d)+:){3}(\\d)+;)*((\\d)+:){3}(\\d)+") && page != null && page.matches("(\\d)*") ){
		
			
			try {
			
				//Page
				int num_page = Integer.valueOf(page);
						
				//Selection
				String[] rect = selection.split(";");
				Rectangle[] list = new Rectangle[ rect.length ];
				
					for( int i=0 ; i< rect.length ; i++){
				
						String[] coords = rect[i].split(":");
						int[] coord = new int[4]; 
				
						for( int j = 0 ; j < coord.length ; j++ )
							coord[j] = Integer.parseInt( coords[j] );
		
						list[i] = new Rectangle( coord[0], coord[1], coord[2], coord[3] );
					}
				
				PDFTextTestFactory factory = TestFactory.getInstance().createPDFTextTestFacory();
				ITestPDFText analyser = factory.createJPedalPDFtextAnalyser();
						
				res = analyser.test( stream, num_page, list , text );
			
			}catch( TestPDFException e ){ throw new TesterException( e.getMessage() ); }
						
		
		}else throw new TesterException( "Bad pdf test param" );
		
		
		return res;
	}
	

}
