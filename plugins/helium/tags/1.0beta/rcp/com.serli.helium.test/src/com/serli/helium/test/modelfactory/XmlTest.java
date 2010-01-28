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

package com.serli.helium.test.modelfactory;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;

import com.serli.helium.core.modelfactory.internal.xmltest.Param;
import com.serli.helium.core.modelfactory.internal.xmltest.SeqTest;
import com.serli.helium.core.modelfactory.internal.xmltest.Test;
import com.serli.helium.core.modelfactory.internal.xmltest.UniqueException;


public class XmlTest {

	private SeqTest seq;
	
	@Before
	public void setUp(){
		this.seq = new SeqTest();
	}
	
	@org.junit.Test
	public void SeqTestAdd(){
		
		Test t = new Test( "pdf" );
		Test t2 = new Test( "pdf" );
		
		this.seq.addTest(t);
		this.seq.addTest(t2);
		
		Assert.assertEquals( 2  , this.seq.getNbTest() );
		Assert.assertEquals( t  , this.seq.getTest(0) );
		Assert.assertEquals( t2 , this.seq.getTest(1) );
	}
	
	@org.junit.Test
	public void SeqTestInsert(){
		
		Test t = new Test( "pdf" );
		Test t2 = new Test( "pdf" );
		
		this.seq.addTest(t);
		this.seq.insertTest(t2,0);
		
		Assert.assertEquals( 2  , this.seq.getNbTest() );
		Assert.assertEquals( t  , this.seq.getTest(1) );
		Assert.assertEquals( t2 , this.seq.getTest(0) );
	}

	@org.junit.Test
	public void seqTestRemove(){
		
		Test t = new Test( "pdf");
		Test t2 = new Test( "pdf");
		
		this.seq.addTest(t);
		this.seq.addTest(t2);
		
		Assert.assertEquals( 2 , this.seq.getNbTest() );
		
		this.seq.removeTest(t);
		
		Assert.assertEquals( t2 , this.seq.getTest(0) );
	}
	
	
	@org.junit.Test
	public void addParam() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test( "pdf");
		t.addParameter( new Param( "param" , "",false ) );
		
		Assert.assertEquals( 1 , t.getNbParameter() );
		Assert.assertNotNull( t.getParam("param") );
		Assert.assertEquals( t,  t.getParam("param").getParent() );
	}
	
	@org.junit.Test
	public void insertParam() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test( "pdf");
		t.addParameter( new Param( "param" , "",false ) );
		t.insertParameter( new Param( "param2" , "",false ), 0);
				
		Assert.assertEquals( 2 , t.getNbParameter() );
		Assert.assertEquals( "param2", t.getAllParameter().get(0).getName() );
		Assert.assertEquals( t,  t.getParam("param").getParent() );
	}	
	
	@org.junit.Test
	public void removeParam() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test( "pdf");
		Param p1 = new Param( "param" , "",false );
		Param p2 = new Param( "param2" , "value",false );
		
		t.addParameter(p1);
		t.addParameter(p2);
		
		Assert.assertEquals( 2, t.getNbParameter() );
		
		t.removeTestParameter( p1.getName() );
		Assert.assertNull( t.getParam("param") );
		
		t.removeTestParameter(p2);
		Assert.assertNull( t.getParam("param2") );
		
		Assert.assertEquals( 0, t.getNbParameter() );
	}
	
	
	@org.junit.Test
	public void addParamValueName() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test( "pdf");
		t.addParameter( new Param("param" , "value",false ) );
		
		Assert.assertEquals( "value",  t.getParam("param").getValue() );
		Assert.assertEquals( "param",  t.getParam("param").getName() );
				
	}
	
	
	@org.junit.Test( expected=UniqueException.class )
	public void addParamUniqueExcept() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test("pdf");
		t.addParameter( new Param("param" , "",false ) );
		t.addParameter( new Param("param" , "",false ) );
	}

	public void addParamsetName() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test("pdf");
		Param p = new Param( "param" , "value",false );
		
		t.addParameter(p);
		
		p.setName("param");				
	}	
	
	@org.junit.Test( expected=IllegalArgumentException.class )
	public void addParamsetNameExcept() throws IllegalArgumentException, UniqueException{
		
		Test t = new  Test("pdf");
		Param p = new Param( "param" , "value",false );
		
		t.addParameter(p);
		
		p.setName("");				
	}
	
	@org.junit.Test( expected=UniqueException.class )
	public void addParamsetNameExcept2() throws IllegalArgumentException, UniqueException{
		
		Test t   = new  Test("pdf");
		Param p  =  new Param("param" , "value",false );
		Param p2  = new Param("param2" , "value",false );
		
		t.addParameter(p);
		t.addParameter(p2);
		
		p.setName("param2");				
	}	
	
	@After
	public void tearDown(){
		
		this.seq = null;
	}	
	
}
