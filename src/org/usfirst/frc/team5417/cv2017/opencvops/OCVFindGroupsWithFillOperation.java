package org.usfirst.frc.team5417.cv2017.opencvops;

import java.util.List;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team5417.cv2017.customops.Pixel;

//
// see https://davidlavy.wordpress.com/opencv/connected-components-in-opencv/
//
// The basic idea is that we search for white pixels. Once we find one, we floodFill
// the entire region connected to the white pixel with a label value (2, 3, ... N).
//
// Then we randomly color each label in the output.
//
// Input: MUST be a binarized image (only 0 and 1 as pixel values)
// Output: A color image where all groups have unique colors
//
public class OCVFindGroupsWithFillOperation implements OpenCVOperation {

	public String name() { return "Open CV Find Groups With Fill"; } 

	@Override
	public Mat doOperation(Mat m) {

		// convert our black and white image (0 and 255)
		// to a binary image (0 and 1)
		Mat binaryImage = new Mat();
		Imgproc.threshold(m, binaryImage, 128.0, 1, Imgproc.THRESH_BINARY);

		//
		// copy color channels
		//
		int[] colorValue = new int[3];
		byte[] binaryValue = new byte[1];
		Mat colorImage = new Mat(binaryImage.size(), CvType.CV_32SC3);
		for (int y = 0; y < binaryImage.rows(); y++) {
			for (int x = 0; x < binaryImage.cols(); x++) {
				binaryImage.get(y, x, binaryValue);
				colorValue[0] = (short)binaryValue[0];
				colorValue[1] = (short)binaryValue[0];
				colorValue[2] = (short)binaryValue[0];
				colorImage.put(y, x, colorValue);
			}
		}
		
		//
		// generate unique colors for each connected component
		//
		int[] cellValue = new int[3];
		for (int y = 0; y < m.rows(); y++) {
			for (int x = 0; x < m.cols(); x++) {
				colorImage.get(y, x, cellValue);
				if (isUncoloredComponent(cellValue)) {
					Pixel newColor = generateNewColor();
					
					Mat mask = new Mat();
					// flood fill with a new label
					Imgproc.floodFill(colorImage, mask, new Point(x, y), new Scalar(newColor.r, newColor.g, newColor.b));
				}
			}
		}

		return colorImage;
	}

	private boolean isUncoloredComponent(int[] cellValue) {
		return cellValue[0] == 1 && cellValue[1] == 1 && cellValue[2] == 1;
	}
	
	private SecureRandom random = new SecureRandom();
	private HashSet<Pixel> usedColors = new HashSet<>();
	
	private boolean isCloseToBlack(Pixel color) {
		return color.r < 100 && color.g < 100 && color.b < 100;
	}
	
	private boolean hasBeenUsedBefore(Pixel color) {
		return usedColors.contains(color);
	}
	
	private Pixel generateNewColor() {
		// we use a mutable pixel here for speed, only creating Color objects as needed
		Pixel newColor = new Pixel(0, 0, 0, 0, 0);
		
		while (isCloseToBlack(newColor) || hasBeenUsedBefore(newColor))
		{
			newColor.r = random.nextInt() % 256;
			newColor.g = random.nextInt() % 256;
			newColor.b = random.nextInt() % 256;
			
			while (newColor.r < 0) newColor.r += 256;
			while (newColor.g < 0) newColor.g += 256;
			while (newColor.b < 0) newColor.b += 256;
			
			// force the color to not be close to black so we don't spin in this while loop
			// and wait for a color to randomly be generated that's not close to black.
			if (isCloseToBlack(newColor)) {
				if (newColor.r < 100) newColor.r += 150;
				if (newColor.g < 100) newColor.g += 150;
				if (newColor.b < 100) newColor.b += 150;
			}
		}

		usedColors.add(newColor);
		
		return newColor;
	}
}
