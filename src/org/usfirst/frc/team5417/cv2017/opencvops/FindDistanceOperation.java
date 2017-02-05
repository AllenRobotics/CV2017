package org.usfirst.frc.team5417.cv2017.opencvops;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class FindDistanceOperation {

	private List<PointD> centersOfMass;
	private double[] pixelsToFeetLookUpTable;

	public FindDistanceOperation(List<PointD> centersOfMass, double[] pixelsToFeetLookUpTable) {
		this.centersOfMass = centersOfMass;
		this.pixelsToFeetLookUpTable = pixelsToFeetLookUpTable;

	}

	public String name() {
		return "FindDistance";
	}

	public double findDistanceInPixels() throws Exception {

		if (centersOfMass.size() == 2) {
			PointD groupCenter1 = centersOfMass.get(0);
			PointD groupCenter2 = centersOfMass.get(1);

			double xDifference = (groupCenter2.getX() - groupCenter1.getX());
			double yDifference = (groupCenter2.getY() - groupCenter1.getY());

			double distance = Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
			return distance;

		} else {
			throw new Exception("did not find exactly 2 groups");
		}
	}

	public double findDistanceInFeet(double distanceInPixels) throws Exception {


		for (int i = 0; i < pixelsToFeetLookUpTable.length - 1; i++) {

			if (pixelsToFeetLookUpTable[i] >= distanceInPixels && pixelsToFeetLookUpTable[i + 1] <= distanceInPixels) {
				// distance is between Foot i and Foot i+1

				double feetPerPixel = 1.0 / (pixelsToFeetLookUpTable[i] - pixelsToFeetLookUpTable[i + 1]);
				double pixelOffset = pixelsToFeetLookUpTable[i] - distanceInPixels;
				double footOffset = pixelOffset * feetPerPixel;
				double distanceInFeet = footOffset + i;

				return distanceInFeet;
			}

		}

		throw new Exception("Distance not found in Look Up Table");
	}

}
