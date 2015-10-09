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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ina.research.amalia.AmaliaException;
import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
 *
 * @author Nicolas HERVE - nherve@ina.fr
 */
public class PrezVideoElement extends PrezElement {
	private String start;
	private String end;

	private String length;

	public PrezVideoElement(String resourceName) {
		this(resourceName, null, null);
	}

	public PrezVideoElement(String resourceName, String start, String end) {
		super(resourceName);

		this.start = start;
		this.end = end;
	}

	public String getEnd() {
		return end;
	}

	public String getLength() {
		return length;
	}

	@Override
	public double getPotentialDuration() throws PrezException {
		return getTime(end).getSecond() - getTime(start).getSecond();
	}

	public String getStart() {
		return start;
	}

	@Override
	public String getTimelineBlockId() throws PrezException {
		return "Video";
	}

	private RexTimeCode getTime(String t) throws PrezException {
		try {
			Pattern p = Pattern.compile("^([0-9]{2}):([0-9]{2}):([0-9]{2})$");
			Matcher ms = p.matcher(t);
			if (!ms.matches()) {
				throw new PrezException("Invalid time " + t);
			}
			return RexTimeCode.build(Integer.parseInt(ms.group(1)), Integer.parseInt(ms.group(2)), Integer.parseInt(ms.group(3)), 0);
		} catch (NumberFormatException e) {
			throw new PrezException(e);
		} catch (AmaliaException e) {
			throw new PrezException(e);
		}
	}

	@Override
	public void init(PrezGenerator generator) throws PrezException {
		String result = generator.exec(AvconvHelper.getVideoInfoCommand(generator, this));
		Dimension dim = AvconvHelper.getDimensions(result);
		setWidth((int) dim.getWidth());
		setHeight((int) dim.getHeight());

		RexTimeCode dur = AvconvHelper.getDuration(result);

		if ((start != null) && (end != null)) {
			RexTimeCode tcs = getTime(start);
			RexTimeCode tce = getTime(end);

			if ((tce.getSecond()) > dur.getSecond()) {
				throw new PrezException("Out of range " + tce + " > " + dur);
			}

			RexTimeCode tcl = new RexTimeCode(tce.getSecond() - tcs.getSecond());
			length = tcl.toString();

			setDuration(tcl.getSecond());

		} else {
			setDuration(dur.getSecond());
		}
	}

	@Override
	public void process(PrezGenerator generator) throws PrezException {
		generator.exec(AvconvHelper.getVideoExtractFramesCommand(generator, this));
		int last = generator.getCurrentFrame() + generator.getNbFrame(getDuration());
		for (int f = last - 2; f < (f + 3); f++) {
			if (!generator.getFrameFile(f).exists()) {
				generator.moveToFrame(f);
				break;
			}
		}
	}

	@Override
	public void processThumbnail(PrezGenerator generator) throws PrezException {
		double middleTime = (getTcOut().getSecond() + getTcIn().getSecond()) / 2;
		int middleFrame = generator.getNbFrame(middleTime);
		generateThumbnail(generator.getFrameFile(middleFrame), new RexTimeCode(middleTime), generator);
	}

	@Override
	public String toString() {
		if ((start != null) && (end != null)) {
			return "Video (" + start + " / " + end + ") " + super.toString();
		} else {
			return "Video " + super.toString();
		}
	}

}
