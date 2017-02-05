package org.usfirst.frc.team5417.cv2017.opencvops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class OCVRemoveSmallGroupsOperation implements OpenCVOperation {

	private static double[] blackPixel = { 0, 0, 0 };

	private int minimumGroupPixelCount;
	private List<Color> groupColors;

	private List<Color> outputColors = new ArrayList<>();

	
	public String name() { return "Open CV Remove Small Groups"; }
	
	public List<Color> getOutputColors() {
		return outputColors;
	}

	public OCVRemoveSmallGroupsOperation(int minimumGroupPixelCount, List<Color> groupColors) {
		this.minimumGroupPixelCount = minimumGroupPixelCount;
		this.groupColors = groupColors;
	}

	@Override
	public Mat doOperation(Mat m) {

		Mat result = m;

		Scalar blackScalar = new Scalar(0);
		Mat blackMat = new Mat(m.rows(), m.cols(), m.type());
		blackMat.setTo(blackScalar);

	
		// find the colors to remove
		for (Color group : groupColors) {
			Scalar lowerBound = new Scalar(group.r, group.g, group.b);
			Scalar upperBound = lowerBound;
			
			Mat justTheOneGroupMat = new Mat();
			Core.inRange(m, lowerBound, upperBound, justTheOneGroupMat);
			
			int groupSize = Core.countNonZero(justTheOneGroupMat);
			if (groupSize < this.minimumGroupPixelCount) {
				// remove the group
				Core.bitwise_and(result, blackMat, result, justTheOneGroupMat);
			}
			else {
				outputColors.add(group);
			}
		}

		return result;
	}

}
