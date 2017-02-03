package org.usfirst.frc.team5417.cv2017;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Mat;
import org.usfirst.frc.team5417.cv2017.customops.BooleanMatrix;
import org.usfirst.frc.team5417.cv2017.customops.FindDistanceOperation;
import org.usfirst.frc.team5417.cv2017.customops.FindGroupsOperation;
import org.usfirst.frc.team5417.cv2017.customops.FindTargetPointOperation;
import org.usfirst.frc.team5417.cv2017.customops.MatchTemplatesOperation;
import org.usfirst.frc.team5417.cv2017.customops.PixelMatrixOperation;
import org.usfirst.frc.team5417.cv2017.customops.Pixel;
import org.usfirst.frc.team5417.cv2017.customops.PixelMatrix;
import org.usfirst.frc.team5417.cv2017.customops.Point;
import org.usfirst.frc.team5417.cv2017.customops.PointD;
import org.usfirst.frc.team5417.cv2017.customops.RemoveSmallGroupsOperation;
import org.usfirst.frc.team5417.cv2017.opencvops.ImageScaleOperation;
import org.usfirst.frc.team5417.cv2017.opencvops.OCVBGR2HSVOperation;
import org.usfirst.frc.team5417.cv2017.opencvops.OCVDilationOperation;
import org.usfirst.frc.team5417.cv2017.opencvops.OCVErosionOperation;
import org.usfirst.frc.team5417.cv2017.opencvops.OCVFilterColorOperation;
import org.usfirst.frc.team5417.cv2017.opencvops.OpenCVOperation;

public class ComputerVision2017 {

	public ComputerVisionResult DoComputerVision(ImageReader reader, int largestDimensionSize, ChannelRange c1Range,
			ChannelRange c2Range, ChannelRange c3Range, int dilateErodeKernelSize, int removeGroupsSmallerThan,
			double minimumTemplateScale, double maximumTemplateScale, double minimumTemplateMatchPercentage,
			List<BooleanMatrix> templatesToUse) {

		Mat m = reader.read();

		ImageScaleOperation scaleOp = new ImageScaleOperation(m, largestDimensionSize);
		m = scaleOp.resize();

		// m = MatrixUtilities.reverseColorChannels(m);

		OCVBGR2HSVOperation bgr2hsvOp = new OCVBGR2HSVOperation();
		m = bgr2hsvOp.doOperation(m);

		// filter colors
		OpenCVOperation filterOp = new OCVFilterColorOperation(c1Range, c2Range, c3Range);

		// dilate the white areas in the image to "heal" broken lines
		OpenCVOperation dilateOp = new OCVDilationOperation(dilateErodeKernelSize);

		// erode the white areas in the image (sort of undoes the dilation, but
		// keeps "healed" lines)
		OpenCVOperation erodeOp = new OCVErosionOperation(dilateErodeKernelSize);

		m = filterOp.doOperation(m);
		m = dilateOp.doOperation(m);
		m = erodeOp.doOperation(m);

		// convert open cv mat to PixelMatrix
		PixelMatrix pixelMatrix = new PixelMatrix(m);

		// find groups (operates on gray scale, outputs color)
		PixelMatrixOperation findGroupsOp = new FindGroupsOperation();

		pixelMatrix = findGroupsOp.doOperation(pixelMatrix);

		// calculate the group sizes
		HashMap<Pixel, Integer> groupSizes = MatrixUtilities.getGroupSizes(pixelMatrix);

		// remove all groups with too few pixels
		PixelMatrixOperation removeGroupsOp = new RemoveSmallGroupsOperation(removeGroupsSmallerThan, groupSizes);

		// remove all groups that don't match a template
		PixelMatrixOperation matchTemplatesOp = new MatchTemplatesOperation(templatesToUse, minimumTemplateScale,
				maximumTemplateScale, minimumTemplateMatchPercentage, groupSizes);

		pixelMatrix = removeGroupsOp.doOperation(pixelMatrix);
		pixelMatrix = matchTemplatesOp.doOperation(pixelMatrix);

		ComputerVisionResult cvResult = new ComputerVisionResult();

		cvResult.visionResult = pixelMatrix.toMat();
//		cvResult.visionResult = m;

		try {
			HashMap<Pixel, Point> centersOfMass = MatrixUtilities.findCentersOfMass(pixelMatrix);
			FindDistanceOperation findDistanceOp = new FindDistanceOperation(centersOfMass);

			cvResult.distance = findDistanceOp.findDistance(pixelMatrix) * scaleOp.getInverseScaleFactor();

			FindTargetPointOperation findTargetOp = new FindTargetPointOperation(centersOfMass);
			
			cvResult.targetPoint = findTargetOp.findTargetPoint();
			cvResult.targetPoint = cvResult.targetPoint.adjustByScale(scaleOp.getInverseScaleFactor());
			
			cvResult.didSucceed = true;

		} catch (Exception e) {
			cvResult.distance = -1;
			cvResult.targetPoint = new PointD(-1, -1);
			
			cvResult.didSucceed = false;
		}

		return cvResult;
	}

}
