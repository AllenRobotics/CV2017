package org.usfirst.frc.team5417.cv2017.customops;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

//
// PixelMatrix wraps the nastiness of dealing with 2 dimensional jagged arrays.
// It also wraps the nastiness of dealing with opencv Mat's.
//
public class PixelMatrix {

	private Pixel[][] pixels;
	private int rowCount;
	private int colCount;

	public PixelMatrix(int rows, int cols) {
		this.rowCount = rows;
		this.colCount = cols;

		pixels = new Pixel[rows][];
		for (int r = 0; r < rows; r++) {
			pixels[r] = new Pixel[cols];

			for (int c = 0; c < cols; c++) {
				pixels[r][c] = new Pixel(c, r, 0, 0, 0);
			}
		}
	}

	public PixelMatrix(Mat m) {
		this(m.rows(), m.cols());
		
		for (int r = 0; r < m.rows(); r++) {
			for (int c = 0; c < m.cols(); c++) {
//				if (c == 136 && r == 66) {
//					System.out.println("this is green");
//				}
//				if (c == 320 && r == 240) {
//					System.out.println("this is middle");
//				}
				double[] pixel = m.get(r, c);
				if (pixel.length == 3) {
					this.put(r, c, pixel);
				}
				else {
					// gray
					this.put(r, c, pixel[0]);
				}
			}
		}
	}
	
	public void put(int r, int c, double[] channels) {
		this.get(r, c).put(channels);
	}
	
	public void put(int r, int c, double gray) {
		this.get(r, c).put(gray);
	}

	public void put(int r, int c, Pixel pixel) {
		this.get(r, c).put(pixel);
	}

	public Pixel get(int r, int c) {
		if (r < 0 || c < 0 || r >= rows() || c >= cols()) {
			System.err.println("Uh-oh!");
		}
		return pixels[r][c];
	}

	public int rows() {
		return rowCount;
	}

	public int cols() {
		return colCount;
	}

	public Mat toMat() {
		Mat m = new Mat(rows(), cols(), CvType.CV_32SC3);

		for (int r = 0; r < rows(); r++) {
			for (int c = 0; c < cols(); c++) {
				Pixel pixel = this.get(r, c);

				double[] bgr = { pixel.b, pixel.g, pixel.r };
				m.put(r, c, bgr);
			}
		}

		return m;
	}
}
