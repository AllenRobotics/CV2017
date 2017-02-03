package org.usfirst.frc.team5417.cv2017.opencvops;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.usfirst.frc.team5417.cv2017.customops.BooleanMatrix;
import org.usfirst.frc.team5417.cv2017.customops.Point;
import org.usfirst.frc.team5417.cv2017.MatrixUtilities;

public class OCVMatchTemplatesOperation implements OpenCVOperation {

	private static double[] blackPixel = { 0, 0, 0 };

	public class CenterSum {
		public BigInteger xSum = new BigInteger("0");
		public BigInteger ySum = new BigInteger("0");
		public int size = 0;

	}

	private List<BooleanMatrix> templates;
	private double minimumScale, maximumScale;
	private double minimumMatchPercentage;
	private HashMap<Color, Integer> groupSizes;

	
	
	@Override
	public String name() { return "Open CV Match Templates"; }

	public OCVMatchTemplatesOperation(List<BooleanMatrix> templates, double minimumScale, double maximumScale, double minimumMatchPercentage, HashMap<Color, Integer> groupSizes) {
		this.templates = templates;
		this.minimumScale = minimumScale;
		this.maximumScale = maximumScale;
		this.minimumMatchPercentage = minimumMatchPercentage;
		this.groupSizes = groupSizes;
	}


	@Override
	public Mat doOperation(Mat m) {

		Mat result = new Mat();
		m.assignTo(result, CvType.CV_32SC3);
		
		// only generate this many scaleFactors
		int numberOfScaleFactors = 10;
		double stepSize = (maximumScale - minimumScale) / numberOfScaleFactors;
		
		List<Point> centers = findCenters(m);
		List<Double> scaleFactors = generateScaleFactors(minimumScale, maximumScale, stepSize);
		
		// pre-scale the templates to save a few CPU cycles
		HashMap<Double, List<BooleanMatrix>> scaledTemplates = new HashMap<>();
		for (Double scaleFactor : scaleFactors) {
			
			List<BooleanMatrix> templatesForCurrentScale = new ArrayList<>();
			for (BooleanMatrix template : this.templates) {
				
				BooleanMatrix scaledTemplate = template.scale(scaleFactor);		
				templatesForCurrentScale.add(scaledTemplate);
			}

			scaledTemplates.put(scaleFactor, templatesForCurrentScale);
		}
		
		// now process the pre-scaled templates
		for (Point center : centers) {

			Color groupColor = getGroupColor(m, center);
			boolean doesGroupMatchAnyTemplate = false;
			
			for (Double scaleFactor : scaleFactors) {
				
				// get our list of pre-scaled templates for this scale
				List<BooleanMatrix> templatesForCurrentScale = scaledTemplates.get(scaleFactor);

				// process each scaled template one after the other
				for (BooleanMatrix scaledTemplate : templatesForCurrentScale) {

					double matchPercentage = 0;
					if (groupColor != null) {
						matchPercentage = getMatchPercentage(m, scaledTemplate, center, groupColor, groupSizes);
					}
					
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
				removeGroup(result, groupColor);
			}
		}
		
		return result;
	}

	private void removeGroup(Mat m, Color groupColor) {

		int[] pixel = new int[3];
		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				m.get(r, c, pixel);
				
				if (pixel[0] == groupColor.r && pixel[1] == groupColor.g && pixel[2] == groupColor.b) {
					m.put(r, c, blackPixel);
				}
			}
		}
	}
	
	private List<Point> findCenters(Mat m) {

		HashMap<Color, CenterSum> groupsCenter = new HashMap<>();

		int[] pixel = new int[3];
		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				m.get(r, c, pixel);

				if (!MatrixUtilities.isBlackPixel(pixel)) {
					
					Color color = new Color(pixel);
					
					if (groupsCenter.containsKey(color)) {
						CenterSum sum = groupsCenter.get(color);
						sum.xSum = sum.xSum.add(new BigInteger(new Integer(c).toString()));
						sum.ySum = sum.ySum.add(new BigInteger(new Integer(r).toString()));
						sum.size++;
					} else {
						CenterSum sum = new CenterSum();
						sum.xSum = sum.xSum.add(new BigInteger(new Integer(c).toString()));
						sum.ySum = sum.ySum.add(new BigInteger(new Integer(r).toString()));
						sum.size++;
						
						groupsCenter.put(color, sum);
					}
				}
			}
		}

		List<Point> result = new ArrayList<Point>();

		for (Color color : groupsCenter.keySet()) {

			CenterSum sum = groupsCenter.get(color);
			int centerX = sum.xSum.divide(new BigInteger((new Integer(sum.size).toString()))).intValue();
			int centerY = sum.ySum.divide(new BigInteger((new Integer(sum.size).toString()))).intValue();
			
			Point center = new Point(centerX, centerY);
			result.add(center);
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
	
	private Color getGroupColor(Mat m, Point center) {
		double[] centerPixel = m.get(center.getY(), center.getX());

		if (!MatrixUtilities.isBlackPixel(centerPixel)) {
			return new Color(centerPixel);
		}
		else {

			// find the first non-black pixel closest to the the center.
			// search outward from the center 
			
			int[] pixel = new int[3];
			for (int offsetR = 1; offsetR < m.rows(); offsetR++) {
				for (int offsetC = 1; offsetC < m.cols(); offsetC++) {

					Point topLeftCorner = new Point(center.getX() - offsetC , center.getY() - offsetR);
					Point bottomRightCorner = new Point(center.getX() + offsetC, center.getY() - offsetR);

					// find the next concentric ring of pixels
					for (int r = topLeftCorner.getY(); r < bottomRightCorner.getY(); ++r) {

						// search the left vertical side
						if (MatrixUtilities.isInImage(r, topLeftCorner.getX(), m.rows(), m.cols())) {
							m.get(r, topLeftCorner.getX(), pixel);
							if (!MatrixUtilities.isBlackPixel(pixel)) {
								return new Color(pixel);
							}
						}

						// search the right vertical side
						if (MatrixUtilities.isInImage(r, bottomRightCorner.getX(), m.rows(), m.cols())) {
							m.get(r, bottomRightCorner.getX(), pixel);
							if (!MatrixUtilities.isBlackPixel(pixel)) {
								return new Color(pixel);
							}
						}

					}
					for (int c = topLeftCorner.getX(); c < bottomRightCorner.getX(); ++c) {
						// search the top row
						if (MatrixUtilities.isInImage(topLeftCorner.getY(), c, m.rows(), m.cols())) {
							m.get(topLeftCorner.getY(), c, pixel);
							if (!MatrixUtilities.isBlackPixel(pixel)) {
								return new Color(pixel);
							}
						}

						// search the bottom row
						if (MatrixUtilities.isInImage(bottomRightCorner.getY(), c, m.rows(), m.cols())) {
							m.get(bottomRightCorner.getY(), c, pixel);
							if (!MatrixUtilities.isBlackPixel(pixel)) {
								return new Color(pixel);
							}
						}
					}
				}
			}
		}
		
		// this can happen if there aren't any groups in the image
		return null;
	}
	
	private double getMatchPercentage(Mat m, BooleanMatrix template, Point center, Color groupColor, HashMap<Color, Integer> groupsToCount) {
		
		double templateSize = template.rows() * template.cols();

		int matchCount = 0;
		
		int[] pixel = new int[3];
		for (int r = 0; r < template.rows(); r++) {
			for (int c = 0; c < template.cols(); c++) {
				
				int imageR = r + center.getY() - template.rows() / 2;
				int imageC = c + center.getX() - template.cols() / 2;
				
				if (MatrixUtilities.isInImage(imageR,  imageC, m.rows(), m.cols())) {
					m.get(imageR, imageC, pixel);
					if (!MatrixUtilities.isBlackPixel(pixel)) {
						if (pixel[0] == groupColor.r && pixel[1] == groupColor.g && pixel[2] == groupColor.b) {
							matchCount++;
						}
						else {
							int i = 10;
							if (i % 4 == 0) {
								System.out.println("000");
							}
						}
					}
				}
			}
		}
		
		int groupSize = groupsToCount.get(groupColor);
		return ((double)matchCount * 2.0) / (templateSize + groupSize);
	}

}
