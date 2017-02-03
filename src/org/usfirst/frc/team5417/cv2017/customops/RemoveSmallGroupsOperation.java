package org.usfirst.frc.team5417.cv2017.customops;

import java.util.HashMap;
import java.util.HashSet;

import org.usfirst.frc.team5417.cv2017.MatrixUtilities;

//
//Removes small groups from the image
//Input: a color image, where each group is a unique color
//Output: a color image, where small groups of color have been removed
//
public class RemoveSmallGroupsOperation implements PixelMatrixOperation {

	private int minimumGroupPixelCount;
	private HashMap<Pixel, Integer> groupSizes;
	
	public String name() { return "Remove Small Groups"; } 

	public RemoveSmallGroupsOperation(int minimumGroupPixelCount, HashMap<Pixel, Integer> groupSizes) {
		this.minimumGroupPixelCount = minimumGroupPixelCount;
		this.groupSizes = groupSizes;
	}

	@Override
	public PixelMatrix doOperation(PixelMatrix m) {


		PixelMatrix result = m;

		HashSet<Pixel> colorsToRemove = new HashSet<>();

		for (Pixel color : groupSizes.keySet()) {
			Integer count = groupSizes.get(color);

			if (count < minimumGroupPixelCount) {
				// NOTE: we MUST make a new Pixel here because we mutate the pixels
				// below as we go, thus invalidating any of them that have been added
				// to the colorsToRemove HashSet
				colorsToRemove.add(new Pixel(color.location.getX(), color.location.getY(), color));
			}
		}

		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {
				Pixel pixel = m.get(r, c);

				if (!MatrixUtilities.isBlackPixel(pixel)) {
					if (colorsToRemove.contains(pixel)) {
						result.put(r, c, MatrixUtilities.blackPixel);
					}
				}

			}

		}

		return result;
	}

}
