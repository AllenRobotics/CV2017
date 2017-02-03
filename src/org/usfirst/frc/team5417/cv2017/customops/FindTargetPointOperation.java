package org.usfirst.frc.team5417.cv2017.customops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindTargetPointOperation {

	HashMap<Pixel, Point> centersOfMass;

	public FindTargetPointOperation(HashMap<Pixel, Point> centersOfMass) {
		this.centersOfMass = centersOfMass;

	}

	public String name() {
		return "FindTargetPoint";
	}

	public PointD findTargetPoint() throws Exception {

		List<Point> centersOfMass = new ArrayList<Point>(this.centersOfMass.values());

		if (centersOfMass.size() == 2) {
			Point groupCenter1 = centersOfMass.get(0);
			Point groupCenter2 = centersOfMass.get(1);

			double xMidPoint = (groupCenter1.getX() + groupCenter2.getX()) / 2;
			double yMidPoint = (groupCenter1.getY() + groupCenter2.getY()) / 2;

			return new PointD(xMidPoint, yMidPoint);

		} else {
			throw new Exception("did not find exactly 2 groups");
		}

	}
}
