package org.usfirst.frc.team5417.cv2017.opencvops;


//
// Color is mutable. That means it's very important that we be careful when
// using it so that it can be safely used in hash-based data structures.
//
public class Color {
	
	public int r;
	public int g;
	public int b;
	
	public Color(double r, double g, double b) {
		this.r = (int)r;
		this.g = (int)g;
		this.b = (int)b;
	}
	
	public Color(double[] rgb) {
		put(rgb);
	}
	
	public Color(float[] rgb) {
		put(rgb);
	}

	public Color(int[] rgb) {
		put(rgb);
	}

	public void put(float[] rgb) {
		this.r = (int)rgb[0];
		this.g = (int)rgb[1];
		this.b = (int)rgb[2];
	}

	public void put(int[] rgb) {
		this.r = rgb[0];
		this.g = rgb[1];
		this.b = rgb[2];
	}

	public void put(double[] rgb) {
		this.r = (int)rgb[0];
		this.g = (int)rgb[1];
		this.b = (int)rgb[2];
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
		if (false == Color.class.isAssignableFrom(o.getClass())) return false;
		
		final Color other = (Color)o;
		return this.r == other.r
				&& this.g == other.g
				&& this.b == other.b;
	}

}
