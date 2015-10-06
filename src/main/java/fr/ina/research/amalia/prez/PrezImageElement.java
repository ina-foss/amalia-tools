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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

/**
*
* @author Nicolas HERVE - nherve@ina.fr
*/
public class PrezImageElement extends PrezElement {
	public PrezImageElement(String resourceName, double duration) {
		super(resourceName);

		setDuration(duration);
	}

	@Override
	public String getTimelineBlockId() throws PrezException {
		return "Slide";
	}

	@Override
	public void init(PrezGenerator generator) throws PrezException {
		try {
			BufferedImage img = ImageIO.read(getResourceFile());
			setWidth(img.getWidth());
			setHeight(img.getHeight());
		} catch (IOException e) {
			throw new PrezException(e);
		}
	}

	@Override
	public void process(PrezGenerator generator) throws PrezException {
		try {
			for (int f = 0; f < generator.getNbFrame(getDuration()); f++) {
				File frameFile = generator.getCurrentFrameFile();
				Files.copy(getResourceFile().toPath(), frameFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				generator.moveToNextFrame();
			}
		} catch (IOException e) {
			throw new PrezException(e);
		}
	}

	@Override
	public void processThumbnail(PrezGenerator generator) throws PrezException {
		generateThumbnail(getResourceFile(), getTcIn(), generator);
	}

	@Override
	public String toString() {
		return "Image " + super.toString();
	}
}
