// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.notation;

/**
 * A class to describe words (lyrics) for a tune. 
 */
public class Words implements MusicElement {
	/** the content of the words. */
	private String content = null;
	
	/** Creates a new word element.
	 * @param theContent The words to be included in the score. */
	public Words(String theContent){
		content = theContent;
	}
	
	/** Returns the content of this words element.
	 * @return The content of this words element. */
	public String getContent() {
		return content;
	}
	
}