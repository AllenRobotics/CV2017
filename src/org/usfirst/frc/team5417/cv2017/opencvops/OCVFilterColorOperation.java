package org.usfirst.frc.team5417.cv2017.opencvops;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.usfirst.frc.team5417.cv2017.ChannelRange;

public class OCVFilterColorOperation implements OpenCVOperation {

	private ChannelRange c1Range;
	private ChannelRange c2Range;
	private ChannelRange c3Range;

	public String name() { return "Open CV Filter Colors"; } 

	public OCVFilterColorOperation(ChannelRange c1Range, ChannelRange c2Range, ChannelRange c3Range) {
		this.c1Range = c1Range;
		this.c2Range = c2Range;
		this.c3Range = c3Range;
	}

	
	@Override
	public Mat doOperation(Mat m) {
		
		Mat result = new Mat();
		
		Scalar lowerBound = new Scalar(c1Range.getLowerBound(), c2Range.getLowerBound(), c3Range.getLowerBound());
		Scalar upperBound = new Scalar(c1Range.getUpperBound(), c2Range.getUpperBound(), c3Range.getUpperBound());
		
		Core.inRange(m, lowerBound, upperBound, result);
		
		return result;
	}

}
