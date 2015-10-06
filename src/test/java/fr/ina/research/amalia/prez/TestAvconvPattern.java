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

import junit.framework.Assert;

import org.junit.Test;

import fr.ina.research.rex.commons.tc.RexTimeCode;

/**
 *
 * @author Nicolas HERVE - nherve@ina.fr
 */
public class TestAvconvPattern {

	public final String output1 = "avconv version 9.18-6:9.18-0ubuntu0.14.04.1, Copyright (c) 2000-2014 the Libav developers\n  built on Mar 16 2015 13:19:10 with gcc 4.8 (Ubuntu 4.8.2-19ubuntu1)\nInput #0, avi, from '/home/nherve/Travail/Data/amalia/grab.avi':\n  Metadata:\n    encoder         : Lavf54.20.4\n  Duration: 00:00:32.30, start: 0.000000, bitrate: 10601 kb/s\n    Stream #0.0: Video: mpeg4 (Simple Profile), yuv420p, 1920x1080 [PAR 1:1 DAR 16:9], 50 tbn, 50 tbc\nAt least one output file must be specified\n";
	public final String output2 = "avconv version 9.18-6:9.18-0ubuntu0.14.04.1, Copyright (c) 2000-2014 the Libav developers\n	  built on Mar 16 2015 13:19:10 with gcc 4.8 (Ubuntu 4.8.2-19ubuntu1)\n	[mov,mp4,m4a,3gp,3g2,mj2 @ 0xbb7840] multiple edit list entries, a/v desync might occur, patch welcome\n	Input #0, mov,mp4,m4a,3gp,3g2,mj2, from '/home/nherve/Travail/Data/videos/MGCAF0006836--AP_1_213419_231219.MP4':\n	  Metadata:\n	    major_brand     : isom\n	    minor_version   : 512\n	    compatible_brands: isomiso2avc1mp41\n	    creation_time   : 1970-01-01 00:00:00\n	    title           : 1974 - Retour de la Joconde - Titre collection:JT 13H\n	    album_artist    : 1974\n	    composer        : INA\n	    encoder         : Lavf53.6.0\n	    comment         : INA\n	    genre           : Actualités\n	    copyright       : INA\n	    description     : INA\n	    synopsis        : Titre:Retour de la Joconde\n	Titre collection: JT 13H\n	Date: 01/08/74\n	Chaine: ORTF\n	Heure: 13:00:08\n	    show            : INA\n	    network         : ORTF\n	    track           : 138\n	  Duration: 00:01:39.97, start: 0.000000, bitrate: 612 kb/s\n	    Stream #0.0(und): Video: h264 (High), yuv420p, 512x384 [PAR 1:1 DAR 4:3], 548 kb/s, 25.18 fps, 25 tbr, 25 tbn, 50 tbc\n	    Metadata:\n	      creation_time   : 1970-01-01 00:00:00\n	    Stream #0.1(und): Audio: aac, 48000 Hz, stereo, fltp, 64 kb/s\n	    Metadata:\n	      creation_time   : 1970-01-01 00:00:00\n	At least one output file must be specified\n";

	@Test
	public void test1() {
		try {
			RexTimeCode tc = AvconvHelper.getDuration(output1);
			Assert.assertEquals("00:00:32.3000", tc.toString());
			Dimension dim = AvconvHelper.getDimensions(output1);
			Assert.assertEquals(1920, (int) dim.getWidth());
			Assert.assertEquals(1080, (int) dim.getHeight());
		} catch (PrezException e) {
			Assert.fail("PrezException : " + e.getMessage());
		}
	}

	@Test
	public void test2() {
		try {
			RexTimeCode tc = AvconvHelper.getDuration(output2);
			Assert.assertEquals("00:01:39.9700", tc.toString());
			Dimension dim = AvconvHelper.getDimensions(output2);
			Assert.assertEquals(512, (int) dim.getWidth());
			Assert.assertEquals(384, (int) dim.getHeight());
		} catch (PrezException e) {
			Assert.fail("PrezException : " + e.getMessage());
		}
	}

}
