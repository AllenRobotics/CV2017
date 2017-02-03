package org.usfirst.frc.team5417.cv2017.opencvops;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageScaleOperation {

	private Mat m;
	private int largestDimensionSize;
	
	private double scaleFactor = 1.0;
	
	public ImageScaleOperation(Mat m, int largestDimensionSize) {
		this.m = m;
		this.largestDimensionSize = largestDimensionSize;
	}
	
	public double getScaleFactor() {
		return this.scaleFactor;
	}
	
	public double getInverseScaleFactor() {
		return 1.0 / this.getScaleFactor();
	}
	
	public Mat resize() {
		// resize the image if necessary
		if (m.cols() > largestDimensionSize || m.rows() > largestDimensionSize) {
			
			if (m.cols() > m.rows()) {
				this.scaleFactor = (double)largestDimensionSize / m.cols();
			}
			else {
				this.scaleFactor = (double)largestDimensionSize / m.rows();
			}
		}
		else {
			this.scaleFactor = 1.0;
		}			
		
		int colcount = (int)(m.cols() * this.scaleFactor);
		int rowcount = (int)(m.rows() * this.scaleFactor);

		Size size = new Size(colcount, rowcount);
		Mat resizedMat = new Mat(size, CvType.CV_32F);
		Imgproc.resize(this.m, resizedMat, size);
		
		return resizedMat;
	}
}
