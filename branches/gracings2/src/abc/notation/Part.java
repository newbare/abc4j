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

/** <TT>Part</TT> objects are used to define parts in tunes. */
public class Part implements Cloneable {
  private char m_label;
  //private Tune m_tune = null;
  private Tune.Music m_music = null;

  Part (Tune tune, char labelValue) {
    //m_tune = tune;
    m_label = labelValue;
    m_music = tune.createMusic();
  }
  
  Part (Part root) {
	  m_label = root.m_label;
	  m_music = (Tune.Music)root.m_music.clone();
  }

  /** Sets the label that identifies this part.
   * @param labelValue The label that identifies this part. */
  public void setLabel(char labelValue)
  { m_label = labelValue; }

  /** Returns the label that identifies this part.
   * @return The label that identifies this part. */
  public char getLabel()
  { return m_label; }

  /** Returns the music to this part.
   * @return The music associated to this part. */
  public Tune.Music getMusic()
  {return m_music;}

  void setMusic(Tune.Music score)
  {m_music = score;}
  	
  	public Object clone(){
  		return new Part(this);
  	}
}
