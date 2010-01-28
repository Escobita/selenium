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

package com.serli.helium.core.replayhandler.extension;

import java.util.HashMap;

import com.serli.helium.core.replayhandler.extension.exception.TesterException;


/**
 * The IStreamTester class. When a plug-in wants to contribute with eclipse extension point
 * to stream tester (only replay) he have to implements this interface and to return true
 * when the test is successfully replayed.
 * 
 * @author Kevin Pollet
 */

public interface IStreamTester {

	/**
	 * 
	 * The test method
	 * 
	 * @param stream the stream byte array
	 * @param param the HashMap of URL parameter
	 * @return true if success false otherwise
	 * @throws TesterException if an exception occur during this replay (ex: bad url parameter)
	 */
	
	public boolean test( byte[] stream, HashMap< String, String > param ) throws TesterException;
	
}
