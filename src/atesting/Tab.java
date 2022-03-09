package atesting;

import processing.core.PApplet;
import processing.core.PImage;

public class Tab extends PApplet {
	int w = 125;
	int h = 25;
	String title = "";
	PImage image = new PImage();
	
	Tab(String s, PImage p) {
		title = s;
		image = p;
	}

	boolean onTop;

	public void draw() {
		noStroke();
		fill((onTop ? 255 : 240));
		rect(0, 0, w, h);
		fill(0);
		text(title, 5, 18);
	}
}