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
	public final static File temp = new File("/tmp/AmaliaACM2015Prez");
	public final static File dest = new File("/home/nherve/Travail/Documents/InaLab/player/acmmm2015/prez");
	public final static File res = new File("/home/nherve/Travail/Data/amalia");
	public final static String avconv = "avconv";

	public static void main(String[] args) {
		Prez acm2015 = new Prez("amalia_acm2015");
		acm2015.add(new PrezImageElement("ppt/Diapositive1.PNG", 20).setText("Title"));
		acm2015.add(new PrezImageElement("ppt/Diapositive2.PNG", 20).setText("Amalia.js"));
		acm2015.add(new PrezImageElement("ppt/Diapositive3.PNG", 20).setText("Motivation"));
		acm2015.add(new PrezImageElement("ppt/Diapositive4.PNG", 20).setText("How"));
		acm2015.add(new PrezImageElement("ppt/Diapositive5.PNG", 5).setText("Demo"));
		acm2015.add(new PrezVideoElement("grab_timeline.avi", "00:00:03", "00:00:39").setText("Amalia - simple timeline"));
		acm2015.add(new PrezVideoElement("grab_zoom.avi", "00:00:14", "00:01:08").setText("Amalia - zoom in timeline"));
		acm2015.add(new PrezImageElement("ppt/Diapositive6.PNG", 5).setText("Research prototypes"));
		acm2015.add(new PrezVideoElement("grab_diginpix.avi", "00:00:12", "00:00:27").setText("Research prototype - DigInPix"));
		acm2015.add(new PrezVideoElement("grab_speechtrax.avi", "00:00:08", "00:00:45").setText("Research prototype - SpeechTrax"));
		acm2015.add(new PrezVideoElement("grab_overlay_joconde.avi", "00:00:10", "00:00:31").setText("Research prototype - Object tracking"));
		
		acm2015.add(new PrezImageElement("ppt/Diapositive7.PNG", 5).setText("Metadata edition"));
		acm2015.add(new PrezVideoElement("grab_edit_overlay.avi", "00:00:28", "00:01:31").setText("Research prototype - Metadata edition"));
		
		acm2015.add(new PrezImageElement("ppt/Diapositive8.PNG", 5).setText("Technical details"));
		acm2015.add(new PrezImageElement("ppt/Diapositive9.PNG", 30).setText("Architecture schema"));
		acm2015.add(new PrezImageElement("ppt/Diapositive10.PNG", 10).setText("External libraries"));
		acm2015.add(new PrezVideoElement("grab_github.avi", "00:00:00", "00:01:00").setText("Amalia - Github projects and model XSD"));
		acm2015.add(new PrezVideoElement("grab_html_integration.avi", "00:00:00", "00:00:42").setText("Amalia - HTML integration and metadata binding"));
		acm2015.add(new PrezImageElement("ppt/Diapositive11.PNG", 5).setText("Documentation & Tools"));
		acm2015.add(new PrezVideoElement("grab_website_amalia.avi", "00:00:02", "00:00:22").setText("amalia.js web site"));
		
		acm2015.add(new PrezImageElement("ppt/Diapositive12.PNG", 5).setText("Professional applications"));
		acm2015.add(new PrezVideoElement("grab_poc_sidoc.avi", "00:00:28", "00:01:31").setText("Professional application - segment creation"));
		
		acm2015.add(new PrezImageElement("ppt/Diapositive13.PNG", 20).setText("Future work"));
		acm2015.add(new PrezImageElement("ppt/Diapositive14.PNG", 5).setText("Questions"));

		PrezGenerator generator = new PrezGenerator().setAvconvCommand(avconv).setTemporaryDir(temp).setDestinationDir(dest).setResourceDir(res);
		try {
//			generator.logInfo("Full presentation duration : " + generator.getPotentialDuration(acm2015));
			generator.generate(acm2015);
		} catch (PrezException e) {
			e.printStackTrace();
		}
	}
}
