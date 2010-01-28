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

package com.serli.helium.test.logging;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.serli.helium.commons.logging.service.ILogger;
import com.serli.helium.test.TestPlugin;

/**
 * 
 * Test the plugin logging system
 * 
 * @author Kevin Pollet
 */

public class LogServiceTest {
		
	@Test
	public void testLogService(){
	
		ServiceReference ref = TestPlugin.getDefault().getContext().getServiceReference( ILogger.class.getName() );
		Assert.assertNotNull(ref);		
	}
		
	@Test
	public void testLogServiceFilter() throws InvalidSyntaxException{
		
		ServiceReference[] ref = TestPlugin.getDefault().getContext().getServiceReferences( ILogger.class.getName(), "(service=logging)");
		Assert.assertTrue( ref.length > 0 );	
	}
	
}
