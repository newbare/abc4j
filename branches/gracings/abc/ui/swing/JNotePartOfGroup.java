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

package abc.ui.swing;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import abc.notation.Note;

class JNotePartOfGroup extends JNote implements JGroupableNote {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	private static final int NOTATION_CONTEXT = ScoreMetrics.NOTE_GLYPH;

	/*protected int stemX = -1;
	protected int stemYBegin = -1;  */
	protected int stemYEnd = -1;

	public JNotePartOfGroup(Note noteValue, Point2D base, ScoreMetrics c) {
		super(noteValue, base, c);
		//onBaseChanged();
	}

	protected void valuateNoteChars() {
		// beamed notes are always 1/8th notes or less
		// so just display a stemless note - stems and beams
		// are drawn programmatically
		noteChars = ScoreMetrics.NOTE;
	}

	protected void onBaseChanged() {
		//System.out.println("JNotePartOfGroup.onBasechanged : "+note.toString());
		super.onBaseChanged();

		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
		// FIXME: ... 1st time called this is always null. why ?
		if (glyphDimension == null) {
			System.err.println("JNotePartOfGroup : glyphDimension is null!");
			return;
		}

		//correct what differs from SNote...
		//The displayed character is not the same.
		//noteChars = ScoreMetrics.NOTE;
		//The Y offset needs to be updated.
		//int noteY = (int)(getBase().getY()-getOffset(note)*m_metrics.getNoteHeight());
		int noteY = (int)(getBase().getY()-getOffset(note)*glyphDimension.getHeight());
		//apply the new Y offset to the note location
		int noteX = (int)displayPosition.getX();
		displayPosition.setLocation(noteX, noteY);

		BasicStroke stemStroke = m_metrics.getNotesLinkStroke();
		/*if (isStemUp)
			stemX = (int)(noteX + m_metrics.getNoteWidth() - stemStroke.getLineWidth()/10);
		else
			stemX = (int)(noteX);*/
		//int stemYBegin = (int)(noteY - m_metrics.getNoteHeight()/6);
		int stemYBegin = (int)(noteY - glyphDimension.getHeight()/6);

		if (isStemUp()) {
			stemYBegin = (int)(displayPosition.getY() - glyphDimension.getHeight()/6);
			// if stemYEnd hasn't been set give it a default
			if (stemYEnd < 0) stemYEnd = (int)(displayPosition.getY() - m_metrics.getStemLength(NOTATION_CONTEXT));
		} else {
			stemYBegin = (int)(displayPosition.getY() + glyphDimension.getHeight()/6);
			// if stemYEnd hasn't been set give it a default
			if (stemYEnd < 0) stemYEnd = (int)(displayPosition.getY() + m_metrics.getStemLength(NOTATION_CONTEXT));
		}

		/* TODO : change m_metrics.getNoteWidth to use call like: getCharDimensions(NOTATION_CONTEXT) */
		//stemUpBeginPosition = new Point2D.Double(noteX + m_metrics.getNoteWidth() - stemStroke.getLineWidth()/10,
		//	stemYBegin);
		setStemUpBeginPosition(new Point2D.Double(
			noteX + glyphDimension.getWidth() - stemStroke.getLineWidth()/10,
			stemYBegin));
		setStemDownBeginPosition(new Point2D.Double(noteX, stemYBegin));

		//notePosition = new Point2D.Double(displayPosition.getX(), displayPosition.getY()+m_metrics.getNoteHeight()*0.5);
		notePosition = new Point2D.Double(displayPosition.getX(), displayPosition.getY()+glyphDimension.getHeight()*0.5);
		onNotePositionChanged();
		/* TJM */
		// reinit stem position : setStemUp(isStemUp);
	}

	public void setStemYEnd(int value) {
		stemYEnd = value;
	}

	public int getStemYEnd() {
		return stemYEnd;
	}

	/*public Point2D getStemBegin() {
		return new Point2D.Double(stemX, stemYBegin);
	}*/

	public Rectangle2D getBoundingBox() {
		Dimension glyphDimension = m_metrics.getGlyphDimension(NOTATION_CONTEXT);
		if (isStemUp()) {
			return new Rectangle2D.Double(
				(int)(getBase().getX()),
				(int)(stemYEnd),
				m_width,
				getStemBeginPosition().getY()-stemYEnd+glyphDimension.getHeight()/2);
		}
		else {
			return new Rectangle2D.Double(
				(int)(getBase().getX()),
				getStemBeginPosition().getY()-glyphDimension.getHeight()/2,
				m_width,
				stemYEnd-getStemBeginPosition().getY()+1+glyphDimension.getHeight()/2);
		}
	}

	public Point2D getEndOfStemPosition() {
		if(stemYEnd!=-1)
			return new Point2D.Double(getStemBeginPosition().getX(), stemYEnd);
		else
			throw new IllegalStateException();
	}

	public static double getOffset(Note note) {
		double positionOffset = 0;
		byte noteHeight = note.getStrictHeight();
		switch (noteHeight) {
			case Note.C : positionOffset = -1; break;
			case Note.D : positionOffset = -0.5;break;
			case Note.E : positionOffset = 0;break;
			case Note.F : positionOffset = 0.5;break;
			case Note.G : positionOffset = 1;break;
			case Note.A : positionOffset = 1.5;break;
			case Note.B : positionOffset = 2;break;
		}
		positionOffset = positionOffset + note.getOctaveTransposition()*3.5;
		//System.out.println("offset for " + note +"," + note.getOctaveTransposition() + " : " + positionOffset);
		return positionOffset;
	}

	public double render(Graphics2D context){
		super.render(context);
		//context.drawChars(noteChars, 0, 1, (int)displayPosition.getX(), (int)displayPosition.getY());

		//draw stem
		Stroke defaultS = context.getStroke();
		context.setStroke(m_metrics.getStemStroke());
		context.drawLine((int)getStemBeginPosition().getX(), (int)getStemBeginPosition().getY(),
				(int)getStemBeginPosition().getX(), stemYEnd);
		context.setStroke(defaultS);

		/* * /java.awt.Color previousColor = context.getColor();
		context.setColor(java.awt.Color.RED);
		context.drawLine((int)getStemBegin().getX(), (int)getStemBegin().getY(),
				(int)getStemBegin().getX(), (int)getStemBegin().getY());
				//(int)getNotePosition().getX(), (int)getNotePosition().getY());
		context.setColor(java.awt.Color.GREEN);
		Point2D m_base = getBase();
		context.drawLine((int)m_base.getX(), (int)m_base.getY(),
				(int)m_base.getX(), (int)m_base.getY());
		context.setColor(previousColor);/* */

		return m_width;
	}
}
