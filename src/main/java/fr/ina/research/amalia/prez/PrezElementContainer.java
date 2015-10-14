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

import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
 *
 * @author Nicolas HERVE - nherve@ina.fr
 */
public abstract class PrezElementContainer extends PrezElement implements Iterable<PrezElement> {
	private List<PrezElement> elements;

	public PrezElementContainer() {
		super();

		elements = new ArrayList<PrezElement>();
	}

	public boolean add(PrezElement e) {
		return elements.add(e);
	}

	@Override
	public double getPotentialDuration() throws PrezException {
		double fd = 0;
		for (PrezElement pe : this) {
			fd += pe.getPotentialDuration();
		}

		return fd;
	}

	@Override
	public void initResource(PrezGenerator generator) throws PrezException {
		generator.logInfo("Pre-processing " + toString());
		for (PrezElement pe : this) {
			pe.initResource(generator);
		}
	}

	@Override
	public RexTimeCode initTcInTcOut(RexTimeCode previous) throws PrezException {
		setTcIn(previous);
		for (PrezElement pe : this) {
			previous = pe.initTcInTcOut(previous);
		}
		setTcOut(previous);
		return previous;
	}

	@Override
	public void initWidthHeightDuration(PrezGenerator generator) throws PrezException {
		for (PrezElement pe : this) {
			pe.initWidthHeightDuration(generator);
		}

		int width = 0;
		int height = 0;
		double duration = 0;

		for (PrezElement pe : this) {
			if (width == 0) {
				width = pe.getWidth();
				height = pe.getHeight();
			} else {
				if ((width != pe.getWidth()) || (height != pe.getHeight())) {
					throw new PrezException("Element size doesn't match with previous elements");
				}
			}
			duration += pe.getDuration();
		}

		setWidth(width);
		setHeight(height);
		setDuration(duration);
	}

	@Override
	public Iterator<PrezElement> iterator() {
		return elements.iterator();
	}

	@Override
	public void process(PrezGenerator generator) throws PrezException {
		generator.logGenerator(this, "Processing");
		for (PrezElement pe : this) {
			pe.process(generator);
		}
	}

	@Override
	public String processThumbnail(PrezGenerator generator) throws PrezException {
		return elements.get(0).processThumbnail(generator);
	}

	public int size() {
		return elements.size();
	}

	@Override
	public String toString() {
		return "Container[" + size() + "] " + super.toString();
	}
}
