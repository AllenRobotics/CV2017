package org.usfirst.frc.team5417.cv2017;

//
// Used in the FilterColorOperation
//
public class ChannelRange {

	private double lowerBound;
	private double upperBound;
	
	public ChannelRange(double lowerBound, double upperBound){
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public double getLowerBound(){
		return lowerBound;
	}
	
	public double getUpperBound(){
		return upperBound;
	}
	
	
}
