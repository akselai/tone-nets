package atesting;

import java.awt.TextField;

import processing.core.*;
import processing.data.DoubleList;
import tonnetz.Interval;

public class ATesting extends PApplet {
	public static void main(String[] args) {
		PApplet.main("atesting.ATesting");
	};

	public void settings() {
		size(1200, 600);
	}

	IntervalColor ic;
	TextField tf;
	MultilineTextBox textBox = new MultilineTextBox(this, "Your Text", 50, 50, 200, 50);

	public void setup() {
		colorMode(HSB);
		surface.setResizable(true);
		tf = new TextField("", 20);
	}

	public int[] MOSRanges(int a, int b) {
		for (int i = 0; i < a; i++) {
			for (int j = 1; j <= b; j++) {
				if (i / (double) a < j / (double) b && a * j - i * b == 1)
					return new int[] { i, j };
			}
		}
		return new int[] {};
	}

	DoubleList points = new DoubleList(); // represents intervals generated
	RotarySlider s = new RotarySlider(this, 300, 300, 275, 25);
	int scaleSize = 9;
	int rd;

	public void drawMOSRanges() {
		double o = 0, u = 0;
		for (int i = 1; i < scaleSize; i++) {
			int[] ranges = MOSRanges(scaleSize, i);
			if (ranges.length == 2) {
				o = ranges[0] / (double) scaleSize;
				u = ranges[1] / (double) i;
			}

			noFill();
			stroke(0xff24dd00);
			strokeWeight(3);
			arc(width / 2, height / 2, rd - 15, rd - 15, (float) ((2 * o - 1 / 2.0) * Math.PI), (float) ((2 * u - 1 / 2.0) * Math.PI));
			stroke(0xff249900);
			arc(width / 2, height / 2, rd - 15, rd - 15, TWO_PI - ((float) u * TWO_PI) - HALF_PI, TWO_PI - ((float) o * TWO_PI) - HALF_PI);
		}
	}

	public void draw() {
		try {
			scaleSize = Integer.parseInt(textBox.text[0]);
		} catch (Exception x) {

		}

		ic = new IntervalColor(new int[] { 0xffff7918, 0xff2986cc, 0xffffe200, 0xfff44336, 0xffff7918, 0xff2986cc, 0xffffe200, 0xfff44336 }, new Interval[] { new Interval(4, 3), new Interval(5, 4), new Interval(6, 5), new Interval(16, 15), new Interval(3, 2), new Interval(8, 5), new Interval(5, 3), new Interval(15, 8) });
		background(255);

		textBox.update();
		textBox.display();

		rectMode(CENTER);
		noFill();
		stroke(0xff000000);
		strokeWeight(2);
		rd = min(height, width);
		ellipse(width / 2, height / 2, rd - 50, rd - 50);
		points.clear();

		s.centerX = width / 2;
		s.centerY = height / 2;
		s.radius = rd / 2 - 25;

		for (int i = 0; i < scaleSize; i++) {
			points.append(i * (s.theta + Math.PI / 2));
		}

		for (int i = 0; i < scaleSize; i++) {
			for (int j = i + 1; j < scaleSize; j++) {
				double d = Math.pow(2, triangle(Math.abs(points.get(j) - points.get(i))) / Math.PI / 2);
				Interval I = new Interval(1200 * Math.log(d) / Math.log(2));
				ic.getAccuracy_1(I);
				double x = ic.getAccuracy(I);
				int c = ic.getColor(I);
				if (c == 0)
					noStroke();
				else
					stroke(c);
				strokeWeight((float) x * 5);
				double first = points.get(i) - Math.PI / 2;
				double second = points.get(j) - Math.PI / 2;
				line((float) (s.radius * Math.cos(first) + s.centerX), (float) (s.radius * Math.sin(first) + s.centerY), (float) (s.radius * Math.cos(second) + s.centerX), (float) (s.radius * Math.sin(second) + s.centerY));
			}
			fill(200);
			stroke(60);
			strokeWeight(1);

			pushMatrix();
			translate((float) (s.radius * Math.cos(points.get(i) - Math.PI / 2) + s.centerX), (float) (s.radius * Math.sin(points.get(i) - Math.PI / 2) + s.centerY));
			rotate((float) (points.get(i) + Math.PI / 4));
			rect(0, 0, 10, 10);
			popMatrix();

			noFill();
			stroke(0);
			strokeWeight(2);
			if (s.press) {
				double first = i * (s.theta + Math.PI / 2) - Math.PI / 2;
				double second = (i + 1) * (s.theta + Math.PI / 2) - Math.PI / 2;
				line((float) (s.radius * Math.cos(first) + s.centerX), (float) (s.radius * Math.sin(first) + s.centerY), (float) (s.radius * Math.cos(second) + s.centerX), (float) (s.radius * Math.sin(second) + s.centerY));
			}
		}
		s.updateDisplay();
		drawMOSRanges();
	}

	public void mousePressed() {
		textBox.updatePress();
	}

	public void mouseDragged() {
		textBox.updateDrag();
	}

	public void mouseReleased() {
		textBox.updateRelease();

		s.releaseEvent();
	}

	public void keyPressed() {
		textBox.updateKeys();
	}

	private double triangle(double x) {
		return Math.PI - Math.abs(Math.PI - x % (Math.PI * 2));
	}
}