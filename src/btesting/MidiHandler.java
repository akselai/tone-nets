package btesting;

import javax.sound.midi.*;
import java.util.List;

public class MidiHandler {
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MidiHandler m = new MidiHandler();
	}

	boolean[] keyPressed = new boolean[128];
	int[] channels = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}; // channel 10 is for perc

	public MidiHandler() {
		MidiDevice device;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
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

			} catch (MidiUnavailableException e) {
				;
			}
		}

	}

//tried to write my own class. I thought the send method handles an MidiEvents sent to it
	public class MidiInputReceiver implements Receiver {
		public String name;

		public MidiInputReceiver(String name) {
			this.name = name;
		}

		public void send(MidiMessage msg, long timeStamp) {
			if (msg.getMessage()[1] == 127)
				System.exit(0);
			if (msg.getMessage()[0] == -111) { // distribute the notes
				keyPressed[msg.getMessage()[1]] = true;
				int i = 0;
				while (channels[i] != -1)
					i++; // keep searching
				channels[i] = msg.getMessage()[1];
			} else if (msg.getMessage()[0] == -127) {
				keyPressed[msg.getMessage()[1]] = false;
				int i = 0;
				while (channels[i] != msg.getMessage()[1])
					i++;// keep searching
				channels[i] = -1;
			}
			// System.out.println(keyPressed[msg.getMessage()[1]]);
			System.out.println(channels[0] + " " + channels[1] + " " + channels[2]);
		}

		public void close() {
		}
	}
}