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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import scanner.PositionableInCharStream;

/** This class encapsulates all information retrieved from a tune transcribed
 * using abc notation : header and music. */
public class Tune implements Cloneable, Serializable
{
  private static final long serialVersionUID = 5621598596188277056L;
  
//    Field name                     header this elsewhere Used by Examples and notes
  private String m_lyricist = null;       //yes							  A: author of lyrics (v2)
  private String m_area = null;           //yes                           A:Donegal, A:Bampton (v1.6)
  private String m_book = null;           //yes         yes       archive B:O'Neills
  private String m_composer = null;       //yes                           C:Trad.
  private String m_discography = null;    //yes                   archive D:Chieftans IV
  //private String m_fileName = null;       //            yes               see index.tex
  private String m_group = null;          //yes         yes       archive G:flute
  private String m_history = null;        //yes         yes       archive H:This this said to ...
  private String m_information = null;    //yes         yes       playabc
  private KeySignature m_key = null;    //yes         yes       playabc
  private String m_notes = null;          //yes                           N:see also O'Neills - 234
  private String m_origin = null;         //yes         yes       index   O:I, O:Irish, O:English
  private String m_rhythm = null;         //yes         yes       index   R:R, R:reel
  private String m_source = null;         //yes                           S:collected in Brittany
  private int m_referenceNumber = -1;    //first                         X:1, X:2
  private String m_transcriptionNotes = null;//yes                         Z:from photocopy, Z:Transcriber <email> <website>...
  private int m_elemskip = 0;            //yes    yes                    see Line Breaking
  private String m_fileurl = null;		//yes                           F:http://www.url.com/thisfile.abc
  private Vector m_titles;              //second yes                    T:Paddy O'Rafferty
  //private AbcMultiPartsDefinition abcMultiPartsDefinition = null;  //yes    yes                    P:ABAC, P:A, P:B
  //private AbcScore m_abcScore = null;
  private Part m_defaultPart = null;
  /** the multi parts definition of this tune if composed of several parts.
   * If this tune is a one-part tune, this attribtue is <TT>null</TT> */
  private MultiPartsDefinition m_multiPartsDef = null;
  private Hashtable m_parts = null;

  /** Creates a new empty tune. */
  public Tune() {
    super();
    m_titles = new Vector(2, 2);
    m_defaultPart = new Part(this, ' ');
  }
  
  /** Copy constructor
   * @param tune The tune to be copied in depth. */
  public Tune(Tune tune) {
	  try {
	  this.m_area = tune.m_area;
	  this.m_book = tune.m_book;
	  this.m_composer = tune.m_composer;
	  if (tune.m_defaultPart != null)
		  this.m_defaultPart = (Part)tune.m_defaultPart.clone();
	  this.m_discography = tune.m_discography;
	  this.m_elemskip = tune.m_elemskip;
	  this.m_fileurl = tune.m_fileurl;
	  this.m_group = tune.m_group;
	  this.m_history = tune.m_history;
	  this.m_information = tune.m_information;
	  if (tune.m_key != null)
		  this.m_key = (KeySignature)tune.m_key.clone();
	  this.m_lyricist = tune.m_lyricist;
	  //m_multiPartsDef after m_parts
	  this.m_notes = tune.m_notes;
	  this.m_origin = tune.m_origin;
	  if (tune.m_parts != null) {
		  this.m_parts = new Hashtable();
		for (Iterator itK = tune.m_parts.keySet().iterator(); itK.hasNext();) {
			Character key = (Character) itK.next();
			Part value = (Part) tune.m_parts.get(key);
			this.m_parts.put(new Character(key.charValue()), value.clone());
		}
		//this.m_parts = (Hashtable)tune.m_parts.clone();
	  }
	  if (tune.m_multiPartsDef != null)
		  this.m_multiPartsDef = (MultiPartsDefinition)tune.m_multiPartsDef.clone(this);
	  this.m_referenceNumber = tune.m_referenceNumber;
	  this.m_rhythm = tune.m_rhythm;
	  this.m_source = tune.m_source;
	  if (tune.m_titles != null)
		  this.m_titles = (Vector)tune.m_titles.clone();
	  this.m_transcriptionNotes = tune.m_transcriptionNotes;
	  } catch (CloneNotSupportedException never) {
		  never.printStackTrace();
	  }
  }

  /** Sets the geographic area where this tune comes from.
   * Corresponds to the "A:" abc field.
   * Ex: A:Donegal, A:Bampton
   * @param area The area where this tune comes from. */
  public void setArea(String area)
  {m_area = area; }

  /** Returns the area where this tune comes from.
   * @return The area where this tune comes from.
   * <TT>null</TT> if the area hasn't been specified. */
  public String getArea()
  { return m_area; }

  /** Sets the list of publications where
   * this tune can be found.
   * Corresponds to the "B:" abc field.
   * Ex: B:O'Neills
   * @param book The book where this tune comes from. */
  public void setBook(String book)
  { m_book = book; }

  /** Returns the list of publications where this
   * tune can be found.
   * @return Returns the list of publications where
   * this tune can be found,
   * <TT>null</TT> if the book hasn't been specified. */
  public String getBook()
  { return m_book; }

  /** Sets the composer of this tune.
   * Corresponds to the "C:" abc field.
   * Ex: C:Paddy Fahey
   * @param composer The composer who wrotes this tune.
   * For tunes known as traditional, you can use "traditional"
   * as parameter so that that people don't think the composer
   * has just been ignored. */
  public void setComposer(String composer)
  { m_composer = composer; }

  /** Returns the composer of this tune.
   * @return The composer of this tune,
   * <TT>null</TT> if the composer hasn't been specified. */
  public String getComposer()
  { return m_composer; }

  /** Sets recordings where this tune appears.
   * Corresponds to the "D:" abc field.
   * Ex: D:Gwenojenn
   * @param discography Recordings where this tune appears. */
  public void setDiscography(String discography)
  { m_discography = discography; }

  /** Returns recordings where this tune appears.
   * @return recordings where this tune appears,
   * <TT>null</TT> if the discography hasn't been specified. */
  public String getDiscography()
  { return m_discography; }

  public void setElemskip(int value)
  { m_elemskip = value; }

  public int getElemskip()
  { return m_elemskip; }

  public void setGroup(String value)
  { m_group = value; }

  public String getGroup()
  { return m_group; }

  /** Adds historical information about the tune.
   * Corresponds to the "H:" abc field.
   * Ex: H:Composed in 1930
   * @param history Historical information about
   * the tune to be added. */
  public void addHistory(String history)
  {
    if (m_history == null)
      m_history = history;
    else
      m_history += "\n" + history;
  }

  /** Returns historical information about the tune.
   * @return Historical information about the tune,
   * <TT>null</TT> if no historical information about
   * the tune is provided. */
  public String getHistory()
  { return m_history; }

  /**Sets the key signature of this tune.
   * @param key The key signature of this tune. */
  void setKey(KeySignature key)
  { m_key = key; }

  /** Returns the key signature of this tune.
   * @return The key signature of this tune. */
  public KeySignature getKey()
  { return m_key; }
  
  /** Returns the clef of the tune.
   * This is a shortcut to <TT>{@link #getKey()}.{@link KeySignature#getClef() getClef()}</TT>
   */
  public Clef getClef() {
	  if (getKey() != null)
		  return getKey().getClef();
	  else
		  return Clef.G;
  }

  /** Adds additional informations about the tune.
   * @param information Additional information about the tune. */
  public void addInformation(String information)
  {
    if (m_information == null)
    	m_information = information;
      else
    	m_information += "\n" + information;
  }

  /** Returns additional information about the tune.
   * @return Additional information about the tune,
   * <TT>null</TT> if no additional information about
   * the tune is provided. */
  public String getInformation()
  { return m_information; }
  
  /** Sets lyricist (author of lyrics)
   * Corresponds to the "A:" abc field in v2.
   * @param lyricist */
  public void setLyricist(String lyricist)
  { m_lyricist = lyricist; }
  
  /** Returns lyricist (author of lyrics)
   * Corresponds to the "A:" abc field in v2. */
  public String getLyricist()
  { return m_lyricist; }

  /** Adds notes concerning the transcription of this tune.
   * Corresponds to the "N:" abc field.
   * Ex: N:see also O'Neills - 234
   * @param notes Notes concerning the transcription of this tune. */
  public void addNotes(String notes)
  {
	  if (m_notes == null)
		  m_notes = notes;
	  else
		  m_notes += "\n" + notes;
  }

  /** Returns notes concerning the transcription of this tune.
   * @return Notes concerning the transcription of this tune,
   * <TT>null</TT> if no transcription notes about
   * the tune is provided. */
  public String getNotes()
  { return m_notes; }

  /** Sets the origin of this tune.
   * Corresponds to the "O:" abc field.
   * Ex: O:Irish, O:English
   * @param origin Origin of this tune : place or a person
   * that the music came from. N.B: For a person, setSource
   * is probably better.
   * @see #addSource(java.lang.String)*/
  public void setOrigin(String origin)
  { m_origin = origin; }

  /** Returns the origin of this tune.
   * @return The origin of this tune.
   * <TT>null</TT> if no origin about
   * the tune is provided. */
  public String getOrigin()
  { return m_origin; }

  /** Returns the part of the tune identified by the given label.
   * @param partLabel A part label.
   * @return The part of the tune identified by the given label, <TT>null</TT>
   * if no part with the specified label exists in this tune. */
  public Part getPart(char partLabel)
  {
    if (m_parts!=null)
    {
      Part p = (Part)m_parts.get(new Character(partLabel));
      return p;
    }
    else
      return null;
  }

  /** Creates a new part in this tune and returns it.
   * @param partLabel The label defining this new tune part.
   * @return The new part properly labeled. */
  public Part createPart(char partLabel) {
	  // check should be requiered to see if the label is not 
	  // empty or blank character because the blank character is
	  // used as flag for the default part.
	  Part part = new Part(this, partLabel);
	  if (m_parts==null) m_parts = new Hashtable();
	  m_parts.put(new Character(partLabel), part);
	  return part;
  }

  /** Sets the multi parts definition of this tune.
   * @param multiPartsDef The multi parts definition of this tune : defines
   * how parts should be played. */
  public void setMultiPartsDefinition(MultiPartsDefinition multiPartsDef)
  { m_multiPartsDef= multiPartsDef; }

  /** Returns the multi parts definition of this tune.
   * @return The multi parts definition of this tune. <TT>null</TT> is returned
   * if this tuned isn't composed of several parts. */
  public MultiPartsDefinition getMultiPartsDefinition()
  { return m_multiPartsDef; }

  /** Sets the rhythm of this tune.
   * Corresponds to the "R:" abc field.
   * Ex: R:hornpipe
   * @param rhythm Type of rhythm of this tune.
   * @see #getRhythm() */
  public void setRhythm(String rhythm)
  { m_rhythm = rhythm; }

  /** Returns the rhythm of this tune.
   * @return The rhythm of this tune,
   * <TT>null</TT> if no rhythm about
   * the tune is provided.
   * @see #setRhythm(java.lang.String)*/
  public String getRhythm()
  {return m_rhythm; }

  /** Adds a source of this tune.
   * Corresponds to the "S:" abc field.
   * Ex: S:collected in Brittany
   * @param source The source of this tune (place where
   * it has been collected for ex). */
  public void addSource(String source)
  {
	  if (m_source == null)
		  m_source = source;
	  else
		  m_source += "\n" + source;
  }

  /** Returns the source of this tune.
   * @return The source of this tune. <TT>null</TT> if no source is provided. */
  public String getSource()
  { return m_source; }

  /** Adds a title to this tune.
   * Corresponds to the "T:" abc field.
   * Ex: T:Dansaone
   * @param title A title for this tune. */
  public void addTitle(String title)
  { m_titles.addElement(title); }

  /** Removes one the titles of this tune.
   * @param title The title to be removed of this tune. */
  public void removeTitle(String title)
  { m_titles.removeElement(title); }

  /** Returns the titles of this tune.
   * @return An array containing the titles of this tune. If this tune has no
   * title, <TT>null</TT> is returned. */
  public String[] getTitles()
  {
    String[] titles = null;
    if (m_titles.size()!=0)
    {
      titles = new String[m_titles.size()];
      for (int i=0; i<m_titles.size(); i++)
        titles[i]=(String)m_titles.elementAt(i);
    }
    return titles;
  }

  /** Sets the reference number of this tune.
   * @param id The reference number of this tune. */
  public void setReferenceNumber(int id)
  { m_referenceNumber = id; }

  /** Returns the reference number of this tune.
   * @return The reference number of this tune. */
  public int getReferenceNumber()
  { return m_referenceNumber; }

  /** Adds notes about transcription of this tune.
   * Corresponds to the "Z:" abc field.
   * Ex: Z:collected in Brittany
   * @param transciptionNotes notes about about who did the ABC
   * transcription : email addresses and URLs are appropriate here,
   * and other contact information such as phone numbers or postal
   * addresses may be included. */
  public void addTranscriptionNotes(String transciptionNotes)
  {
    if (m_transcriptionNotes == null)
      m_transcriptionNotes = transciptionNotes;
    else
      m_transcriptionNotes += "\n" + transciptionNotes;
  }

  /** Returns transcription notes of this tune.
   * @return Transcription notes of this tune. */
  public String getTranscriptionNotes()
  { return m_transcriptionNotes; }
  
  /** Sets the url of the file */
  public void setFileURL(String fileurl)
  { m_fileurl = fileurl; }
  /** Returns the URL of the file */
  public String getFileURL()
  { return m_fileurl; }
  
	static public Tune transpose(Tune t, int semitones) {
		Tune ret = (Tune) t.clone();
		if (semitones == 0)
			return ret;
		// collect all part's music to transpose
		Vector musics = new Vector();
		musics.add(ret.m_defaultPart.getMusic());
		if (ret.m_multiPartsDef != null) {
			Vector alreadyAddedParts = new Vector();
			Part[] parts = ret.m_multiPartsDef.toPartsArray();
			for (int i = 0; i < parts.length; i++) {
				char label = parts[i].getLabel();
				// already added, skip it!
				if (alreadyAddedParts.contains(new Character(label)))
					continue;
				musics.add(parts[i].getMusic());
				alreadyAddedParts.add(new Character(label));
			}
		}

		KeySignature lastKey = ret.getKey();
		if (lastKey == null)
			lastKey = new KeySignature(Note.C, KeySignature.MAJOR);
		Note lastKeyNote = new Note(lastKey.getNote(), lastKey.getAccidental());
		KeySignature noneTranspKey = lastKey;
		Note noneTranspKeyNote = lastKeyNote;
		ret.setKey(KeySignature.transpose(noneTranspKey, semitones));
		Iterator itMusics = musics.iterator();
		int musiccount = 0;
		while (itMusics.hasNext()) {
			musiccount++;
			Music music = (Music) itMusics.next();
			for (int i = 0, j = music.size(); i<j; i++) {
				MusicElement element = (MusicElement) music.elementAt(i);
				if (element instanceof KeySignature) {
					noneTranspKey = (KeySignature) element;
					noneTranspKeyNote = new Note(noneTranspKey.getNote(), noneTranspKey.getAccidental());
					KeySignature transposed = KeySignature
						.transpose(noneTranspKey, semitones);
					music.setElementAt(transposed, i);
					lastKey = transposed;
					byte octav = 0;
					try {
						octav = Note.getOctaveTransposition((byte) (noneTranspKeyNote.getHeight()+semitones));
					} catch (Exception e) { //Illegal arg if transp note is accidented
						octav = Note.getOctaveTransposition((byte) (noneTranspKeyNote.getHeight()+semitones-1));
					}
					lastKeyNote = new Note(lastKey.getNote(), lastKey.getAccidental(), octav);
				} else if ((element instanceof Note)
						&& !((Note) element).isRest()) {
					Note original = (Note) element;
					Note transp = (Note) transpose_Note(original, noneTranspKeyNote,
							noneTranspKey, lastKeyNote, lastKey);
					music.setElementAt(transp, i);
				} else if (element instanceof MultiNote) {
					MultiNote multi = (MultiNote) element;
					MultiNote transp = (MultiNote) transpose_Note(multi, noneTranspKeyNote,
							noneTranspKey, lastKeyNote, lastKey);
					music.setElementAt(transp, i);
				}
			}// end for each element in the music
		}// end while there are more music part
		return ret;
	}
	
	/**
	 * Subfunction of {@link #transpose(Tune, int)} which transpose
	 * a Note, its graces notes (recursively)
	 */
	static private NoteAbstract transpose_Note(NoteAbstract original,
			Note noneTranspKeyNote, KeySignature noneTranspKey,
			Note lastKeyNote, KeySignature lastKey) {
		NoteAbstract transp = null;
		if (original instanceof Note) {
			Interval interval = new Interval(noneTranspKeyNote, (Note) original,
					noneTranspKey);
			// Note transp = Note.transpose(original, semitones, lastKey);
			Note transpHeight = interval.calculateSecondNote(lastKeyNote, lastKey);
			try {
				transp = (Note) ((Note) original).clone();
			} catch (CloneNotSupportedException never) {
				never.printStackTrace();
			}
			((Note) transp).setHeight(transpHeight.getHeight());
			((Note) transp).setOctaveTransposition(transpHeight.getOctaveTransposition());
			((Note) transp).setAccidental(transpHeight.getAccidental());
		} else if (original instanceof MultiNote) {
			try {
				transp = (MultiNote) ((MultiNote) original).clone();
			} catch (CloneNotSupportedException never) {
				never.printStackTrace();
			}
			Note[] notes = ((MultiNote) transp).toArray();
			for (int k = 0; k < notes.length; k++) {
				notes[k] = (Note) transpose_Note(notes[k], noneTranspKeyNote,
						noneTranspKey, lastKeyNote, lastKey);
			}
			((MultiNote) transp).setNotes(notes);
		}
//		TieDefinition tie = original.getTieDefinition();
//		if (tie != null) {
//			if (tie.getStart() == original)
//				tie.setStart(transp);
//			else if (tie.getEnd() == original)
//				tie.setEnd(transp);
//		}
//		Vector slurs = original.getSlurDefinitions();
//		for (Iterator it = slurs.iterator(); it.hasNext();) {
//			SlurDefinition slur = (SlurDefinition) it.next();
//			if (slur.getStart() == original)
//				slur.setStart(transp);
//			else if (slur.getEnd() == original)
//				slur.setEnd(transp);
//		}
		Chord chord = transp.getChord();
		if (chord != null) {
			if (chord.hasNote())
				chord.setNote((Note) transpose_Note(chord.getNote(),
						noneTranspKeyNote, null, lastKeyNote, null));
			if (chord.hasBass())
				chord.setBass((Note) transpose_Note(chord.getBass(),
						noneTranspKeyNote, null, lastKeyNote, null));
		}
		if (transp.hasGracingNotes()) {
			Note[] graces = transp.getGracingNotes();
			for (int k = 0; k < graces.length; k++) {
				graces[k] = (Note) transpose_Note(graces[k], noneTranspKeyNote,
						noneTranspKey, lastKeyNote, lastKey);
			}
		}
		return transp;
	}

	/**
	 * Return the music for graphical rendition, i.e. if structure is ABBA, and
	 * score contains 2 parts P:A and P:B, returns a music composed of the 2
	 * parts. {@link #getMusic()} returns a music composed of 4 parts which is
	 * ok for audio/midi rendition, but not good for graphical score rendition.
	 */
	public Music getMusicForGraphicalRendition() {
		if (m_multiPartsDef == null)
			return (m_defaultPart.getMusic());
		else {
			Vector alreadyAddedParts = new Vector();
			Music globalScore = new Music();
			Music defaultScore = m_defaultPart.getMusic();
			for (int i = 0; i < defaultScore.size(); i++)
				globalScore.addElement(defaultScore.elementAt(i));
			Part[] parts = m_multiPartsDef.toPartsArray();
			for (int i = 0; i < parts.length; i++) {
				char label = parts[i].getLabel();
				// already added, skip it!
				if (alreadyAddedParts.contains(new Character(label)))
					continue;
				globalScore.addElement(new PartLabel(label));
				Music score = parts[i].getMusic();
				for (int j = 0; j < score.size(); j++)
					globalScore.addElement(score.elementAt(j));
				alreadyAddedParts.add(new Character(label));
			}
			return globalScore;
		}
	}

  /**
	 * Returns the music part of this tune.
	 * 
	 * @see #getMusicForGraphicalRendition()
	 * @return The music part of this tune. If this tune isn't composed of
	 *         several parts this method returns the "normal" music part. If
	 *         this tune is composed of several parts the returned music is
	 *         generated so that the tune looks like a "single-part" one. If you
	 *         want to retrieve the music related to each part separatly just do
	 *         <TT>getPart(char partLabel).getScore()</TT>.
	 * @see #getPart(char)
	 */
  public Music getMusic()
  {
    if (m_multiPartsDef==null)
      return (m_defaultPart.getMusic());
    else
    {
      Music globalScore = new Music();
      Music defaultScore = m_defaultPart.getMusic();
      for (int i=0; i<defaultScore.size(); i++)
        globalScore.addElement(defaultScore.elementAt(i));
      Part[] parts = m_multiPartsDef.toPartsArray();
      for (int i=0; i<parts.length; i++)
      {
    	globalScore.addElement(new PartLabel(parts[i].getLabel()));
        Music score = parts[i].getMusic();
        for (int j=0; j<score.size(); j++)
          globalScore.addElement(score.elementAt(j));
      }
      return globalScore;
    }
  }
  
  public Tempo getGeneralTempo() {
	  Music music = getMusic();
	  for (int i = 0; i < music.size(); i++) {
		  if (music.elementAt(i) instanceof Tempo) //got it!
			  return (Tempo) music.elementAt(i);
		  if (music.elementAt(i) instanceof NoteAbstract)
			  return null; //Found some notes, meaning it's
		  //a tempo change, but no general tempo defined
	  }
	  return null; //no tempo object found
  }
  
  /** Returns a string representation of this tune.
   * @return A string representation of this tune. */
  public String toString()
  {
    String string2return = "";
    if (m_titles.size()!=0)
      string2return = m_titles + "(" + m_referenceNumber + ")@" + hashCode();
    else
      string2return = "(" + m_referenceNumber + ")@" + hashCode();
    return string2return;
  }
  
	/**
  	 * Returns a deep clone of the Tune object
  	 */
  	public Object clone() {
  		/*if (false) {
  			Tune ret = new Tune(this);
  			return ret;
  		}*/
  		Tune ret = null;
  		try {
  			long s = System.currentTimeMillis();
			// Write the object out to a byte array
			FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(fbos);
			out.writeObject(this);
			out.flush();
			out.close();

			// Retrieve an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
			ret = (Tune) in.readObject();
			long e = System.currentTimeMillis();
			System.out.println("Tune.clone: "+fbos.getSize()+" en "+((e-s)/1000.0)+"s");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return ret;
  	}

  /** Creates a new score. 
   * NB : bullshit pattern, why going through a tune to create a score???
   * The reference to the tune is not kept in the score. */
  Music createMusic()
  { return new Music(); }

  /**
   * A Music is a collection of {@link MusicElement} (notes, bars...).
   */
  public class Music extends Vector implements Cloneable, Serializable {
	  
	private static final long serialVersionUID = 5411161761359626571L;
	
	protected transient NoteAbstract lastNote = null;
	
	public Music ()
    { super (); }

    public void addElement(KeySignature key) {
      if (Tune.this.getKey()==null)
        Tune.this.setKey(key);
      addElement0(key);
    }
    
    public void addElement(NoteAbstract note) {
    	//System.out.println("adding note " + note + " to " + this);
        lastNote = note;
        addElement0(note);
    }

	public void addElement(MusicElement element) {
		addElement0(element);
	}

	private synchronized void addElement0(MusicElement me) {
		if (me == null)
			System.err.println("addElement0 null");
		else if (me.getReference().getPart() == ' ') {
			// do not change x in tune.getMusic()
			me.getReference().setX((short) size());
			super.addElement(me);
		}
	}

    /* Returns the last note that has been added to this score.
     * @return The last note that has been added to this score. <TT>null</TT>
     * if no note in this score. */
    public NoteAbstract getLastNote() {
    	if (lastNote == null) {
    		for (int i = super.size()-1; i >= 0; i--) {
    			if (super.elementAt(i) instanceof NoteAbstract) {
    				lastNote = (NoteAbstract) super.elementAt(i);
    				break;
    			}
    		}
    	}
    	return lastNote;
    }
    
    /** Returns the score element location at the specified offset.
     * @param offset An offset in a char stream.  
     * @return The score element location at the specified offset.
     */  
    public MusicElement getElementAt(int offset) {
    	MusicElement foundElement = null;
    	MusicElement current = null;
    	for (int i=0; i<size(); i++) {
    		current = (MusicElement)elementAt(i);
    		if (current instanceof PositionableInCharStream) {
    			PositionableInCharStream pos = (PositionableInCharStream)current; 
    			if (pos.getPosition().getCharactersOffset()<=offset && 
    					(pos.getPosition().getCharactersOffset()+ pos.getLength())>=offset 
    					)
    				foundElement=current;
    		}
    	}
    	return foundElement;
    }
    
    public int indexOf(MusicElement elmnt) {
    	if (elmnt != null) {
    		Object elmntIt = null;
	    	boolean isLooking4Note = elmnt instanceof Note;
	    	for (int i=0; i<size(); i++){
	    		elmntIt = elementAt(i);
	    		if (elmntIt != null) {
		    		if (elementAt(i) instanceof MultiNote && isLooking4Note) {
		    			if (((MultiNote)elmntIt).contains((Note)elmnt))
		    					return i;
		    		}
		    		else
		    			if (elementAt(i).equals(elmnt))
		    				return i;
	    		}
	    	}
    	}
    	return -1;
    }
    
    /*public Note getHighestNoteBewteen(int scoreElmntIndexBegin, int ScoreElmtIndexEnd) throws IllegalArgumentException {
    	if (scoreElmntIndexBegin>ScoreElmtIndexEnd)
    		throw new IllegalArgumentException("First parameter " + scoreElmntIndexBegin + " must be located before " + ScoreElmtIndexEnd + " in the score");
    	Note highestNote = null;
    	ScoreElementInterface currentScoreEl;
    	for (int i=scoreElmntIndexBegin; i<=ScoreElmtIndexEnd; i++) {
    		currentScoreEl=(ScoreElementInterface)elementAt(i);
    		if (currentScoreEl instanceof Note && (highestNote==null || ((Note)currentScoreEl).isHigherThan(highestNote)))
    			highestNote = (Note)currentScoreEl;
    	}
    	return highestNote;
    }*/
    
    /** Returns the highest note between two music elements. <TT>MultiNote</TT> instances
     * are ignored.  
     * @param elmtBegin The music element where to start (included) the search
     * of the highest note.
     * @param elmtEnd The music element where to end (included) the search
     * of the highest note.
     * @return The highest note or multinote between two music elements. <TT>null</TT> if
     * no note has been found between the two music elements.
     * @throws IllegalArgumentException Thrown if one of the music elements hasn't been found 
     * in the music or if the <TT>elmtEnd</TT> param is located before the <TT>elmntBegin</TT> 
     * param in the music. */
    public NoteAbstract getHighestNoteBewteen(MusicElement elmtBegin, MusicElement elmtEnd)
    	throws IllegalArgumentException {
    	NoteAbstract highestNote = null;
    	int highestNoteHeight = Note.REST;
    	if (elmtBegin instanceof NoteAbstract) {
    		if (!((elmtBegin instanceof Note) && ((Note)elmtBegin).isRest())) {
    			highestNote = (NoteAbstract) elmtBegin;
        		highestNoteHeight = 
	    			(highestNote instanceof MultiNote)
	    				?((MultiNote)elmtBegin).getHighestNote().getMidiLikeHeight()
	    				:((Note) highestNote).getMidiLikeHeight();
    		}
    	}
    	int idxBegin = indexOf(elmtBegin);
    	int idxEnd = indexOf(elmtEnd);
    	if (idxBegin==-1)
    		throw new IllegalArgumentException("Note " + elmtBegin + " hasn't been found in tune");
    	if (idxEnd==-1)
        	throw new IllegalArgumentException("Note " + elmtEnd + " hasn't been found in tune");
    	if (idxBegin>idxEnd)
    		throw new IllegalArgumentException("Note " + elmtBegin + " is located after " + elmtEnd + " in the score");
    	MusicElement currentScoreEl;
    	int currentNoteHeight;
    	for (int i=idxBegin+1; i<=idxEnd; i++) {
    		currentScoreEl=(MusicElement)elementAt(i);
    		if (currentScoreEl instanceof NoteAbstract) {
    	    	currentNoteHeight = 
    	    		(currentScoreEl instanceof MultiNote)
    	    			?((MultiNote)currentScoreEl).getHighestNote().getMidiLikeHeight()
    	    			:((Note)currentScoreEl).getMidiLikeHeight();
    	    	if ((currentNoteHeight != Note.REST) && 
    	    		((highestNoteHeight == Note.REST)
    	    			|| (currentNoteHeight > highestNoteHeight))) {
    	    		highestNoteHeight = currentNoteHeight;
    	    		highestNote = (NoteAbstract) currentScoreEl;
    	    	}
    		}
    	}
    	return highestNote;
	}
    
    /**  
     * @param elmtBegin (included)
     * @param elmtEnd (included)
     * @return The lowest note or multinote between the two given score elements if found.
     * <TT>null</TT> if no note has been found between the two music elements.
     * @throws IllegalArgumentException
     */
    public NoteAbstract getLowestNoteBewteen(MusicElement elmtBegin, MusicElement elmtEnd)
		throws IllegalArgumentException {
    	NoteAbstract lowestNote = null;
    	int lowestNoteHeight = Note.REST;
    	if (elmtBegin instanceof NoteAbstract) {
    		if (!((elmtBegin instanceof Note) && ((Note)elmtBegin).isRest())) {
    			lowestNote = (NoteAbstract) elmtBegin;
        		lowestNoteHeight = 
	    			(lowestNote instanceof MultiNote)
	    				?((MultiNote)elmtBegin).getLowestNote().getMidiLikeHeight()
	    				:((Note) lowestNote).getMidiLikeHeight();
    		}
    	}
    	int idxBegin = indexOf(elmtBegin);
    	int idxEnd = indexOf(elmtEnd);
    	if (idxBegin==-1)
    		throw new IllegalArgumentException("Note " + elmtBegin + " hasn't been found in tune");
    	if (idxEnd==-1)
        	throw new IllegalArgumentException("Note " + elmtEnd + " hasn't been found in tune");
    	if (idxBegin>idxEnd)
    		throw new IllegalArgumentException("Note " + elmtBegin + " is located after " + elmtEnd + " in the score");
    	MusicElement currentScoreEl;
    	int currentNoteHeight;
    	for (int i=idxBegin+1; i<=idxEnd; i++) {
    		currentScoreEl=(MusicElement)elementAt(i);
    		if (currentScoreEl instanceof NoteAbstract) {
    	    	currentNoteHeight = 
    	    		(currentScoreEl instanceof MultiNote)
    	    			?((MultiNote)currentScoreEl).getLowestNote().getMidiLikeHeight()
    	    			:((Note)currentScoreEl).getMidiLikeHeight();
    	    	if ((currentNoteHeight != Note.REST) && 
    	    		((lowestNoteHeight == Note.REST)
    	    			|| (currentNoteHeight < lowestNoteHeight))) {
    	    		lowestNoteHeight = currentNoteHeight;
    	    		lowestNote = (NoteAbstract) currentScoreEl;
    	    	}
    		}
    	}
    	return lowestNote;

	}
    
    /**
     * Returns an element for the given reference, <TT>null</TT>
     * if not found
     * @param ref
     * @return
     */
    public MusicElement getElementByReference(MusicElementReference ref) {
    	for (Iterator it = iterator(); it.hasNext();) {
			MusicElement element = (MusicElement) it.next();
			if (element.getReference().equals(ref))
				return element;
		}
    	return null;
    }
    
    /** Returns a collection of Note between begin and end included
     * @param elmtBegin
     * @param elmtEnd
     * @return a Collection of NoteAbstract (Note or MultiNote)
     * @throws IllegalArgumentException
     */
    public Collection getNotesBetween(MusicElement elmtBegin, MusicElement elmtEnd)
    	throws IllegalArgumentException {
    	int idxBegin = indexOf(elmtBegin);
    	int idxEnd = this.indexOf(elmtEnd);
    	if (idxBegin==-1)
    		throw new IllegalArgumentException("Note " + elmtBegin + " hasn't been found in tune");
    	if (idxEnd==-1)
        	throw new IllegalArgumentException("Note " + elmtEnd + " hasn't been found in tune");
    	if (idxBegin>idxEnd)
    		throw new IllegalArgumentException("Note " + elmtBegin + " is located after " + elmtEnd + " in the score");
    	Collection ret = new Vector();
    	MusicElement currentScoreEl;
    	for (int i=idxBegin; i<=idxEnd; i++) {
    		currentScoreEl=(MusicElement)elementAt(i);
    		if (currentScoreEl instanceof NoteAbstract)
    			ret.add((NoteAbstract)currentScoreEl);
    	}
    	return ret;
    }

    /**
     * @return The shortest note in the tune.
     */
    public Note getShortestNote() throws IllegalArgumentException {
    	Note shortestNote = null;
    	//init
    	MusicElement currentScoreEl;
    	Iterator it = iterator();
    	while (it.hasNext()) {
    		currentScoreEl=(MusicElement)it.next();
    		if (currentScoreEl instanceof Note) {
    			if (shortestNote == null)
    				shortestNote = (Note)currentScoreEl;
    			else if (((Note)currentScoreEl)
    						.isShorterThan(shortestNote))
    				shortestNote = (Note)currentScoreEl;
    		} else if (currentScoreEl instanceof MultiNote) {
    			Note shortestInChrod = ((MultiNote) currentScoreEl).getShortestNote();
    			if (shortestNote == null)
    				shortestNote = shortestInChrod;
    			else if (shortestInChrod.isShorterThan(shortestNote))
    				shortestNote = shortestInChrod;
    		}
    	}
    	return shortestNote;

	}
    
    
    /*public Note getLowestNoteBewteen(int scoreElmntIndexBegin, int ScoreElmtIndexEnd) throws IllegalArgumentException {
    	if (scoreElmntIndexBegin>ScoreElmtIndexEnd)
    		throw new IllegalArgumentException("First parameter " + scoreElmntIndexBegin + " must be located before " + ScoreElmtIndexEnd + " in the score");
    	Note lowestNote = null;
    	ScoreElementInterface currentScoreEl;
    	for (int i=scoreElmntIndexBegin; i<=ScoreElmtIndexEnd; i++) {
    		currentScoreEl=(ScoreElementInterface)elementAt(i);
    		if (currentScoreEl instanceof Note && (lowestNote==null || ((Note)currentScoreEl).isLowerThan(lowestNote)))
    			lowestNote = (Note)currentScoreEl;
    		else
    			if (currentScoreEl instanceof MultiNote 
    					&& (lowestNote==null || ((MultiNote)currentScoreEl).getLowestNote().isLowerThan(lowestNote)))
    					lowestNote = ((MultiNote)currentScoreEl).getLowestNote();
    	}
    	return lowestNote;
    }*/

	/**
	 * Returns <TT>true</TT> if this tune music has chord names,
	 * <TT>false</TT> otherwise.
	 */
    public boolean hasChordNames() {
    	MusicElement currentScoreEl;
       	Iterator it = iterator();
    	while (it.hasNext()) {
			currentScoreEl=(MusicElement)it.next();
			if (currentScoreEl instanceof NoteAbstract) {
				if (((NoteAbstract)currentScoreEl).getChordName() != null)
					return true;
			}
		}
		return false;
	}
    
    private boolean hasObject(Class musicElementClass) {
    	MusicElement currentScoreEl;
    	Iterator it = iterator();
    	while (it.hasNext()) {
			currentScoreEl=(MusicElement)it.next();
			if ((currentScoreEl!=null)
					&& currentScoreEl.getClass().equals(musicElementClass)) {
				return true;
			}
		}
		return false;
    }
    
	/**
	 * Returns <TT>true</TT> if this tune music has part label(s),
	 * <TT>false</TT> otherwise.
	 */
	public boolean hasPartLabel() {
		return hasObject(PartLabel.class);
	}
	
	/**
	 * Returns <TT>true</TT> if this tune music has tempo,
	 * <TT>false</TT> otherwise.
	 */
	public boolean hasTempo() {
		return hasObject(Tempo.class);
	}
	//TODO hasLyrics...
	
	public Object clone() {
		/*Music ret = new Music(size());
		for (Iterator it = this.iterator(); it.hasNext();) {
			MusicElement me = (MusicElement) it.next();
			MusicElement clone = (MusicElement) me.clone();
			ret.addElement(clone);
			if (me instanceof NoteAbstract) {
				
			}
		}
		return ret;*/
		return super.clone();
	}
  }
  
  /**
   * ByteArrayInputStream implementation that does not
   * synchronize methods.
   */
  private class FastByteArrayInputStream extends InputStream {
  	/** Our byte buffer */
  	protected byte[] buf = null;
  	/** Number of bytes that we can read from the buffer */
  	protected int count = 0;
  	/** Number of bytes that have been read from the buffer */
  	protected int pos = 0;

  	public FastByteArrayInputStream(byte[] buf, int count) {
  		this.buf = buf;
  		this.count = count;
  	}

  	public final int available() {
  		return count - pos;
  	}

  	public final int read() {
  		return (pos < count) ? (buf[pos++] & 0xff) : -1;
  	}

  	public final int read(byte[] b, int off, int len) {
  		if (pos >= count)
  			return -1;
  		if ((pos + len) > count)
  			len = (count - pos);
  		System.arraycopy(buf, pos, b, off, len);
  		pos += len;
  		return len;
  	}

  	public final long skip(long n) {
  		if ((pos + n) > count)
  			n = count - pos;
  		if (n < 0)
  			return 0;
  		pos += n;
  		return n;
  	}
  }
  
  /**
   * ByteArrayOutputStream implementation that doesn't
   * synchronize methods and doesn't copy the data on
   * toByteArray().
   */
  private class FastByteArrayOutputStream extends OutputStream {
  	/** Buffer and size */
  	protected byte[] buf = null;
  	protected int size = 0;

  	/** Constructs a stream with buffer capacity size 5K */
  	public FastByteArrayOutputStream() {
  		this(5 * 1024);
  	}

  	/** Constructs a stream with the given initial size */
  	public FastByteArrayOutputStream(int initSize) {
  		this.size = 0;
  		this.buf = new byte[initSize];
  	}

  	/** Ensures that we have a large enough buffer for the given size. */
  	private void verifyBufferSize(int sz) {
  		if (sz > buf.length) {
  			byte[] old = buf;
  			buf = new byte[Math.max(sz, 2 * buf.length)];
  			System.arraycopy(old, 0, buf, 0, old.length);
  			old = null;
  		}
  	}

  	public int getSize() {
  		return size;
  	}

  	/**
  	 * Returns the byte array containing the written data. Note that this array
  	 * will almost always be larger than the amount of data actually written.
  	 */
  	public byte[] getByteArray() {
  		return buf;
  	}

  	public final void write(byte b[]) {
  		verifyBufferSize(size + b.length);
  		System.arraycopy(b, 0, buf, size, b.length);
  		size += b.length;
  	}

  	public final void write(byte b[], int off, int len) {
  		verifyBufferSize(size + len);
  		System.arraycopy(b, off, buf, size, len);
  		size += len;
  	}

  	public final void write(int b) {
  		verifyBufferSize(size + 1);
  		buf[size++] = (byte) b;
  	}

  	public void reset() {
  		size = 0;
  	}

  	/** Returns a ByteArrayInputStream for reading back the written data */
  	public InputStream getInputStream() {
  		return new FastByteArrayInputStream(buf, size);
  	}
  }
}
