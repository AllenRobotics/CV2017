package org.usfirst.frc.team5417.cv2017.customops;

import org.usfirst.frc.team5417.cv2017.MatrixUtilities;

//
// A pixel, either rgb or grayscale. If grayscale, just use any of the r/g/b member variables.
//
// NOTE: Pixels are mutable. That means they should not be used
//       with hash-based data structure. They have been left mutable
//       for speed. It can be expensive to create a lot of tiny objects
//       in memory.
//
public class Pixel {

	public Point location;
	
	public int r;
	public int g;
	public int b;
	
	private boolean isGray = false;

	public Pixel(int x, int y, int r, int g, int b) {
		this.location = new Point(x, y);
		put(r, g, b);
	}

	public Pixel(int x, int y, int gray) {
		this.location = new Point(x, y);
		putGray(gray);
	}

	public Pixel(int x, int y, double[] channels) {
		this.location = new Point(x, y);
		put(channels);
	}
	
	public Pixel(int x, int y, Pixel other) {
		this.location = new Point(x, y);
		put(other);
	}

	public int gray() {
		if (isGray) {
			return r;
		}
		else {
			//
			// see https://en.wikipedia.org/wiki/Grayscale for grayscale conversion formula
			//
			return (int)(0.299 * r + 0.587 * g + 0.114 * b);
		}
	}
	
	public void put(int r, int g, int b) {
//		if (r < 0 || g < 0 || b < 0) {
//			System.out.println("Oh no!");
//		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.isGray = false;
	}

	public void put(double[] channels) {
		if (channels.length == 3) {
			put((int)channels[0], (int)channels[1], (int)channels[2]);
		}
		else {
			putGray((int)channels[0]);
		}
	}

	public void put(double gray) {
		putGray((int)gray);
	}
	
	public void put(Pixel other) {
		put(other.r, other.g, other.b);
		this.isGray = other.isGray;
	}

	// please leave this private
	private void putGray(int gray) {
//		if (r < 0 || g < 0 || b < 0) {
//			System.out.println("Oh no!");
//		}
		this.r = gray;
		this.g = gray;
		this.b = gray;
		this.isGray = true;
	}
	
	// these functions:
	//   public int hashCode()
	//   public boolean equals(Object other)
	// are so that we can use a Pixel as a key in HashSet or HashMap
	
	@Override
	public int hashCode() {
		return (int)
				(this.r ^ 3
				+ this.g ^ 13
				+ this.b ^ 29);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (false == Pixel.class.isAssignableFrom(o.getClass())) return false;
		
		final Pixel other = (Pixel)o;
		return this.r == other.r
				&& this.g == other.g
				&& this.b == other.b;
	}

}
