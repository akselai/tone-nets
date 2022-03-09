package atesting;

import processing.core.PApplet;
import processing.core.PFont;

public class PWindow extends PApplet {
	int x, y, w, h;
	boolean setLocation, setTitle, makeResizable;
	String title;
	PFont f;

	PWindow() {
		super();
		PApplet.runSketch(new String[] { this.getClass().getSimpleName() }, this);
	};

	PWindow(int x_, int y_) {
		super();
		x = x_;
		y = y_;
		setLocation = true;
		PApplet.runSketch(new String[] { this.getClass().getSimpleName() }, this);
	};

	PWindow(int x_, int y_, int ww, int hh) {
		super();
		x = x_;
		y = y_;
		w = ww;
		h = hh;

		setLocation = true;
		PApplet.runSketch(new String[] { this.getClass().getSimpleName() }, this);
	}

	PWindow(int x_, int y_, int ww, int hh, String s) {
		super();
		x = x_;
		y = y_;
		w = ww;
		h = hh;
		setLocation = true;
		title = s;
		setTitle = true;
		PApplet.runSketch(new String[] { this.getClass().getSimpleName() }, this);
	}

	PWindow(int x_, int y_, String s, boolean k) {
		super();
		x = x_;
		y = y_;
		setLocation = true;
		title = s;
		setTitle = true;
		makeResizable = true;
		PApplet.runSketch(new String[] { this.getClass().getSimpleName() }, this);
	}

	// these stuff below are overridable.

	public void settings() {
		if (w > 0 && h > 0)
			size(w, h);
		else
			size(500, 200);
	}

	public void setup() {
		if (setLocation)
			surface.setLocation(x, y);
		if (setTitle)
			surface.setTitle(title);
		if (makeResizable)
			surface.setResizable(true);
		f = createFont("ui.ttf", 12);
		textFont(f);
	}

	public void draw() {
	}

	public void mousePressed() {

	}

	public void exit() {
		dispose();
	}
}
