package btesting;

import javax.sound.midi.*;
import java.util.List;

public class MidiHandler {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MidiHandler m = new MidiHandler();
	}

	boolean[] keyPressed = new boolean[128];
	int[] channels = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }; // channel 10 is for perc

	public MidiHandler() {
		MidiDevice device;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 5; i < infos.length; i++) {
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				// does the device have any transmitters?
				// if it does, add it to the device list
				System.out.println(infos[i]);

				// get all transmitters
				List<Transmitter> transmitters = device.getTransmitters();
				// and for each transmitter

				for (int j = 0; j < transmitters.size(); j++) {
					// create a new receiver
					transmitters.get(j).setReceiver(
							// using my own MidiInputReceiver
							new MidiInputReceiver(device.getDeviceInfo().toString()));
				}

				Transmitter trans = device.getTransmitter();
				trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));

				// open each device
				device.open();
				// if code gets this far without throwing an exception
				// print a success message
				System.out.println(device.getDeviceInfo() + " Was Opened");

				// break;
			} catch (MidiUnavailableException e) {
				;
			}
		}

	}

	public class MidiInputReceiver implements Receiver {
		public String name;
		public Synthesizer synth;
		public Receiver synthReceiver;

		int[] scaleToMidiNotes;

		public MidiInputReceiver(String name) {
			scaleToMidiNotes = freqToMidiBend(scale);
			this.name = name;
			try {
				this.synth = MidiSystem.getSynthesizer();
				this.synth.open();
				synthReceiver = synth.getReceiver();

				System.out.println(synth.getLatency());
			} catch (MidiUnavailableException mue) {
				System.out.println("mue on input create??");
			}
		}

		ShortMessage toggleCustomBend = new ShortMessage(); // activate custom bend

		public void setBendRate(int channel, int msb, int lsb) throws InvalidMidiDataException {
			toggleCustomBend.setMessage(ShortMessage.CONTROL_CHANGE, channel, 65, 0);
			synthReceiver.send(toggleCustomBend, -1);
			toggleCustomBend.setMessage(ShortMessage.CONTROL_CHANGE, channel, 101, 0);
			synthReceiver.send(toggleCustomBend, -1);
			toggleCustomBend.setMessage(ShortMessage.CONTROL_CHANGE, channel, 100, 0);
			synthReceiver.send(toggleCustomBend, -1);
			toggleCustomBend.setMessage(ShortMessage.CONTROL_CHANGE, channel, 6, msb);
			synthReceiver.send(toggleCustomBend, -1);
			toggleCustomBend.setMessage(ShortMessage.CONTROL_CHANGE, channel, 38, lsb);
			synthReceiver.send(toggleCustomBend, -1);
		}

		public void send(MidiMessage msg, long timeStamp) {
			scaleToMidiNotes = freqToMidiBend(scale);
			long s = System.nanoTime();
			if (synth == null || !synth.isOpen()) {
				System.out.println("no synth!!");
				return;
			}
			if (!(msg instanceof ShortMessage))
				return;

			ShortMessage event = (ShortMessage) msg;
			// int chan = event.getChannel(); // which channel (0 thru 15)
			int comm = event.getCommand();
			int data1 = event.getData1();
			int data2 = event.getData2();
			int i = -1;
			if (comm == ShortMessage.NOTE_ON && data2 > 0) {
				if (data1 == 127)
					System.exit(0);

				keyPressed[data1] = true; // data1 is note when command is note_on or note_off
				i = 0;
				while (channels[i] != -1)
					i++; // keep searching
				channels[i] = data1;
			}

			else if (comm == ShortMessage.NOTE_OFF || (comm == ShortMessage.NOTE_ON && data2 <= 0)) {
				keyPressed[data1] = false;
				i = 0;
				while (channels[i] != data1)
					i++;// keep searching
				channels[i] = -1;
			}

			ShortMessage message = new ShortMessage();
			ShortMessage sendaBenda = new ShortMessage(); // pitch bend value
			try {
				// it'd be awkward if this didn't work...
				message.setMessage(comm, i, scaleToMidiNotes[data1] >> 14, data2);
				sendaBenda.setMessage(ShortMessage.PITCH_BEND, i, scaleToMidiNotes[data1] & 0x7f, (scaleToMidiNotes[data1] & 0x3fff) >> 7);

				synthReceiver.send(message, 100000);
				synthReceiver.send(sendaBenda, 100000);
			} catch (InvalidMidiDataException x) {
				System.out.println("no");
			}

			System.out.println((System.nanoTime() - s) / 1000);
		}

		public int[] freqToMidiBend(double[] x) {
			if (x.length != 128)
				return null; // nope!
			int[] r = new int[128];
			for (int i = 0; i < 128; i++) {
				double m = Math.log(x[i] / 440.0) / Math.log(2) * 12 + 69;
				int g1 = (int) Math.round(m);
				int g2 = (int) ((m - (double) g1) * 4096.0) + 8192;
				r[i] = (g1 << 14) + g2;
			} // format: 7 bit note name + 14 bit pitch bend
			return r;
		}

		public void close() {
		}
	}

	public double[] scale = { 8.020833333333334, 8.59375, 9.0234375, 9.453125, 10.3125, 11.028645833333334, 11.458333333333334, 11.6015625, 12.03125, 13.75, 14.1796875, 15.46875, 16.041666666666668, 17.1875, 18.046875, 18.90625, 20.625, 22.057291666666668, 22.916666666666668, 23.203125, 24.0625, 27.5, 28.359375, 30.9375, 32.083333333333336, 34.375, 36.09375, 37.8125, 41.25, 44.114583333333336, 45.833333333333336, 46.40625, 48.125, 55.0, 56.71875, 61.875, 64.16666666666667, 68.75, 72.1875, 75.625, 82.5, 88.22916666666667, 91.66666666666667, 92.8125, 96.25, 110.0, 113.4375, 123.75, 128.33333333333334, 137.5, 144.375, 151.25, 165.0, 176.45833333333334, 183.33333333333334, 185.625, 192.5, 220.0, 226.875, 247.5, 256.6666666666667, 275.0, 288.75, 302.5, 330.0, 352.9166666666667, 366.6666666666667, 371.25, 385.0, 440.0, 453.75, 495.0, 513.3333333333334, 550.0, 577.5, 605.0, 660.0, 705.8333333333334, 733.3333333333334, 742.5, 770.0, 880.0, 907.5, 990.0, 1026.6666666666667, 1100.0, 1155.0, 1210.0, 1320.0, 1411.6666666666667, 1466.6666666666667, 1485.0, 1540.0, 1760.0, 1815.0, 1980.0, 2053.3333333333335, 2200.0, 2310.0, 2420.0, 2640.0, 2823.3333333333335, 2933.3333333333335, 2970.0, 3080.0,
			3520.0, 3630.0, 3960.0, 4106.666666666667, 4400.0, 4620.0, 4840.0, 5280.0, 5646.666666666667, 5866.666666666667, 5940.0, 6160.0, 7040.0, 7260.0, 7920.0, 8213.333333333334, 8800.0, 9240.0, 9680.0, 10560.0, 11293.333333333334, 11733.333333333334, 11880.0 };
}