/*
 * Copyright (c) 2015 Institut National de l'Audiovisuel, INA
 *
 * This file is free software: you can redistribute it and/or modify   
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or   
 * (at your option) any later version.                                 
 * 
 * Redistributions of source code and compiled versions
 * must retain the above copyright notice, this list of conditions and 
 * the following disclaimer.                                           
 * 
 * Neither the name of the copyright holder nor the names of its       
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.            
 * 
 * You should have received a copy of the GNU Lesser General Public License   
 * along with this file. If not, see <http://www.gnu.org/licenses/>    
 * 
 * This file is distributed in the hope that it will be useful,        
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        
 * GNU Lesser General Public License for more details.
 */
package fr.ina.research.amalia.prez;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
*
* @author Nicolas HERVE - nherve@ina.fr
*/
public class Prez implements Iterable<PrezElement> {
	private String name;
	private List<PrezElement> elements;

	public Prez(String name) {
		super();

		elements = new ArrayList<PrezElement>();
		setName(name);
	}

	public boolean add(PrezElement e) {
		return elements.add(e);
	}

	public String getName() {
		return name;
	}

	@Override
	public Iterator<PrezElement> iterator() {
		return elements.iterator();
	}

	public void setName(String name) {
		this.name = name;
	}

	public int size() {
		return elements.size();
	}

}
