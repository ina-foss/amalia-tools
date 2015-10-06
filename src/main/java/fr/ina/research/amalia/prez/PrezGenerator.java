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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ina.research.amalia.AmaliaException;
import fr.ina.research.amalia.model.LocalisationBlock;
import fr.ina.research.amalia.model.MetadataBlock;
import fr.ina.research.amalia.model.MetadataBlock.MetadataType;
import fr.ina.research.amalia.model.MetadataFactory;
import fr.ina.research.rex.commons.log.RexDefaultLogger;
import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
*
* @author Nicolas HERVE - nherve@ina.fr
*/
public class PrezGenerator extends RexDefaultLogger {
	private File temporaryDir;
	private File destinationDir;
	private String thumbsSubdir;
	private File resourceDir;
	private String avconvCommand;
	private int width;
	private int height;
	private int thumbWidth;
	private int thumbHeight;
	private double duration;
	private int currentFrame;
	private double fps;
	private MetadataBlock textMetadata;
	private Map<String, MetadataBlock> timelineMetadata;
	private DecimalFormat df = new DecimalFormat("00000000");
	private String metadataPrefix;

	public PrezGenerator() {
		super();

		setFps(25);
		setThumbWidth(190);
		setThumbHeight(100);
		setThumbsSubdir("thumbs");
	}

	public void addToTextBlock(LocalisationBlock block) {
		textMetadata.addToRootLocalisationBlock(block);
	}

	public void addToTimelineBlock(String key, LocalisationBlock block) throws PrezException {
		try {
			if (!timelineMetadata.containsKey(key)) {
				timelineMetadata.put(key, createTimelineBlock(metadataPrefix + "-" + key));
			}
			timelineMetadata.get(key).addToRootLocalisationBlock(block);
		} catch (PrezException e) {
			throw new PrezException(e);
		}
	}

	private MetadataBlock createTimelineBlock(String id) throws PrezException {
		try {
			MetadataBlock timelineMetadata = MetadataFactory.createMetadataBlock(id, MetadataType.SEGMENTATION);
			timelineMetadata.setVersion(1);
			timelineMetadata.setRootLocalisationBlock(new RexTimeCode(0d), new RexTimeCode(duration));

			return timelineMetadata;
		} catch (AmaliaException e) {
			throw new PrezException(e);
		}
	}

	public String exec(String... cmd) throws PrezException {
		Process p = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			p = pb.start();

			InputStream out = p.getInputStream();

			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(out));
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			p.waitFor();
			br.close();

			return sb.toString();
		} catch (IOException e) {
			throw new PrezException(e);
		} catch (InterruptedException e) {
			throw new PrezException(e);
		} finally {
			if (p != null) {
				try {
					p.getInputStream().close();
					p.getOutputStream().close();
					p.getErrorStream().close();
				} catch (IOException e) {
					// ignored
				}
			}
		}
	}

	public void generate(Prez prez) throws PrezException {
		preProcess(prez);
		process(prez);
		postProcess(prez);
	}

	public String getAvconvCommand() {
		return avconvCommand;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public File getCurrentFrameFile() {
		return getFrameFile(currentFrame);
	}

	public File getDestinationDir() {
		return destinationDir;
	}

	public double getFps() {
		return fps;
	}

	public File getFrameFile(int frame) {
		return new File(temporaryDir, "f_" + df.format(frame) + ".png");
	}

	public int getNbFrame(double duration) {
		return (int) (duration * fps);
	}

	public File getResourceDir() {
		return resourceDir;
	}

	public File getTemporaryDir() {
		return temporaryDir;
	}

	public int getThumbHeight() {
		return thumbHeight;
	}

	public String getThumbsSubdir() {
		return thumbsSubdir;
	}

	public int getThumbWidth() {
		return thumbWidth;
	}

	public void moveToFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public void moveToNextFrame() {
		currentFrame++;
	}

	private void postProcess(Prez prez) throws PrezException {
		File mp4 = new File(destinationDir, prez.getName() + ".mp4");
		if (mp4.exists()) {
			logInfo("Cleaning " + mp4);
			mp4.delete();
		}
		File json = new File(destinationDir, prez.getName() + ".json");
		if (json.exists()) {
			logInfo("Cleaning " + json);
			json.delete();
		}

		logInfo("Post-processing " + mp4);
		exec(AvconvHelper.generateFinalVideoCommand(this, mp4));

		logInfo("Post-processing " + json);
		List<MetadataBlock> blocks = new ArrayList<MetadataBlock>();
		blocks.add(textMetadata);
		blocks.addAll(timelineMetadata.values());
		try {
			MetadataFactory.serializeToJsonFile(blocks, json);
		} catch (AmaliaException e) {
			throw new PrezException(e);
		}
	}

	private void preProcess(Prez prez) throws PrezException {
		width = 0;
		height = 0;
		duration = 0;

		if (temporaryDir.exists()) {
			String[] cmd = { "sh", "-c", "rm -fr " + temporaryDir.getAbsolutePath() + "/*.png" };
			logInfo("Cleaning " + temporaryDir.getAbsolutePath());
			exec(cmd);
		}

		temporaryDir.mkdirs();
		destinationDir.mkdirs();
		new File(destinationDir, thumbsSubdir).mkdirs();

		metadataPrefix = prez.getName();

		try {
			RexTimeCode previous = new RexTimeCode(0, getFps());

			for (PrezElement pe : prez) {
				File rf = new File(resourceDir, pe.getResourceName());
				if (rf.exists() && rf.isFile() && rf.canRead()) {
					pe.setResourceFile(rf);
					pe.init(this);
					logInfo("Pre-processing " + pe.toString());
					if (width == 0) {
						width = pe.getWidth();
						height = pe.getHeight();
					} else {
						if ((width != pe.getWidth()) || (height != pe.getHeight())) {
							throw new PrezException("Element size doesn't match with previous elements");
						}
					}
					duration += pe.getDuration();
					pe.setTcIn(previous);
					previous = new RexTimeCode(previous.getSecond());
					previous.add(pe.getDuration());
					pe.setTcOut(previous);
				} else {
					throw new PrezException("Unable to acces to file " + rf.getAbsolutePath());
				}
			}

			textMetadata = MetadataFactory.createMetadataBlock(metadataPrefix + "-text", MetadataType.SYNCHRONIZED_TEXT);
			textMetadata.setVersion(1);
			textMetadata.setRootLocalisationBlock(new RexTimeCode(0d), new RexTimeCode(duration));

			timelineMetadata = new HashMap<String, MetadataBlock>();
		} catch (AmaliaException e) {
			throw new PrezException(e);
		}
	}

	private void process(Prez prez) throws PrezException {
		currentFrame = 1;

		for (PrezElement pe : prez) {
			logInfo("[" + df.format(currentFrame) + "] Processing " + pe.toString());
			pe.process(this);
			pe.processThumbnail(this);
			pe.processMetadata(this);
		}
	}

	public PrezGenerator setAvconvCommand(String avconvCommand) {
		this.avconvCommand = avconvCommand;
		return this;
	}

	public PrezGenerator setDestinationDir(File destinationDir) {
		this.destinationDir = destinationDir;
		return this;
	}

	public void setFps(double fps) {
		this.fps = fps;
	}

	public PrezGenerator setResourceDir(File resourceDir) {
		this.resourceDir = resourceDir;
		return this;
	}

	public PrezGenerator setTemporaryDir(File temporaryDir) {
		this.temporaryDir = temporaryDir;
		return this;
	}

	public void setThumbHeight(int thumbHeight) {
		this.thumbHeight = thumbHeight;
	}

	public void setThumbsSubdir(String thumbsSubdir) {
		this.thumbsSubdir = thumbsSubdir;
	}

	public void setThumbWidth(int thumbWidth) {
		this.thumbWidth = thumbWidth;
	}

}
