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

import java.io.Serializable;
import java.util.Date;

/**
 * A simplified tweet.
 *
 * @author Nicolas HERVE - nherve@ina.fr
 */
public class SimpleTweet implements Serializable {
	private static final long serialVersionUID = 6577276572680486788L;

	private Date date;
	private long id;
	private String text;
	private String user;
	private String screenName;
	private String pp;

	public SimpleTweet() {
		super();
	}

	public Date getDate() {
		return date;
	}

	public long getId() {
		return id;
	}

	public String getPp() {
		return pp;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getText() {
		return text;
	}

	public String getUser() {
		return user;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setPp(String pp) {
		this.pp = pp;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "SimpleTweet [date=" + date + ", id=" + id + ", text=" + text + ", user=" + screenName + "/" + user + ", pp=" + pp + "]";
	}

}
