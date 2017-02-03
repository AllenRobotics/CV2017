package org.usfirst.frc.team5417.cv2017.customops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.usfirst.frc.team5417.cv2017.MatrixUtilities;

public class FindDistanceOperation {

	private HashMap<Pixel, Point> centersOfMass;
	
	public FindDistanceOperation(HashMap<Pixel, Point> centersOfMass){
		this.centersOfMass = centersOfMass;
	}
	
	
	public String name() {
		return "FindDistance";
	}

	public double findDistance(PixelMatrix m) throws Exception {

		List<Point> centersOfMass = new ArrayList<Point>(this.centersOfMass.values());
		
		if(centersOfMass.size()==2){
			Point groupCenter1 = centersOfMass.get(0);
			Point groupCenter2 = centersOfMass.get(1);
			
			double xDifference = (groupCenter2.getX() - groupCenter1.getX());
			double yDifference = (groupCenter2.getY() - groupCenter1.getY());

			double distance = Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
			return distance;
			
		} else{
			throw new Exception("did not find exactly 2 groups");
		}
	}

	
	
}
