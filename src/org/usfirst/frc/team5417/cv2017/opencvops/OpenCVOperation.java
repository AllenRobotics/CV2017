package org.usfirst.frc.team5417.cv2017.opencvops;

import org.opencv.core.Mat;

public interface OpenCVOperation {

	public String name(); 
	public Mat doOperation(Mat m);
	
}
