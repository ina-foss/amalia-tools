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

import java.io.File;

import fr.ina.research.amalia.prez.Prez;
import fr.ina.research.amalia.prez.PrezException;
import fr.ina.research.amalia.prez.PrezGenerator;
import fr.ina.research.amalia.prez.PrezImageElement;
import fr.ina.research.amalia.prez.PrezVideoElement;

/**
*
* @author Nicolas HERVE - nherve@ina.fr
*/
public class AmaliaACMMM2015Prez {
	public final static File temp = new File("/tmp/AmaliaACM2015Prez/temp");
	public final static File dest = new File("/home/nherve/Travail/Documents/InaLab/player/acmmm2015/prez");
	public final static File res = new File("/home/nherve/Travail/Data/amalia");
	public final static String avconv = "avconv";

	public static void main(String[] args) {
		Prez acm2015 = new Prez("amalia_acm2015");
		acm2015.add(new PrezImageElement("ppt/Diapositive1.PNG", 5).setText("Title"));
		acm2015.add(new PrezImageElement("ppt/Diapositive2.PNG", 20).setText("Amalia.js"));
		acm2015.add(new PrezImageElement("ppt/Diapositive3.PNG", 20).setText("Motivation"));
		acm2015.add(new PrezImageElement("ppt/Diapositive4.PNG", 20).setText("How"));
		acm2015.add(new PrezImageElement("ppt/Diapositive5.PNG", 5).setText("Demo"));
//		acm2015.add(new PrezVideoElement("grab_diginpix.avi", "00:00:12", "00:00:15").setText("DigInPix preview"));
		acm2015.add(new PrezVideoElement("grab_diginpix.avi", "00:00:12", "00:00:02").setText("DigInPix preview"));
		acm2015.add(new PrezImageElement("ppt/Diapositive6.PNG", 5).setText("Questions"));

		PrezGenerator generator = new PrezGenerator().setAvconvCommand(avconv).setTemporaryDir(temp).setDestinationDir(dest).setResourceDir(res);
		try {
			generator.generate(acm2015);
		} catch (PrezException e) {
			e.printStackTrace();
		}
	}
}
