package org.usfirst.frc.team5417.cv2017.customops;

public class PointD {

	private double x;
	private double y;

	public PointD(double x, double y){
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public PointD adjustByScale(double scaleFactor) {
		double newX = this.x * scaleFactor;
		double newY = this.y * scaleFactor;
		
		return new PointD(newX, newY);
	}
	
	// these functions:
	// public int hashCode()
	// public boolean equals(Object other)
	// are so that we can use a Point as a key in HashSet or HashMap

	@Override
	public int hashCode() {
		return (int) (Double.doubleToLongBits(this.x) ^ 3 + Double.doubleToLongBits(this.y) ^ 13);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (false == PointD.class.isAssignableFrom(o.getClass()))
			return false;

		final PointD other = (PointD) o;
		return this.x == other.x && this.y == other.y;
	}

}
