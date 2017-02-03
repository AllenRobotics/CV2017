package org.usfirst.frc.team5417.cv2017.opencvops;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OCVDilationOperation implements OpenCVOperation {

	private int regionWidth;

	public String name() { return "Open CV Dilation"; } 

	public OCVDilationOperation(int regionWidth) {
		this.regionWidth = regionWidth;
	}

	@Override
	public Mat doOperation(Mat m) {
		
		Mat kernel = getOCVSquareKernel(regionWidth);
		Mat result = new Mat();
		Imgproc.dilate(m, result, kernel);

		return result;
	}
	
	private Mat getOCVSquareKernel(int regionWidth) {
		Mat kernel = new Mat(regionWidth, regionWidth, CvType.CV_32F);
		
		for (int r = 0; r < kernel.rows(); r++) {
			for (int c = 0; c < kernel.cols(); c++) {
				kernel.put(r, c, 1);
			}
		}
		
		return kernel;
	}

}
