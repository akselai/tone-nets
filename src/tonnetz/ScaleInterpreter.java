package tonnetz;

public class ScaleInterpreter {
	public static double centralPitch = 440.0;
	public static byte centralMidiNote = 69;
	public static String desc = "";

	public ScaleInterpreter(double d, byte b) {
		centralPitch = d;
		centralMidiNote = b;
	}
	
	public static double[] interpretFile(String[] file) throws Exception {		
		Interval[] segment = new Interval[] {};
		int k = 0; // non-comment
		for (int i = 0; i < file.length; i++) {
			if (file[i].charAt(0) != '!') {
				if (k == 0)
					desc = file[i];
				if (k == 1)
					segment = new Interval[Integer.valueOf(file[i].trim())];
				if (k > 1)
					segment[k - 2] = interpretString(file[i].trim());
				k++;
			}
		}
		return fillMidiNotes(segment);
	}

	public static double[] interpretFileNoHeader(String[] file) throws Exception {
		Interval[] segment = new Interval[file.length];
		for (int i = 0; i < file.length; i++)
			segment[i] = interpretString(file[i].trim());
		return fillMidiNotes(segment);
	}

	public static double[] fillMidiNotes(Interval[] segment) {
		double[] MidiPitches = new double[128];
		int l = segment.length;
		Interval I = segment[l - 1];
		for (int i = 0; i < 128; i++) {
			int p = Math.floorMod(i - centralMidiNote - 1, l);
			int q = Math.floorDiv(i - centralMidiNote - 1, l);
			MidiPitches[i] = segment[p].stack(I, q).value() * centralPitch;
		}
		return MidiPitches;
	}

	private static Interval interpretString(String s) {
		try {
		String a = "", b = "", x = "";
		x = s.split(" ")[0];
		a = x.split("/")[0];
		if (x.contains("/"))
			b = x.split("/")[1];
		if (x.contains("."))
			return new Interval(Double.valueOf(x));
		return new Interval(Long.valueOf(a), Long.valueOf(b));
		} catch (Exception x) {
			return new Interval(1, 1);
		}
	}
}