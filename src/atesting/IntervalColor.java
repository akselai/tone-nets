package atesting;

import processing.core.PApplet;
import tonnetz.Interval;

public class IntervalColor extends PApplet {
	int[] c;
	Interval[] I;
	double[] res;
	double kernelWidth = 0.0003;
	int colorBlendMode = 0;

	public final int PURE_COLORS = 0;
	public final int BLEND_COLORS = 1;

	public IntervalColor(int[] colors, Interval[] intervals) {
		c = colors;
		I = intervals;
	}

	public double kernel(double x, double d) {
		return Math.exp(-x * x / d); // gaussian
	} // or you can try other kernel

	public void getAccuracy_1(Interval J) { // get accuracy over all intervals
		this.res = new double[min(this.c.length, this.I.length)];
		for (int i = 0; i < res.length; i++)
			res[i] = kernel(J.value() - this.I[i].value(), kernelWidth);
	}

	// below is for HSB color blending
	// HSB is 0 to 1, RGB is 0 to 255;

	public static float h(int color) {
		float r = ((color & 0x00ff0000) >> 16) / 255f;
		float g = ((color & 0x0000ff00) >> 8) / 255f;
		float b = (color & 0x000000ff) / 255f;
		float m = max(r, g, b);
		float d = m - min(r, g, b);
		if (r == g && g == b) {
			return 0;
		}
		if (r >= g && r >= b) {
			float t = (g - b) / d;
			return (t < 0 ? t + 6 : t) / 6f;
		}
		if (g >= r && g >= b) {
			return ((b - r) / d + 2) / 6f;
		}
		return ((r - g) / d + 4) / 6f;
	}

	public static float s(int color) {
		float r = ((color & 0x00ff0000) >> 16) / 255f;
		float g = ((color & 0x0000ff00) >> 8) / 255f;
		float b = (color & 0x000000ff) / 255f;
		float m = max(r, g, b);
		return (m == 0 ? 0 : (m - min(r, g, b)) / m);
	}

	public static float b(int color) {
		float r = ((color & 0x00ff0000) >> 16) / 255f;
		float g = ((color & 0x0000ff00) >> 8) / 255f;
		float b = (color & 0x000000ff) / 255f;
		return max(r, g, b);
	}

	public static int a(int color) {
		int b = color >> 24;
		return (b >= 0 ? b : b + 256);
	}

	public static int colorRGB(float h, float s, float b, int a) {
		float c_ = s * b;
		float x = c_ * (1 - abs(((h * 6) % 2) - 1));
		float m = b - c_;

		if (0 <= h && h < 1f / 6)
			return rgb((int) ((c_ + m) * 255), (int) ((x + m) * 255), (int) (m * 255), a);
		if (1f / 6 <= h && h < 2f / 6)
			return rgb((int) ((x + m) * 255), (int) ((c_ + m) * 255), (int) (m * 255), a);
		if (2f / 6 <= h && h < 3f / 6)
			return rgb((int) (m * 255), (int) ((c_ + m) * 255), (int) ((x + m) * 255), a);
		if (3f / 6 <= h && h < 4f / 6)
			return rgb((int) (m * 255), (int) ((x + m) * 255), (int) ((c_ + m) * 255), a);
		if (4f / 6 <= h && h < 5f / 6)
			return rgb((int) ((x + m) * 255), (int) (m * 255), (int) ((c_ + m) * 255), a);
		return rgb((int) ((c_ + m) * 255), (int) (m * 255), (int) ((x + m) * 255), a);
	}

	public static int rgb(int r, int g, int b, int a) {
		return (a << 24) + (r << 16) + (g << 8) + b;
	}

	public int blendColor(int from, int to, float x) {
		x = constrain(x, 0, 1);
		float u = h(from);
		float v = h(to);
		int r = colorRGB(u * (1 - x) + v * x, s(from) * (1 - x) + s(to) * x, b(from) * (1 - x) + b(to) * x, (int) (a(from) * (1 - x) + a(to) * x));
		return r;
	}

	public int alphaColor(int color, float x) { // x = 0 => transparent, x = 1 => filled in
		return rgb(color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff, (int) (x * 255));
	}

	// end of HSB color blending

	public int getColor(Interval I) { // draw color
		int c_ = 0x00000000;
		switch (colorBlendMode) {
		case PURE_COLORS:
			int s = 0;
			for (int i = 0; i < this.res.length; i++) {
				if (this.res[s] <= this.res[i]) {
					c_ = this.c[i];
					s = i;
				}
			}
			c_ = alphaColor(c_, (float) this.res[s]);
			break;
		case BLEND_COLORS:
			for (int i = 0; i < this.res.length; i++)
				c_ = blendColor(c_, this.c[i], (float) this.res[i]);
			break;
		default:
			break;
		}
		return c_;
	}

	public double getAccuracy(Interval I) {
		double a = 0;
		for (int i = 0; i < this.res.length; i++)
			a += Math.exp(this.res[i] * 10);
		return Math.log(a) / 10;
	}
}