package org.usfirst.frc.team5417.cv2017;

import org.opencv.core.Mat;
import org.usfirst.frc.team5417.cv2017.opencvops.PointD;

public class ComputerVisionResult {

	public boolean didSucceed = false;
	
	public double distance = -1;
	public PointD targetPoint = null;
	public Mat visionResult = null;
	
}
