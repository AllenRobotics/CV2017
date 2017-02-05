package org.usfirst.frc.team5417.cv2017.opencvops;

//
// BooleanMatrix class handles the nastiness of 2 dimensional jagged arrays.
//
// Also, we use BooleanMatrix instead of Matrix when we want to deal only with booleans
// because dealing directly with booleans avoids the automatic Boxing/Unboxing that java
// does with built in types if they need to be stored as Objects.
//
// Boxing and Unboxing mean this:
// - Automatically turn boolean built-in type into Boolean class instance
// - Automatically turn Boolean class instance into boolean built-in type
// - Take note of the capitalization in the above 2 lines (Boolean/boolean)
//
public class BooleanMatrix {
	private boolean[][] items;

	private int rowCount;
	private int colCount;

	public BooleanMatrix(int rows, int cols, boolean defaultValue) {
		this.rowCount = rows;
		this.colCount = cols;

		items = new boolean[rows][];
		for (int r = 0; r < rows; r++) {
			items[r] = new boolean[cols];

			for (int c = 0; c < cols; c++) {
				items[r][c] = defaultValue;
			}
		}
	}

	public BooleanMatrix scale(double scaleFactor) {
		int newRows = (int) (this.rows() * scaleFactor);
		int newCols = (int) (this.cols() * scaleFactor);

		BooleanMatrix scaledMatrix = new BooleanMatrix(newRows, newCols, true);

		return scaledMatrix;

	}

	public void put(int r, int c, boolean item) {
		items[r][c] = item;
	}

	public boolean get(int r, int c) {
		return items[r][c];
	}

	public int rows() {
		return rowCount;
	}

	public int cols() {
		return colCount;
	}
}
