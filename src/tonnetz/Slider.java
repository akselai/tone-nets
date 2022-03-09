package tonnetz;

import processing.core.PApplet;

public class Slider extends PApplet {
	
	/*
	 * slider with button
	 */
	
	PApplet p;
	int x, y; // position of slider
	int boxx, boxy; // position of button
	int stretch;
	int size;
	int w;
	int segment, pos; // how many divisions to the slider
	boolean over;
	boolean press;
	boolean prevPress;
	boolean pressTrigger;
	boolean locked = false;
	
	public Slider(PApplet ip, int ix, int iy, int il, int is, int a, int s) {
		p = ip;
		x = ix;
		y = iy;
		stretch = il;
		size = is;
		w = a;
		segment = s;
		pos = round((lockSlider(p.mouseX - size / 2, x, w + x) - x) / (float) (w / segment));
		boxx = stretch - size / 2;
		boxy = y - size / 2;
	}
	
	public void updateDisplay() { // put in draw()
		update();
		display();
	}

	public void update() {
		boxx = stretch;
		boxy = y - size / 2;
		
		if (locked == false) {
			overEvent();
			pressEvent();
		}

		if (press) {
			pos = round((lockSlider(p.mouseX - size / 2, x, w + x) - x) / (float) (w / segment));
			stretch = pos * (w / segment) + x;
		}
		
		prevPress = p.mousePressed; // THIS LINE IS A ONE BIT MEMORY AND ALWAYS GOES LAST
	}

	public void overEvent() {
		if (overRect(boxx, boxy, size, size)) {
			over = true;
		} else {
			over = false;
		}
	}

	public void pressEvent() {
		pressTrigger = !prevPress && p.mousePressed;
		if (over && pressTrigger || locked) {
			press = true;
			locked = true;
		} else {
			press = false;
		}
	}

	public void releaseEvent() { // put in mouseReleased()
		locked = false;
	}

	public void display() {
		p.fill(255);
		p.stroke(0);
		p.rect(x - 3, y - size / 2 + 3, w + size + 6, size - 6, 3);
		p.rect(boxx, boxy, size, size);
		if (over || press) {
			p.fill(240);
			p.rect(boxx, boxy, size, size);
		}
	}

	int lockSlider(int val, int minv, int maxv) {
		return min(max(val, minv), maxv);
	}
	
	boolean overRect(int x, int y, int w, int h) {
	    if (p.mouseX >= x && p.mouseX <= x+w && 
	        p.mouseY >= y && p.mouseY <= y+h) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	
}