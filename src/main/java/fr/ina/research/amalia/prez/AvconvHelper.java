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

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ina.research.amalia.AmaliaException;
import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
*
* @author Nicolas HERVE - nherve@ina.fr
*/
public class AvconvHelper {

	public static String[] generateFinalVideoCommand(PrezGenerator generator, File out) {
		String[] command = { generator.getAvconvCommand(), "-framerate", "" + generator.getFps(), "-start_number", "1", "-f", "image2", "-i", generator.getTemporaryDir() + "/f_%08d.png", "-c:v", "h264", "-crf", "1", out.getAbsolutePath() };
		return command;
	}

	public static Dimension getDimensions(String avconvOutput) throws PrezException {
		try {
			BufferedReader r = new BufferedReader(new StringReader(avconvOutput));
			Pattern p = Pattern.compile("(.*)Stream(.*)Video:(.*)\\s+(\\d+)x(\\d+)\\s+\\[PAR(.*)");

			String line = null;
			while ((line = r.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String w = m.group(4);
					String h = m.group(5);
					return new Dimension(Integer.parseInt(w), Integer.parseInt(h));
				}
			}
			throw new PrezException("Unable to find video dimensions");
		} catch (IOException e) {
			throw new PrezException(e);
		}
	}

	public static RexTimeCode getDuration(String avconvOutput) throws PrezException {
		try {
			BufferedReader r = new BufferedReader(new StringReader(avconvOutput));
			Pattern p = Pattern.compile("(.*)Duration:\\s+([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{2}),\\s+start(.*)");

			String line = null;
			while ((line = r.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					try {
						return RexTimeCode.build(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)), 100 * Integer.parseInt(m.group(5)));
					} catch (NumberFormatException e) {
						throw new PrezException(e);
					} catch (AmaliaException e) {
						throw new PrezException(e);
					}
				}
			}
			throw new PrezException("Unable to find video duration");
		} catch (IOException e) {
			throw new PrezException(e);
		}
	}

	public static String[] getVideoExtractFramesCommand(PrezGenerator generator, PrezVideoElement video) {
		if ((video.getStart() != null) && (video.getLength() != null)) {
			String[] command = { generator.getAvconvCommand(), "-i", video.getResourceFile().getAbsolutePath(), "-r", "" + generator.getFps(), "-ss", video.getStart(), "-t", video.getLength(), "-start_number", "" + generator.getCurrentFrame(), "-f", "image2", generator.getTemporaryDir() + "/f_%08d.png" };
			return command;
		} else {
			String[] command = { generator.getAvconvCommand(), "-i", video.getResourceFile().getAbsolutePath(), "-r", "" + generator.getFps(), "-start_number", "" + generator.getCurrentFrame(), "-f", "image2", generator.getTemporaryDir() + "/f_%08d.png" };
			return command;
		}
	}

	public static String[] getVideoInfoCommand(PrezGenerator generator, PrezVideoElement video) {
		String[] command = { generator.getAvconvCommand(), "-i", video.getResourceFile().getAbsolutePath() };
		return command;
	}

}
