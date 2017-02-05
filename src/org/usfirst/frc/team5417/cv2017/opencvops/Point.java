package org.usfirst.frc.team5417.cv2017.opencvops;

public class Point{
	
	private int x;
	private int y;
	
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}
	
	
	// these functions:
		//   public int hashCode()
		//   public boolean equals(Object other)
		// are so that we can use a Point as a key in HashSet or HashMap
		
		@Override
		public int hashCode() {
			return (int)
					(this.x ^ 3
					+ this.y ^ 13
					);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			if (false == Point.class.isAssignableFrom(o.getClass())) return false;
			
			final Point other = (Point)o;
			return this.x == other.x
					&& this.y == other.y;
		}

	
}
