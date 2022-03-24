package atesting;

import processing.core.PApplet;

public class RotarySlider extends PApplet {

	PApplet p;
	float boxx, boxy; // position of button
	int buttonWidth;
	boolean over;
	boolean press;
	boolean prevPress;
	boolean pressTrigger;
	boolean locked = false;

	int centerX;
	int centerY;
	int radius;
	double theta; // true bearing angle of the slider

	public RotarySlider(PApplet thisApplet, int centerX, int centerY, int radius, int buttonWidth) {
		p = thisApplet;
		this.buttonWidth = buttonWidth;
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		boxx = centerX - radius;
		boxy = centerY;
	}

	public void updateDisplay() { // put in draw()
		update();
		display();
	}

	public void update() {
		if (locked == false) {
			overEvent();
			pressEvent();
		}

		if (press)
			theta = Math.atan2(p.mouseY - centerY, p.mouseX - centerX);
		theta = (theta >= 0 ? theta : theta + 2 * Math.PI);
		boxx = (float) (radius * Math.cos(theta) + centerX);
		boxy = (float) (radius * Math.sin(theta) + centerY);

		prevPress = p.mousePressed; // THIS LINE IS A ONE BIT MEMORY AND ALWAYS GOES LAST
	}

	public void overEvent() {
		if (overRect(boxx, boxy, buttonWidth, buttonWidth)) {
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
		p.strokeWeight(1);
		p.rectMode(CENTER);

		if (over || press)
			p.fill(240);

		p.pushMatrix();
		p.translate(boxx, boxy);
		p.rotate((float) (theta + Math.PI / 4));
		p.rect(0, 0, buttonWidth, buttonWidth);
		p.popMatrix();
	}

	boolean overRect(float x, float y, float w, float h) {
		if (p.mouseX >= x - w / 2 && p.mouseX <= x + w / 2 && p.mouseY >= y - h / 2 && p.mouseY <= y + h / 2) {
			return true;
		} else {
			return false;
		}
	}
}
