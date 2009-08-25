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

package com.serli.helium.text.streamtester;

import java.util.HashMap;

import com.serli.helium.core.replayhandler.extension.IStreamTester;
import com.serli.helium.core.replayhandler.extension.exception.TesterException;
import com.serli.helium.moteur.TestFactory;
import com.serli.helium.moteur.stream.ITestStreamText;
import com.serli.helium.moteur.stream.StreamTextTestFactory;
import com.serli.helium.moteur.stream.exception.TestStreamIllegalArgumentException;

/**
 * The text stream tester
 * 
 * @author Kevin Pollet
 */

public class TextTester implements IStreamTester {

/*-------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * The text testre default constructor
	 */
	
	public TextTester(){}
	
/*-------------------------------------------------------------------------------------------------------------------------------*/	
	
	@Override
	public boolean test( byte[] stream, HashMap<String, String> param ) throws TesterException {
		
		boolean res = false;
		
		String text = param.get( "text" );			

		if( text == null ) throw new TesterException( "No text parameter" );
		else{
			
			try {

				StreamTextTestFactory factory = TestFactory.getInstance().createStreamTextTestFactory();
				ITestStreamText analyser = factory.createStreamContainsTextAnalyser();
				
				res = analyser.test( new String( stream ) , text );		
				
			} catch( TestStreamIllegalArgumentException e ){ throw new TesterException( e.getMessage() ); }		
		}
		
		return res;
	}	
	
/*-------------------------------------------------------------------------------------------------------------------------------*/	

}
