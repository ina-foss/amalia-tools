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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.imageio.ImageIO;

import fr.ina.research.amalia.AmaliaException;
import fr.ina.research.amalia.model.MetadataFactory;
import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
*
* @author Nicolas HERVE - nherve@ina.fr
*/
public abstract class PrezElement {
	private final static RenderingHints HINTS;

	static {
		HINTS = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		HINTS.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		HINTS.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		HINTS.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}
	
	private static DecimalFormat df = new DecimalFormat("000.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
	private String resourceName;
	private File resourceFile;
	private int width;
	private int height;
	private double duration;
	private RexTimeCode tcIn;
	private RexTimeCode tcOut;
	private String text;
	private String thumb;

	public PrezElement(String resourceName) {
		super();

		this.resourceName = resourceName;
		setText("---");
		setThumb(null);
	}
	
	protected void generateThumbnail(File from, RexTimeCode tc, PrezGenerator generator) throws PrezException {
		try {
			BufferedImage img = ImageIO.read(from);
			int ow = img.getWidth();
			int oh = img.getHeight();

			double wr = (double) generator.getThumbWidth() / (double) ow;
			double hr = (double) generator.getThumbHeight() / (double) oh;

			double fr = Math.min(wr, hr);
			int nw = (int) (fr * ow);
			int nh = (int) (fr * oh);

			int wo = (generator.getThumbWidth() - nw) / 2;
			int ho = (generator.getThumbHeight() - nh) / 2;
			
			BufferedImage thumb = new BufferedImage(generator.getThumbWidth(), generator.getThumbHeight(), img.getType());
			Graphics2D g2 = (Graphics2D) thumb.getGraphics();
			
			AffineTransform t = new AffineTransform();
			t.translate(wo, ho);
			t.scale(fr, fr);
			g2.setRenderingHints(HINTS); 
			g2.drawImage(img, t, null);
			
			g2.dispose();
			
			String thumbFile = generator.getThumbsSubdir() + "/" + tc.toString("%02d_%02d_%02d_%04d") + ".png";
			ImageIO.write(thumb, "png", new File(generator.getDestinationDir(), thumbFile));
			
			setThumb(thumbFile);
		} catch (IOException e) {
			throw new PrezException(e);
		}
	}

	public double getDuration() {
		return duration;
	}

	public int getHeight() {
		return height;
	}

	public File getResourceFile() {
		return resourceFile;
	}

	public String getResourceName() {
		return resourceName;
	}

	public RexTimeCode getTcIn() {
		return tcIn;
	}

	public RexTimeCode getTcOut() {
		return tcOut;
	}

	public String getText() {
		return text;
	}

	public String getThumb() {
		return thumb;
	}

	public abstract String getTimelineBlockId() throws PrezException;

	public int getWidth() {
		return width;
	}

	public boolean hasThumb() {
		return thumb != null;
	}

	public abstract void init(PrezGenerator generator) throws PrezException;

	public abstract void process(PrezGenerator generator) throws PrezException;

	public void processMetadata(PrezGenerator generator) throws PrezException {
		try {
			generator.addToTextBlock(MetadataFactory.createSynchronizedTextLocalisationBlock(getTcIn(), getTcOut(), getText()));
			generator.addToTimelineBlock(getTimelineBlockId(), MetadataFactory.createLocalisationBlock(getTcIn(), getTcOut()));
		} catch (AmaliaException e) {
			throw new PrezException(e);
		}
	}

	public abstract void processThumbnail(PrezGenerator generator) throws PrezException;

	protected void setDuration(double duration) {
		this.duration = duration;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	PrezElement setResourceFile(File resourceFile) {
		this.resourceFile = resourceFile;
		return this;
	}

	protected void setTcIn(RexTimeCode tcIn) {
		this.tcIn = tcIn;
	}

	protected void setTcOut(RexTimeCode tcOut) {
		this.tcOut = tcOut;
	}

	public PrezElement setText(String text) {
		this.text = text;
		return this;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return "[size=" + getWidth() + "x" + getHeight() + ", duration=" + df.format(getDuration()) + "s, resource=" + getResourceName() + ", file=" + getResourceFile() + "]";
	}
}
