package tonnetz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.midi.*;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.data.IntList;

// TODO map the scale degrees to MIDI notes (which midi note is 1/1?) complete!
//      UI design (ok maybe later) (esc to go to menu)
//      mouse actions bc keyboard succs
//      finally implement the midi
//      and also the synth and then publish as v0_beta

public class Tonnetz extends PApplet {

	/*
	 * as the name suggests, this is the core of the project.
	 */

	PFont l_font, u_font; // lattice font, ui font

	File midiFile = null;

	Lattice lat = new Lattice();

	IntList keysPressed = new IntList(); // what keys are pressed

	ArrayList<int[]> scale = new ArrayList<int[]>(); // the scale of vectors in the canonical basis (integer exps of
														// primes)
	String[] scalaFile = { "! pentatonic.scl", "!", " ", " 5 ", "!", " 193.15686", " 5/4 pure", "696.57843",
			" 889.73529", "2/1" };
	public static double[] MIDIPitches = new double[128];

	// Slider sz = new Slider(this, 50, 700, 50, 25, 500, 100);

	public static void main(String[] args) {
		PApplet.main("tonnetz.Tonnetz");
	}

	public void settings() {
		size(800, 800);
	}

	public void setup() {
		try {
			ScaleInterpreter.interpretFile(scalaFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		surface.setResizable(true);

		int[][] z = { { 0, 0 }, { -1, -1 }, { 2, 0 }, { 1, -1 }, { 0, 1 }, { -1, 0 }, { 2, 1 }, { 1, 0 }, { 0, -1 },
				{ -1, 1 }, { 2, -1 }, { 1, 1 } };
		for (int i = 0; i < 12; i++) {
			scale.add(z[i]);
		}

		for (int i = -1; i <= 2; i++) {
			for (int j = -1; j <= 1; j++) {
				int[] o = { i, j };
				lat.rungs.add(o);
			}
		}
		l_font = createFont("smooth.ttf", 18);
		textFont(l_font);
		textAlign(CENTER, CENTER);
	}

	public void draw() {
		// lat.rungs.clear();
		// for (int i = 0; i < 25; i++) {
		// lat.rungs.add(new int[] {(int) random(-6, 6), (int) random(-6, 6)});
		// }
		lat.drawLattice();
		lat.circleKeyPressNotes();
		// circleMIDINotes();
		// sz.update();
		// sz.display();
	}

	public void mousePressed() {
		switch (mouseButton) {
		case LEFT:
			// do something
			return;
		case RIGHT:
			selectInput("Which MIDI file?", "fileSelected");
			return;
		}
	}

	public void keyPressed() {
		if (keysPressed.size() == 0 || keysPressed.get(keysPressed.size() - 1) != key) {
			// ^ workaround of repeating key press problem
			int[] a = lat.keyboardAction();
			if (a != null) {
				// playTone((int) a[0], (int) a[1], ShortMessage.NOTE_ON);
			}
			if (!keysPressed.hasValue(key)) {
				keysPressed.append(key);
				lat.rungsPressed.add(a);
			}
		}
	}

	public void keyReleased() {
		int[] a = lat.keyboardAction();
		keysPressed.removeValue(key);
		lat.rungsPressed.remove(a);
		if (a != null) {
			// playTone(a[0], a[1], ShortMessage.NOTE_OFF);
		}
		// println(rungsPressed);
	}

	class Calc { // ragtag calculations
		final int[] primes = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83,
				87, 89, 97 };

		float vectorPitch(int[] a) {
			float x = 1f;
			for (int i = 0; i < a.length; i++) {
				x *= Math.pow(primes[i], a[i]);
			}
			return x;
		}
	}

	static class NoteName {
		static String name(int a, int b) {
			String[] chain = { "F", "C", "G", "D", "A", "E", "B" };
			int c = a + b * 4 + 1;
			int t = (c % 7 + 7) % 7;
			String ac = accidental((c - t) / 7);
			return chain[t] + ac;
		}

		static String accidental(int a) {
			switch (a) {
			case 1:
				return "\u0080";
			case 2:
				return "\u0082";
			case 3:
				return "\u0080\u0082";
			case 4:
				return "\u0082\u0082";
			case -1:
				return "\u0081";
			case -2:
				return "\u0083";
			case -3:
				return "\u0081\u0083";
			case -4:
				return "\u0083\u0083";
			default:
				return "";
			}
		}
	}

	static class ScaleInterpreter {
		public double centralPitch = 440.0;
		public static byte centralMIDINote = 60;
		public static String desc = "";
		public static Interval[] scaleSegment;

		public static void interpretFile(String[] file) throws Exception {
			int k = 0; // non-comment
			for (int i = 0; i < file.length; i++) {
				if (file[i].charAt(0) != '!') {
					if (k == 0)
						desc = file[i];
					if (k == 1)
						scaleSegment = new Interval[Integer.valueOf(file[i].trim())];
					if (k > 1)
						scaleSegment[k - 2] = interpretString(file[i].trim());
					k++;
				}
			}
			int l = scaleSegment.length;
			Interval I = scaleSegment[l - 1];
			for (int i = 0; i < 128; i++) {
				int p = Math.floorMod(i - centralMIDINote - 1, l);
				int q = Math.floorDiv(i - centralMIDINote - 1, l);
				MIDIPitches[i] = scaleSegment[p].stack(I, q).value();
			}
		}

		private static Interval interpretString(String s) throws Exception {
			String a = "", b = "", x = "";
			x = s.split(" ")[0];
			a = x.split("/")[0];
			if (x.contains("/"))
				b = x.split("/")[1];
			if (x.contains("."))
				return new Interval(Double.valueOf(x));
			return new Interval(Long.valueOf(a), Long.valueOf(b));
		}
	}

	class Lattice {
		Lattice() {
			basisVectors.add(new PVector(1, 0));
			basisVectors.add(new PVector(1 / 2f, -sqrt(3 / 4f)));
		}

		int d = 7; // nodal radius of the tonnetz
		int rad = 20; // radius of the note names;

		int lineWidth = 100;
		PVector shift = new PVector(400, 400); // shift to the center

		ArrayList<int[]> rungs = new ArrayList<int[]>();
		ArrayList<int[]> rungsPressed = new ArrayList<int[]>(); // where keys are pressed

		ArrayList<PVector> basisVectors = new ArrayList<PVector>(); // projection of basis vectors onto 2d or 3d

		boolean lcr(int i, int j) { // i really don't know why i named it that
			int[] o = { i, j };
			for (int k = 0; k < rungs.size(); k++) {
				if (Arrays.equals(rungs.get(k), o)) {
					return true;
				}
			}
			return false;
		}

		void drawLattice() {
			background(255);
			for (int i = -d; i < d; i++) {
				for (int j = -d; j < d; j++) {
					String n = NoteName.name(i, j);

					PVector c = intervalVector(new int[] { i, j });

					stroke(0, 0, 0, 100);
					strokeWeight(2);
					PVector c0 = intervalVector(new int[] { i + 1, j + 0 });
					PVector c1 = intervalVector(new int[] { i + 0, j + 1 });

					boolean ij = lcr(i, j);
					boolean ij0 = lcr(i + 1, j + 0);
					boolean ij1 = lcr(i + 0, j + 1);

					stroke(0, 0, 0, (ij && ij0 ? 100 : 45));
					line(c.x, c.y, c0.x, c0.y);
					stroke(0, 0, 0, (ij && ij1 ? 100 : 45));
					line(c.x, c.y, c1.x, c1.y);
					stroke(0, 0, 0, (ij0 && ij1 ? 100 : 45));
					line(c0.x, c0.y, c1.x, c1.y);

					noStroke();
					fill(255);
					ellipse(c.x, c.y, 2 * rad, 2 * rad); // lines intersecting letters

					if (ij)
						fill(0, 0, 0, 255);
					else
						fill(0, 0, 0, 45);

					text(n, c.x, c.y);
					fill(0, 0, 0, 255);
				}
			}
		}

		PVector intervalVector(int[] v) { // calculates the on-screen position vector
			shift = new PVector(width / 2, height / 2);
			PVector s0 = basisVectors.get(0).copy();
			s0.mult(v[0] * lineWidth);
			PVector s1 = basisVectors.get(1).copy();
			s1.mult(v[1] * lineWidth);
			s0.add(s1).add(shift);
			return s0;
		}

		public void circleKeyPressNotes() {
			strokeWeight(3);
			stroke(0);
			noFill();
			for (int i = 0; i < rungsPressed.size(); i++) {
				if (rungsPressed.get(i) != null) {
					PVector r = intervalVector(rungsPressed.get(i));
					ellipse(r.x, r.y, 30, 30);
				}
			}
		}

		public int[] keyboardAction() {
			switch (key) {
			case 'a':
				return scale.get(0);
			case 'w':
				return scale.get(1);
			case 's':
				return scale.get(2);
			case 'e':
				return scale.get(3);
			case 'd':
				return scale.get(4);
			case 'f':
				return scale.get(5);
			case 't':
				return scale.get(6);
			case 'g':
				return scale.get(7);
			case 'y':
				return scale.get(8);
			case 'h':
				return scale.get(9);
			case 'u':
				return scale.get(10);
			case 'j':
				return scale.get(11);
			default:
				return null;
			}
		}

	}

	class ScreenKeyboard { // WIP
		void drawScreenKeyboard() {
			fill(255);
			// some type of keyboard like halberstadt or smth idk
		}
	}

	class ActuallyPlayTheMIDILikeIDKAndOtherJunkThatIveSpentSoManyHoursOn {
		ArrayList<int[]> midiNoteOn = new ArrayList<int[]>(); // what midi keys are on (channel, note)

		float cmod(float a, float b) {
			return a - round(a / b) * b;
		}

		float equi(float a) {
			while (a > 2) {
				a /= 2;
			}
			while (a < 1) {
				a *= 2;
			}
			return a;
		}

		/*
		 * public void circleMIDINotes() { strokeWeight(3); noFill(); for (int i = 0; i
		 * < midiNoteOn.size(); i++) { int[] n = scale.get((int) (midiNoteOn.get(i)[1] %
		 * scale.size())); PVector r = intervalVector(n); ellipse(r.x, r.y, 30, 30); }
		 * strokeWeight(1); }
		 */

		public void fileSelected(File selection) {
			if (selection == null) {
				println("Window was closed or the user hit cancel.");
			} else {
				midiFile = selection;
				readMIDI();
			}
		}

		private Sequencer seq;
		private Receiver note_player;
		private Receiver MIDI_player = new Receiver() {
			@Override
			// this function gets called on its own every time the midi playing in "seq"
			// finds an event
			public void send(MidiMessage msg, long timeStamp) {
				if (msg instanceof ShortMessage) {
					ShortMessage event = (ShortMessage) msg; // convert
					int channel = event.getChannel();
					int command = event.getCommand();
					int note = event.getData1();
					int vel = event.getData2();

					int[] p = { channel, note };
					if (command == ShortMessage.NOTE_ON && vel > 0) {
						midiNoteOn.add(p);
					} else if (command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && vel <= 0)) {
						// find note
						int i = 0;
						try {
							while (midiNoteOn.get(i)[0] != channel || midiNoteOn.get(i)[1] != note) {
								i++;
							}
							midiNoteOn.remove(i);
						} catch (Exception x) {
							println("cannot remove note!");
						}
					}
				}
			}

			@Override
			public void close() {
			}
		};

		void playTone(int a, int b, int command) { // octave equivalence
			float freq = pow((float) 1.5, a) * pow((float) 1.25, b);
			freq = equi(freq);
			float midiNoteWithBend = log(freq) / log(2) * 12;
			int note = round(midiNoteWithBend) + 60;
			// int d = (int) (cmod(midiNoteWithBend, 1f) * 4096 + 8192);
			ShortMessage message = new ShortMessage();
			// ShortMessage bend = new ShortMessage();
			// println(d % 128);
			try {
				message.setMessage(command, 0, note, 120);
				// bend.setMessage(ShortMessage.PITCH_BEND, 0, d % 128, d >> 7);
			} catch (InvalidMidiDataException imde) {
				println("Not a correct event was sent.");
			}
			note_player.send(message, -1);
			// note_player.send(bend, -1);
		}

		void readMIDI() {
			try {
				seq.stop();
				seq.close();
				seq = null;
			} catch (Exception e) {
				println("Seq already stopped.");
			}

			try {
				seq = MidiSystem.getSequencer(true);
				seq.open();
				seq.setLoopCount(-1);

				Transmitter transmitter = seq.getTransmitter();
				transmitter.setReceiver(MIDI_player);

				Sequence mid = MidiSystem.getSequence(midiFile);
				seq.setSequence(mid);
				seq.start();
			}

			catch (MidiUnavailableException e) { // bro where did your pc go
				println("MIDI device unavailable!");
			} catch (InvalidMidiDataException e) { // you've done fked up in your midi
				println("Invalid MIDI data!");
			} catch (IOException e) { // huh
				println("I/O Error!");
			} catch (Exception e) { // wow you fked up so bad that we had to quit
				println("Fatal error!");
				e.printStackTrace();
				exit();
			}
		}

	}
}