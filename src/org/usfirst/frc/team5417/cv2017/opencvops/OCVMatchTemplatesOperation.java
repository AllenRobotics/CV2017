package org.usfirst.frc.team5417.cv2017.opencvops;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.usfirst.frc.team5417.cv2017.customops.BooleanMatrix;
import org.usfirst.frc.team5417.cv2017.customops.Point;
import org.usfirst.frc.team5417.cv2017.MatrixUtilities;

public class OCVMatchTemplatesOperation implements OpenCVOperation {

	private List<BooleanMatrix> templates;
	private double minimumScale, maximumScale;
	private double minimumMatchPercentage;
	private List<Color> groupColors;
	
	@Override
	public String name() { return "Open CV Match Templates"; }

	public OCVMatchTemplatesOperation(List<BooleanMatrix> templates, double minimumScale, double maximumScale, double minimumMatchPercentage, List<Color> groupColors) {
		this.templates = templates;
		this.minimumScale = minimumScale;
		this.maximumScale = maximumScale;
		this.minimumMatchPercentage = minimumMatchPercentage;
		this.groupColors = groupColors;
	}


	@Override
	public Mat doOperation(Mat m) {

		Mat result = m;
		
		// only generate this many scaleFactors
		int numberOfScaleFactors = 10;
		double stepSize = (maximumScale - minimumScale) / numberOfScaleFactors;

		List<Double> scaleFactors = generateScaleFactors(minimumScale, maximumScale, stepSize);

		Scalar blackScalar = new Scalar(0);
		Scalar whiteScalar = new Scalar(255);
		Mat blackMat = new Mat(m.rows(), m.cols(), m.type());
		
		for (Color group : groupColors) {

			boolean doesGroupMatchAnyTemplate = false;

			Scalar lowerBound = new Scalar(group.r, group.g, group.b);
			Scalar upperBound = lowerBound;
			
			Mat justTheOneGroupMat = new Mat();
			Core.inRange(m, lowerBound, upperBound, justTheOneGroupMat);
			
			int groupSize = Core.countNonZero(justTheOneGroupMat);

			// find center using moments function (http://answers.opencv.org/question/460/finding-centroid-of-a-mask/)
			Moments moments = Imgproc.moments(justTheOneGroupMat);
			Point center = new Point(
					(int)(moments.get_m10() / moments.get_m00()),
					(int)(moments.get_m01() / moments.get_m00())
					);
			
			for (Double scaleFactor : scaleFactors) {

				for (BooleanMatrix template : this.templates) {

					int templateWidth = (int)Math.max(1, template.cols() * scaleFactor);
					int templateHeight = (int)Math.max(1,  template.rows() * scaleFactor);
					
					int p1x = Math.max(0, center.getX() - templateWidth / 2);
					int p1y = Math.max(0, center.getY() - templateHeight / 2);
					int p2x = Math.min(m.cols(), p1x + templateWidth);
					int p2y = Math.min(m.rows(), p1y + templateHeight);
					
					Mat templateMat = new Mat(m.rows(), m.cols(), CvType.CV_8UC1);
					templateMat.setTo(blackScalar);
					
					Rect roi = new Rect(p1x, p1y, p2x - p1x, p2y - p1y);
					Mat templateRoi = templateMat.submat(roi);
					templateRoi.setTo(whiteScalar);
					
//					int templateSize = Core.countNonZero(templateMat);
//					System.out.println(templateSize);
					
					int templateArea = templateWidth * templateHeight;
					
					Mat matchMat = new Mat();
					Core.bitwise_and(justTheOneGroupMat, templateMat, matchMat);
					
					int matchSize = Core.countNonZero(matchMat);
					
					double matchPercentage = (double)(matchSize * 2) / (groupSize + templateArea);

					if (matchPercentage < minimumMatchPercentage) {
						// the match percentage is too low, so we need to get rid of this group
						doesGroupMatchAnyTemplate = false;						
					}
					else {
						// the match percentage is high enough, so we keep this group
						doesGroupMatchAnyTemplate = true;
						break;
					}
				}

				if (doesGroupMatchAnyTemplate) {
					break;
				}
			}
			
			if (!doesGroupMatchAnyTemplate) {
				// the match percentage is too low, so we need to get rid of this group
				Core.bitwise_and(result, blackMat, result, justTheOneGroupMat);
			}
		}
		
		return result;
	}

	private List<Double> generateScaleFactors(double min, double max, double step) {
		
		List<Double> scales = new ArrayList<>();
		scales.add(min);

		if (step < .1) {
			step = .1;
		}
		
		double current = min + step;
		
		while (current < max) {
			scales.add(current);
			current += step;
		}
		
		if (min != max && current != max) {
			scales.add(max);
		}
		
		return scales;
	}

}
