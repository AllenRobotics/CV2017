package org.usfirst.frc.team5417.cv2017.customops;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.usfirst.frc.team5417.cv2017.MatrixUtilities;

//
// See https://en.wikipedia.org/wiki/Disjoint-set_data_structure
//
// The FindGroupsOperation identifies non-overlapping groups of non-black pixels
// and assigns each group a unique color in the output image.
//
// Input: a grayscale or binarized (black or white) image
// Output: a color image where each identified group is color coded 
//
public class FindGroupsOperation implements PixelMatrixOperation {

	private SecureRandom random = new SecureRandom();
	private HashSet<Pixel> usedColors = new HashSet<>();

	public String name() { return "Find Groups"; } 

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
	
	public class DisjointSetNode {
		// this is private so that we're forced to call getOrGenerateColor
		private Pixel rgb;
		
		public DisjointSetNode parent;
		public int rank;
	
		// we should only call this function when we're creating the
		// output image. it's much faster to generate one color for
		// each group than to generate one color for each pixel. that's
		// why we took the generateNewColor call out of MakeSet().
		public Pixel getOrGenerateColor() {
			if (this.rgb == null) {
				this.rgb = generateNewColor();
			}
			return this.rgb;
		}
	}

	public boolean isBlackPixel(Pixel pixel) {
		return pixel.gray() == 0;
	}

	@Override
	public PixelMatrix doOperation(PixelMatrix m) {

		Matrix nodeGroups = new Matrix(m.rows(), m.cols(), null);

		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				// Build a matrix of DisjointSetNodes where every black pixel is
				// null and otherwise is set

				Pixel gray = m.get(r, c);

				if (isBlackPixel(gray)) {
					nodeGroups.put(r, c, null);
				} else {
					DisjointSetNode newNode = MakeSet();
					nodeGroups.put(r, c, newNode);
				}

			}
		}

		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				DisjointSetNode currentNode = (DisjointSetNode) nodeGroups.get(r, c);

				// identifying groups
				if (currentNode != null) {
					DisjointSetNode[] adjacentNodes = getAdjacentNodes(r, c, m.rows(), m.cols(), nodeGroups, 3);
					for (DisjointSetNode node : adjacentNodes) {
						Union(currentNode, node);
					}
				}
			}
		}

		
		//PixelMatrix result = new PixelMatrix(m.rows(), m.cols());
		PixelMatrix result = m;
		
		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {

				DisjointSetNode currentNode = (DisjointSetNode) nodeGroups.get(r, c);

				// generating the color coded output image by using a
				// new color for each group.
				if (currentNode == null) {
					result.put(r, c, MatrixUtilities.blackPixel);
				} else {
					DisjointSetNode root = Find(currentNode);
					result.put(r, c, root.getOrGenerateColor());
				}
			}
		}

		return result;
	}

	private DisjointSetNode[] getAdjacentNodes(int r, int c, int rows, int cols, Matrix nodeGroups, int regionWidth) {

		int startR = r - regionWidth / 2;
		int startC = c - regionWidth / 2;

		int endR = r + regionWidth / 2;
		int endC = c + regionWidth / 2;

		List<DisjointSetNode> adjacentNodes = new ArrayList<DisjointSetNode>();

		for (int RR = startR; RR <= endR; RR++) {
			for (int CC = startC; CC <= endC; CC++) {
				if (isInImage(RR, CC, rows, cols)) {

					DisjointSetNode potentialNode = (DisjointSetNode) nodeGroups.get(RR, CC);

					if (potentialNode != null) {
						// pixel is set
						adjacentNodes.add(potentialNode);
					}
				}
			}
		}

		DisjointSetNode[] result = new DisjointSetNode[adjacentNodes.size()];
		result = adjacentNodes.toArray(result);
		return result;
	}

	private boolean isSamePoint(int r1, int c1, int r2, int c2) {
		return r1 == r2 && c1 == c2;
	}

	private boolean isInImage(int r, int c, int rows, int cols) {
		return r >= 0 && c >= 0 && r < rows && c < cols;
	}

	private DisjointSetNode MakeSet() {

		DisjointSetNode newNode = new DisjointSetNode();
		newNode.parent = newNode;
		newNode.rank = 0;

		return newNode;
	}

	private DisjointSetNode Find(DisjointSetNode x) {
		if (x.parent != x) {
			x.parent = Find(x.parent);
		}
		return x.parent;
	}

	private void Union(DisjointSetNode x, DisjointSetNode y) {
		DisjointSetNode xRoot = Find(x);
		DisjointSetNode yRoot = Find(y);

		if (xRoot == yRoot)
			return;

		if (xRoot.rank < yRoot.rank) {
			xRoot.parent = yRoot;
		} else if (xRoot.rank > yRoot.rank) {
			yRoot.parent = xRoot;
		} else {
			yRoot.parent = xRoot;
			xRoot.rank = xRoot.rank + 1;
		}
	}

}
