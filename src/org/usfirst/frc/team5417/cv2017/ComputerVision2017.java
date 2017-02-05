package org.usfirst.frc.team5417.cv2017;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.opencv.core.Mat;
import org.usfirst.frc.team5417.cv2017.customops.*;
import org.usfirst.frc.team5417.cv2017.opencvops.*;

public class ComputerVision2017 {

	public ComputerVisionResult DoComputerVision(ImageReader reader, int largestDimensionSize, ChannelRange c1Range,
			ChannelRange c2Range, ChannelRange c3Range, int dilateErodeKernelSize, int removeGroupsSmallerThan,
			int numberOfScaleFactors, double minimumTemplateMatchPercentage, List<BooleanMatrix> templatesToUse,
			double[] distanceLookUpTable) {

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

		ComputerVisionResult cvResult = new ComputerVisionResult();

		List<PointD> centersOfMass;

		// dummy
		// cvResult.visionResult = m;

		// OpenCV
		{
			OCVFindGroupsWithFillOperation findGroupsOp = new OCVFindGroupsWithFillOperation();
			m = findGroupsOp.doOperation(m);

			List<Color> groupColors = findGroupsOp.getOutputColors();

			// calculate the group sizes
			OCVMatchTemplatesAndRemoveSmallGroupsOperation matchAndRemoveOp = new OCVMatchTemplatesAndRemoveSmallGroupsOperation(
					removeGroupsSmallerThan, templatesToUse, numberOfScaleFactors, minimumTemplateMatchPercentage,
					groupColors);
			m = matchAndRemoveOp.doOperation(m);

			centersOfMass = matchAndRemoveOp.getCentersOfMass();

			cvResult.visionResult = m;
		}

		try {
			FindDistanceOperation findDistanceOp = new FindDistanceOperation(centersOfMass, distanceLookUpTable);

			cvResult.distance = findDistanceOp.findDistanceInPixels() * scaleOp.getInverseScaleFactor();
			cvResult.distance = findDistanceOp.findDistanceInFeet(cvResult.distance);

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
