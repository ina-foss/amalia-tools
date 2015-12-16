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
package fr.ina.research.amalia.model.tweet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import fr.ina.research.amalia.AmaliaException;
import fr.ina.research.amalia.model.LocalisationBlock;
import fr.ina.research.amalia.model.MetadataBlock;
import fr.ina.research.amalia.model.MetadataBlock.MetadataType;
import fr.ina.research.amalia.model.MetadataFactory;
import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
 *
 * @author Nicolas HERVE - nherve@ina.fr
 */
public class SimpleTweetFormater {

	public SimpleTweetFormater() {
		super();
	}

	public MetadataBlock createHistogramMetadataBlock(String id, Date begin, Date end, int nbBin, Collection<SimpleTweet> tweets) throws AmaliaException {
		long offset = begin.getTime();
		double duration = (end.getTime() - offset) / 1000;

		double binsize = duration / nbBin;
		int[] bins = new int[nbBin];
		Arrays.fill(bins, 0);

		for (SimpleTweet tweet : tweets) {
			RexTimeCode tc = new RexTimeCode((double) (tweet.getDate().getTime() - offset) / 1000);
			int b = (int) (tc.getSecond() / binsize);
			if (b == nbBin) {
				b--;
			}
			bins[b]++;
		}

		MetadataBlock histoMetadata = MetadataFactory.createMetadataBlock(id, MetadataBlock.MetadataType.HISTOGRAM, new RexTimeCode(duration));
		histoMetadata.setVersion(1);
		histoMetadata.setAlgorithm("SimpleTweetFormater");
		histoMetadata.setProcessedNow();
		histoMetadata.setProcessor("Ina Research");
		histoMetadata.getRootLocalisationBlock().getDataBlock().addHistogram(bins, null);

		return histoMetadata;
	}

	public MetadataBlock createTextMetadataBlock(String id, Date begin, Date end, Collection<SimpleTweet> tweets) throws AmaliaException {
		long offset = begin.getTime();
		double duration = (end.getTime() - offset) / 1000;

		MetadataBlock textMetadata = MetadataFactory.createMetadataBlock(id, MetadataType.SYNCHRONIZED_TEXT);
		textMetadata.setVersion(1);
		textMetadata.setAlgorithm("SimpleTweetFormater");
		textMetadata.setProcessedNow();
		textMetadata.setProcessor("Ina Research");
		textMetadata.setRootLocalisationBlock(new RexTimeCode(0d), new RexTimeCode(duration));

		for (SimpleTweet tweet : tweets) {
			RexTimeCode tcin = new RexTimeCode((double) (tweet.getDate().getTime() - offset) / 1000);
			RexTimeCode tcout = new RexTimeCode(tcin.getSecond() + 1);
			LocalisationBlock tb = MetadataFactory.createSynchronizedTextLocalisationBlock(tcin, tcout, tweet.getText());
			tb.setThumb(tweet.getPp());
			textMetadata.addToRootLocalisationBlock(tb);
		}

		return textMetadata;
	}

	public List<MetadataBlock> createTweetMetadataBlocks(String idText, String idHisto, boolean filter, Date begin, Date end, int nbBin, Collection<SimpleTweet> tweets) throws AmaliaException {
		Collection<SimpleTweet> tweetsToConvert = null;
		if (filter) {
			tweetsToConvert = filterDate(begin, end, tweets);
		} else {
			tweetsToConvert = tweets;
		}
		List<MetadataBlock> blocks = new ArrayList<MetadataBlock>();
		blocks.add(createTextMetadataBlock(idText, begin, end, tweetsToConvert));
		blocks.add(createHistogramMetadataBlock(idHisto, begin, end, nbBin, tweetsToConvert));
		return blocks;
	}

	public Collection<SimpleTweet> filterDate(Date begin, Date end, Collection<SimpleTweet> tweets) throws AmaliaException {
		GregorianCalendar sgc = new GregorianCalendar();
		sgc.setTime(begin);

		GregorianCalendar egc = new GregorianCalendar();
		egc.setTime(end);

		GregorianCalendar cgc = new GregorianCalendar();

		List<SimpleTweet> filteredTweets = new ArrayList<SimpleTweet>();
		for (SimpleTweet st : tweets) {
			cgc.setTime(st.getDate());
			if (sgc.before(cgc) && cgc.before(egc)) {
				filteredTweets.add(st);
			}
		}

		return filteredTweets;
	}
}
