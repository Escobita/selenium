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

package com.serli.helium.core.modelfactory.internal.xmltest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * This class represents the seqtest XML element. You can see in the resp.dtd more details.
 * This element is used to generate an XML response for the servlet. A seqtest element
 * contains a test list. 
 * 
 * @author Kevin Pollet
 */


public class SeqTest implements PropertyChangeListener{

/*---------------------------------------------------------------------------------------------------------------------------*/	

	public final static String PROPERTY_TEST_ADDED = "TestAdded";
	public final static String PROPERTY_TEST_REMOVED = "TestRemoved";
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	private ArrayList< Test > test_list;
	private PropertyChangeSupport change_support;

/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 *  The test sequence constructor
	 */
	
	public SeqTest(){
		
		this.test_list = new ArrayList< Test >();
		this.change_support = new PropertyChangeSupport( this );
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
	 */
	
	public void addPropertyChangeListener( PropertyChangeListener listener ){
	
		this.change_support.addPropertyChangeListener(listener);
	}	
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
	 */
	
	public void removePropertyChangeListener( PropertyChangeListener listener ){
	
		this.change_support.removePropertyChangeListener(listener);
	}	
	
/*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Add a test to this sequence
	 * 
	 * @param t the test
	 */
	
	public void addTest( Test t ){
		
		this.test_list.add(t);
		t.setParent( this );
		t.addPropertyChangeListener( this );
		
		this.change_support.firePropertyChange( PROPERTY_TEST_ADDED , null, t );				
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Remove a test to this sequence
	 * 
	 * @param t the test
	 * @return true if success false otherwise
	 */
	
	public boolean removeTest( Test t ){
					
		boolean res = this.test_list.remove(t);
		
		if( res ){
			
			t.removePropertyChangeListener( this );
			t.setParent(null);
			
			this.change_support.firePropertyChange( PROPERTY_TEST_REMOVED, t, null);
		}
		
		return res;
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Insert a test a the given position
	 * 
	 * @param t the test
	 * @param index the test index
	 */
	
	public void insertTest( Test t, int index ){
				
		this.test_list.add( index, t );
		t.setParent( this );
		t.addPropertyChangeListener( this );
		
		this.change_support.firePropertyChange( PROPERTY_TEST_ADDED , null, t );
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Remove a test to this sequence
	 * 
	 * @param ind the test index
	 * @return the removed test or null if none
	 */
	
	public Test removeTest( int ind ){
		
		if ( ind > this.test_list.size() || ind < 0 ) return null;
		
		Test res = this.test_list.remove( ind );
		
		if( res != null ){
			
			res.removePropertyChangeListener( this );
			res.setParent(null);
			
			this.change_support.firePropertyChange( PROPERTY_TEST_REMOVED, res, null);						
		}	
		
		return res;
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get a test in this sequence
	 * 
	 * @param ind the test index
	 * @return the test or null if index is wrong
	 */
	
	public Test getTest( int ind ){
		
		if ( ind > this.test_list.size() || ind < 0 ) return null;
		return this.test_list.get(ind);
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Get all test list
	 * 
	 * @return all test list
	 */
	
	public ArrayList< Test > getAllTest(){
		
		return this.test_list;
		
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	
	
	/**
	 * Remove all test from this sequence
	 */
	
	public void clear(){

		while( !this.test_list.isEmpty() ){
			
			Test t = this.test_list.get(0);
			this.removeTest(t);
		}
			
	}	
	
/*---------------------------------------------------------------------------------------------------------------------------*/	
		
	/**
	 * Return the test sequence number of test
	 * 
	 * @return the number of test
	 */
	
	public int getNbTest(){
		
		return this.test_list.size();
	}
	
/*---------------------------------------------------------------------------------------------------------------------------*/	

	/**
	 * Get the XML value of this test sequence
	 * 
	 * @return the XML test sequence
	 */
	
	public String toXml(){
		
		String res = "<seqtest>\n";
		
		for( int i= 0 ; i < this.test_list.size() ; i++ ){
			res += this.test_list.get(i).toXml();
		}
		
		res +="</seqtest>\n";
		
		return res;				
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		this.change_support.firePropertyChange( evt );
	}

/*---------------------------------------------------------------------------------------------------------------------------*/	
	
}
