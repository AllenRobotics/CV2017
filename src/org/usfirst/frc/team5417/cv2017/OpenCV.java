package org.usfirst.frc.team5417.cv2017;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCV {

	public static void LoadLibraries() {
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	}

	public static Mat reverseColorChannels(Mat m) {
		Imgproc.cvtColor(m, m, Imgproc.COLOR_BGR2RGB);
		return m;
	}

	public static List<Double> generateScaleFactors(int numberOfFactors) {
		
		List<Double> scales = new ArrayList<>();

		// calculate the maximum x value as the log base 2 of numberOfFactors
		double maxX = Math.log(numberOfFactors) / Math.log(2);
		double minX = 0;
		
		double xStep = maxX / numberOfFactors;
		
		
		double x = minX;
		
		while (x <= maxX) {
			// scale is x squared
			double scale = x * x;
			scales.add(scale);
			x += xStep;
		}
		
		if (x != maxX) {
			scales.add(maxX * maxX);
		}
		
		return scales;
	}

}
