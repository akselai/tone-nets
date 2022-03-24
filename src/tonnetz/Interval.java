package tonnetz;

public class Interval {
	boolean isRational;
	long a = 0, b = 0;
	double x = 0;
	final double LOG2 = Math.log(2);

	public Interval(long a_, long b_) {
		isRational = true;
		a = a_;
		b = b_;
	}

	public Interval(double x_) {
		isRational = false;
		x = x_;
	}

	public final static Interval OCTAVE = new Interval(2, 1);

	public Interval reduce() {
		if (this.isRational) {
			// if you pass a rational with zero denom then by convention it's 1/0
			if (this.b == 0) {
				return new Interval(1, 0);
			}
			// if you pass a rational with negative sign then it's not handled
			if (this.a < 0 || this.b < 0) {
				throw new ArithmeticException();
			}
			long t = 1;
			long a_ = a;
			long b_ = b;
			while (this.b != 0) {
				t = this.b;
				this.b = this.a % this.b;
				this.a = t;
			}
			return new Interval(a_ / t, b_ / t);
		}
		return this;
	}

	public double value() {
		if (this.isRational)
			return this.a / (double) this.b;
		return Math.pow(2, this.x / 1200);
	}

	public Interval toCents() {
		if (this.isRational)
			return new Interval(Math.log(this.a / (double) this.b) / LOG2 * 1200);
		else
			return this;
	}

	public Interval stack(Interval I, int n) {
		Interval J = this;
		if (n > 0) {
			if (this.isRational && I.isRational)
				J = new Interval(this.a * (long) Math.pow(I.a, n), this.b * (long) Math.pow(I.b, n)).reduce();
			else
				J = new Interval(this.toCents().x + I.toCents().x * n);
		}
		if (n < 0) {
			if (this.isRational && I.isRational)
				J = new Interval(this.a * (long) Math.pow(I.b, -n), this.b * (long) Math.pow(I.a, -n)).reduce();
			else
				J = new Interval(this.toCents().x + I.toCents().x * n);
		}
		return J;
	}

	public Interval copy() {
		Interval a = new Interval(0);
		a.a = this.a;
		a.b = this.b;
		a.isRational = this.isRational;
		a.x = this.x;
		return a;
	}

	public String toString() {
		if (isRational) {
			return this.a + "/" + this.b;
		} else {
			return String.format("%f", this.x);
		}
	}

	public static Interval[] reduce(Interval[] intervals, Interval modulus) {
		Interval[] res = new Interval[intervals.length];
		for (int i = 0; i < intervals.length; i++) {
			res[i] = new Interval(intervals[i].toCents().x % modulus.toCents().x);
		}
		return res;
	}

	public static Interval[] sort(Interval[] intervals) {
		for (int i = 1; i < intervals.length; i++) {
			Interval p = intervals[i].copy();
			int j = i - 1;
			while (j >= 0 && intervals[j].toCents().x > p.toCents().x) {
				intervals[j + 1] = intervals[j].copy();
				j--;
			}
			intervals[j + 1] = p.copy();
		}
		return intervals;
	}
}
