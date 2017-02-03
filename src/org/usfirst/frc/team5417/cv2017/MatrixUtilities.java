package org.usfirst.frc.team5417.cv2017;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team5417.cv2017.customops.BooleanMatrix;
import org.usfirst.frc.team5417.cv2017.customops.Pixel;
import org.usfirst.frc.team5417.cv2017.customops.PixelMatrix;
import org.usfirst.frc.team5417.cv2017.customops.Point;
import org.usfirst.frc.team5417.cv2017.opencvops.Color;

public class MatrixUtilities {

	public static void LoadOpenCVLibraries() {
		System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
	}

	public static Pixel blackPixel = new Pixel(0, 0, 0, 0, 0);
	public static Pixel whitePixel = new Pixel(0, 0, 255, 255, 255);
	public static Pixel blackColor = new Pixel(0, 0, 0, 0, 0);
	public static Pixel whiteColor = new Pixel(0, 0, 255, 255, 255);

	public static Mat reverseColorChannels(Mat m) {
		Imgproc.cvtColor(m, m, Imgproc.COLOR_BGR2RGB);
		return m;
	}
	
	public static BooleanMatrix getDiamondKernel(int regionWidth) {

		BooleanMatrix diamond = new BooleanMatrix(regionWidth, regionWidth, false);

		// build a diamond. first calculate width of diamond for this row in "w"
		for (int w = 1, r = 0; r <= diamond.rows() / 2; r++, w += 2) {
			for (int leftRightOffset = 0; leftRightOffset < w / 2 + 1; leftRightOffset++) {
				int r1 = r;
				int r2 = diamond.rows() - r - 1;
				int c1 = diamond.cols() / 2 - leftRightOffset;
				int c2 = diamond.cols() / 2 + leftRightOffset;

				diamond.put(r1, c1, true);
				diamond.put(r1, c2, true);
				diamond.put(r2, c1, true);
				diamond.put(r2, c2, true);
			}
		}

		return diamond;
	}

	public static BooleanMatrix getSquareKernel(int regionWidth) {

		BooleanMatrix square = new BooleanMatrix(regionWidth, regionWidth, true);

		return square;
	}

	public static boolean isInImage(int r, int c, int rows, int cols) {
		return r >= 0 && c >= 0 && r < rows && c < cols;
	}

	public static BooleanMatrix getRectangleKernel(int regionWidth, int regionHeight) {

		BooleanMatrix square = new BooleanMatrix(regionWidth, regionHeight, true);

		return square;
	}

//	public static void addAdjacentPointsToHashSet(HashSet<Point> adjacentPoints, int r, int c, PixelMatrix image,
//			Matrix kernel, Pixel onlyMatchColor) {
//
//		int startR = r - kernel.rows() / 2;
//		int startC = c - kernel.cols() / 2;
//
//		int endR = r + kernel.rows() / 2;
//		int endC = c + kernel.cols() / 2;
//
//		for (int RR = startR; RR <= endR; RR++) {
//			for (int CC = startC; CC <= endC; CC++) {
//				if (isInImage(RR, CC, image.rows(), image.cols())) {
//					if (kernel.get(RR - startR, CC - startC).equals(1)) {
//
//						Pixel pixel = image.get(RR, CC);
//
//						if (onlyMatchColor == null || (onlyMatchColor.r == pixel.r && onlyMatchColor.g == pixel.g
//								&& onlyMatchColor.b == pixel.b)) {
//
//							// pixel is set
//							adjacentPoints.add(pixel.location);
//						}
//					}
//				}
//			}
//		}
//	}

	public static void addAdjacentPointsIfNotProcessed(List<Point> adjacentPoints, BooleanMatrix alreadyProcessedPoints, int r,
			int c, PixelMatrix image, BooleanMatrix kernel, Pixel onlyMatchThisColor) {

		int startR = r - kernel.rows() / 2;
		int startC = c - kernel.cols() / 2;

		int endR = r + kernel.rows() / 2;
		int endC = c + kernel.cols() / 2;

		for (int RR = startR; RR <= endR; RR++) {
			for (int CC = startC; CC <= endC; CC++) {
				
				if (isInImage(RR, CC, image.rows(), image.cols())) {

					//
					// This optimization is faster than using a HashSet. Here we use a 
					// BooleanMatrix to record whether or not we've already processed a
					// pixel.
					// 
					// If we've already processed it, we don't need to do it again.
					//
					if (alreadyProcessedPoints.get(RR, CC) == false) {
						
						// if our kernel says that we should process this pixel
						if (kernel.get(RR - startR, CC - startC) == true) {

							Pixel pixel = image.get(RR, CC);
							if (pixel.r == onlyMatchThisColor.r && pixel.g == onlyMatchThisColor.g && pixel.b == onlyMatchThisColor.b) {
								// pixel is set
								adjacentPoints.add(pixel.location);
							}

							alreadyProcessedPoints.put(RR, CC, true);
						}
					}
				}
			}
		}
	}

	public static boolean isBlackPixel(Pixel pixel) {
		if (pixel.r == 0 && pixel.g == 0 && pixel.b == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlackPixel(float[] pixel) {
		if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlackPixel(int[] pixel) {
		if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isBlackPixel(double[] pixel) {
		if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static HashMap<Pixel, Integer> getGroupSizes(PixelMatrix m) {

		HashMap<Pixel, Integer> groupsToCount = new HashMap<>();

		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				Pixel pixel = m.get(r, c);

				if (!MatrixUtilities.isBlackPixel(pixel)) {
					if (groupsToCount.containsKey(pixel)) {
						Integer count = groupsToCount.get(pixel);
						groupsToCount.put(pixel, count + 1);
					} else {
						groupsToCount.put(pixel, 1);
					}
				}
			}
		}

		return groupsToCount;
	}

	public static HashMap<Color, Integer> getGroupSizes(Mat m) {

		HashMap<Color, Integer> groupsToCount = new HashMap<>();

		int[] pixel = new int[3];
		Color color = new Color(0, 0, 0);
		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				m.get(r, c, pixel);

				// if the pixel is black
				if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
					// do nothing if it's black
				}
				// else the pixel is not black, so count the size of the group
				else {
					color.put(pixel);
					if (groupsToCount.containsKey(color)) {
						Integer count = groupsToCount.get(color);
						groupsToCount.put(color, count + 1);
					} else {
						Color newColor = new Color(color.r, color.g, color.b);
						groupsToCount.put(newColor, 1);
					}
				}
			}
		}

		return groupsToCount;
	}

	public static HashMap<Pixel, Point> findCentersOfMass(PixelMatrix m){
		HashMap<Pixel, CenterOfMass> masses = new HashMap<>();

		for (int y = 0; y < m.rows(); y++) {
			for (int x = 0; x < m.cols(); x++) {

				Pixel pixel = m.get(y, x);
				if (!MatrixUtilities.isBlackPixel(pixel)) {
					if (masses.containsKey(pixel)) {
						// We need to update the group
						CenterOfMass mass = masses.get(pixel);
						mass.x = mass.x + x;
						mass.y = mass.y + y;
						mass.numberOfPixels++;
					} else {
						// We need to add the group with the HashMap
						CenterOfMass mass = new CenterOfMass(x, y, 1);
						masses.put(pixel, mass);

					}
				}

			}
		}
		HashMap<Pixel, Point> findResults = new HashMap<>();

		for (Pixel pixel : masses.keySet()) {
			CenterOfMass mass = masses.get(pixel);
			mass.x = mass.x / mass.numberOfPixels;
			mass.y = mass.y / mass.numberOfPixels;

			findResults.put(pixel, new Point(mass.x, mass.y));

		}
		return findResults;

	}
}
