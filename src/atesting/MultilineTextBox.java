package atesting;

import java.awt.event.KeyEvent;

import processing.core.*;

public class MultilineTextBox extends PApplet {
	/*
	 * WIP textbox
	 */
	PApplet p;

	PFont font;
	int fontSize = 20;
	String prompt;
	String[] text;
	int xpos;
	int ypos;

	boolean alreadyPressed;
	boolean selecting;
	int xpos2;
	int ypos2;

	PVector loc;

	float WIDTH;
	float HEIGHT;

	boolean hasFocus;
	boolean hasInputFocus;

	// boolean drawBox;
	
	// int lineLimit;
	int lastPress = -500;

	MultilineTextBox(PApplet thisApplet, String prompt, float x, float y, float w, float h) {
		p = thisApplet;

		WIDTH = w;
		HEIGHT = h;

		this.prompt = prompt;
		text = new String[1];
		text[0] = "";
		ypos = 0;
		xpos = 0;

		loc = new PVector(x, y);
	}

	void update() {
	}

	void updatePress() {
		if (p.mousePressed) {
			if (p.mouseX > loc.x && p.mouseX < loc.x + WIDTH && p.mouseY > loc.y && p.mouseY < loc.y + HEIGHT) {
				hasFocus = true;
				hasInputFocus = true;

				testPos();

				alreadyPressed = true;
			} else {
				hasFocus = (text.length <= 1 && text[0].length() <= 0 ? false : true);
				hasInputFocus = false;
				if (!alreadyPressed)
					selecting = false;
			}
		}
	}

	void updateDrag() {
		if (p.mouseX > loc.x && p.mouseX < loc.x + WIDTH && p.mouseY > loc.y && p.mouseY < loc.y + HEIGHT)
			testPos();
		else if (selecting)
			testPos();
	}

	void updateRelease() {
		alreadyPressed = false;
	}

	void updateKeys() {
		if (p.keyPressed) {
			if (p.key == CODED) {
				if (p.millis() - lastPress > 0) {
					if (p.keyCode == LEFT) {
						if (xpos <= 0 && ypos > 0) {
							ypos--;
							xpos = text[ypos].length();
						} else
							xpos = constrain(xpos - 1, 0, text[ypos].length());
					}
					if (p.keyCode == RIGHT) {
						if (xpos >= text[ypos].length() && ypos < text.length - 1) {
							ypos++;
							xpos = 0;
						} else
							xpos = constrain(xpos + 1, 0, text[ypos].length());
					}
					if (p.keyCode == UP && ypos > 0) {
						ypos--;
						xpos = constrain(xpos, 0, text[ypos].length());
					}
					if (p.keyCode == DOWN && ypos < text.length - 1) {
						ypos++;
						xpos = constrain(xpos, 0, text[ypos].length());
					}
					if (p.keyCode == KeyEvent.VK_HOME)
						xpos = 0;
					if (p.keyCode == KeyEvent.VK_END)
						xpos = text[ypos].length();

					if (!(p.keyCode == SHIFT)) {
						ypos2 = ypos;
						xpos2 = xpos;
					}

					lastPress = p.millis();
				}
			} else {
				switch (p.key) {
				case ESC:
				case TAB:
					break;
				case ENTER:
				case RETURN:
					newline();
					break;
				case BACKSPACE:
				case DELETE:
					if (selecting && (ypos != ypos2 || xpos != xpos2)) {
						int minypos = min(ypos, ypos2);
						int maxypos = max(ypos, ypos2);

						int minxpos;
						int maxxpos;

						if (minypos == maxypos) {
							minxpos = min(xpos, xpos2);
							maxxpos = max(xpos, xpos2);
						} else {
							minxpos = (minypos == ypos ? xpos : xpos2);
							maxxpos = (maxypos == ypos ? xpos : xpos2);
						}

						if (minypos == maxypos)
							text[ypos] = text[ypos].substring(0, minxpos) + text[ypos].substring(maxxpos, text[ypos].length());
						else {
							String combine = text[minypos].substring(0, minxpos) + text[maxypos].substring(maxxpos, text[maxypos].length());
							String[] pre = append(subset(text, 0, minypos), combine);
							text = concat(pre, subset(text, maxypos + 1, text.length - (maxypos + 1)));
						}

						ypos = minypos;
						xpos = minxpos;

						ypos2 = ypos;
						xpos2 = xpos;
						selecting = false;

						lastPress = p.millis();
					} else {
						if (p.millis() - lastPress > 0 && xpos > 0) {
							text[ypos] = text[ypos].substring(0, xpos - 1) + text[ypos].substring(xpos, text[ypos].length());

							xpos--;
							xpos2 = xpos;

							lastPress = p.millis();
						} else if (ypos > 0 && xpos == 0) {
							int over = text[ypos - 1].length();
							String combine = text[ypos - 1] + text[ypos];

							String[] pre = append(subset(text, 0, ypos - 1), combine);
							text = concat(pre, subset(text, ypos + 1, text.length - (ypos + 1)));

							ypos--;
							xpos = over;

							ypos2 = ypos;
							xpos2 = xpos;
						}
					}
					break;
				default:
					if (selecting && (ypos != ypos2 || xpos != xpos2)) {
						int minypos = min(ypos, ypos2);
						int maxypos = max(ypos, ypos2);

						int minxpos;
						int maxxpos;

						if (minypos == maxypos) {
							minxpos = min(xpos, xpos2);
							maxxpos = max(xpos, xpos2);
						} else {
							minxpos = (minypos == ypos ? xpos : xpos2);
							maxxpos = (maxypos == ypos ? xpos : xpos2);
						}

						if (p.millis() - lastPress > 0) {

							if (minypos == maxypos)
								text[ypos] = text[ypos].substring(0, minxpos) + p.key + text[ypos].substring(maxxpos, text[ypos].length());
							else {
								String combine = text[minypos].substring(0, minxpos) + p.key + text[maxypos].substring(maxxpos, text[maxypos].length());
								String[] pre = append(subset(text, 0, minypos), combine);
								text = concat(pre, subset(text, maxypos + 1, text.length - (maxypos + 1)));
							}

							ypos = minypos;
							xpos = minxpos + 1;

							ypos2 = ypos;
							xpos2 = xpos;
							selecting = false;

							lastPress = p.millis();
						}
					} else {
						if (p.millis() - lastPress > 0 && p.textWidth(text[ypos].substring(0, xpos) + p.key + text[ypos].substring(xpos, text[ypos].length())) < WIDTH - 8) {
							text[ypos] = text[ypos].substring(0, xpos) + p.key + text[ypos].substring(xpos, text[ypos].length());

							xpos++;
							xpos2 = xpos;

							lastPress = p.millis();
						}
					}
					break;
				}
			}
		}
	}

	void newline() {
		String after = text[ypos].substring(xpos, text[ypos].length());
		text[ypos] = text[ypos].substring(0, xpos);
		text = splice(text, after, ypos + 1);

		ypos++;
		xpos = 0;

		ypos2 = ypos;
		xpos2 = xpos;
	}

	void testPos() {
		if (alreadyPressed) {
			selecting = true;

			ypos2 = (int) (constrain((float) (p.mouseY - loc.y) / fontSize, 0, text.length - 1));

			for (int i = 0; i < text[ypos2].length(); i++) {
				if (p.mouseX - loc.x - 4 <= p.textWidth(text[ypos2].substring(0, i)) + p.textWidth(text[ypos2].charAt(i)) / 2) {
					xpos2 = i;
					return;
				}
			}

			xpos2 = text[ypos].length();
		} else {
			selecting = false;

			ypos = (int) (constrain((float) (p.mouseY - loc.y) / fontSize, 0, text.length - 1));
			for (int i = 0; i < text[ypos].length(); i++) {
				if (p.mouseX - loc.x - 4 <= p.textWidth(text[ypos].substring(0, i)) + p.textWidth(text[ypos].charAt(i)) / 2) {
					xpos = i;
					return;
				}
			}

			xpos = text[ypos].length();
		}
	}

	void display() {
		p.colorMode(RGB);
		
		p.fill(255);
		p.strokeWeight(2);
		p.rectMode(CORNER);

		if (hasInputFocus)
			p.stroke(220, 150, 90);
		else 
			p.stroke(0);
		p.rect(loc.x, loc.y, WIDTH, HEIGHT, 6);

		p.stroke(0);
		font = p.createFont("audiowide.ttf", fontSize);
		p.textFont(font);

		p.textSize(fontSize);
		p.textAlign(LEFT, TOP);
		if (hasFocus)
			p.fill(0);
		else
			p.fill(102);

		if (hasFocus) {
			for (int i = 0; i < text.length; i++)
				p.text(text[i], (int) (loc.x + 4), (int) (loc.y + 5 + i * fontSize));
		} else
			p.text(prompt, (int) (loc.x + 4), (int) (loc.y + 5));

		ypos = constrain(ypos, 0, text.length);
		ypos2 = constrain(ypos2, 0, text.length);

		xpos = constrain(xpos, 0, text[ypos].length());
		xpos2 = constrain(xpos2, 0, text[ypos2].length());

		if (selecting && (xpos != xpos2 || ypos != ypos2)) {
			p.fill(162, 234, 255, 102);
			p.noStroke();

			int minypos = min(ypos, ypos2);
			int maxypos = max(ypos, ypos2);

			int minxpos;
			int maxxpos;

			if (minypos == maxypos) {
				minxpos = min(xpos, xpos2);
				maxxpos = max(xpos, xpos2);
			} else {
				minxpos = (minypos == ypos ? xpos : xpos2);
				maxxpos = (maxypos == ypos ? xpos : xpos2);
			}

			if (minypos == maxypos)
				p.rect(loc.x + 4 + p.textWidth(text[minypos].substring(0, minxpos)), loc.y + 4 + minypos * fontSize, p.textWidth(text[maxypos].substring(0, maxxpos)) - p.textWidth(text[maxypos].substring(0, minxpos)), fontSize);
			else {
				for (int y = minypos; y <= maxypos; y++) {
					for (int x = 0; x < text[y].length(); x++) {
						if ((y == minypos ? x >= minxpos : true) && (y == maxypos ? x < maxxpos : true))
							p.rect(loc.x + 4 + p.textWidth(text[y].substring(0, x)), loc.y + 4 + y * fontSize, p.textWidth(text[y].charAt(x)), fontSize);
					}
					if (text[y].length() <= 0)
						p.rect(loc.x + 4, loc.y + 4 + y * fontSize, p.textWidth(" ") / 2, fontSize);
				}
			}
		} else if (hasInputFocus && p.millis() / 300 % 2 == 1)
			p.line(loc.x + 4 + p.textWidth(text[ypos].substring(0, xpos)), loc.y + 8 + ypos * fontSize, (loc.x + 4 + p.textWidth(text[ypos].substring(0, xpos))), loc.y + 8 + fontSize + ypos * fontSize);
	}

	String[] getText() {
		return text;
	}

	void setText(String toSet) {
		String[] output = new String[0];
		int last = -1;
		for (int i = 0; i < toSet.length(); i++) {
			if (toSet.charAt(i) == '\n') {
				output = append(output, toSet.substring(last + 1, i));
				last = i;
			}
		}

		output = append(output, toSet.substring(last + 1, toSet.length()));

		text = output;
	}

	String consolidate() {
		String toReturn = "";
		for (int i = 0; i < text.length; i++) {
			toReturn += text[i];
			if (i < text.length - 1)
				toReturn += "\n";
		}

		return toReturn;
	}
}
