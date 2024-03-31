import ij.*;

public class FilteringSession {

	/*******************************************************************************
	 *
	 * E D G E   D E T E C T O R   S E C T I O N
	 *
	 ******************************************************************************/

	/**
	 * Detects the vertical edges inside an ImageAccess object.
	 * This is the non-separable version of the edge detector.
	 * The kernel of the filter has the following form:
	 *
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *
	 * Mirror border conditions are applied.
	 */
	static public ImageAccess detectEdgeVertical_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);
		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel = arr[2][0]+arr[2][1]+arr[2][2]-arr[0][0]-arr[0][1]-arr[0][2];
				pixel = pixel / 6.0;
				out.putPixel(x, y, pixel);
			}
		}
		return out;
	}

	/**
	 * Detects the vertical edges inside an ImageAccess object.
	 * This is the separable version of the edge detector.
	 * The kernel of the filter applied to the rows has the following form:
	 *     -------------------
	 *     | -1  |  0  |  1  |
	 *     -------------------
	 *
	 * The kernel of the filter applied to the columns has the following 
	 * form:
	 *     -------
	 *     |  1  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *     |  1  |
	 *     -------
	 *
	 * Mirror border conditions are applied.
	 */
	static public ImageAccess detectEdgeVertical_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			doDifference3(rowin, rowout);
			out.putRow(y, rowout);
		}
		
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			out.getColumn(x, colin);
			doAverage3(colin, colout);
			out.putColumn(x, colout);
		}
		return out;
	}

	static public ImageAccess detectEdgeHorizontal_NonSeparable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[3][3];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);

		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel = arr[0][2] + arr[1][2] + arr[2][2] -
						arr[0][0] - arr[1][0] - arr[2][0];
				pixel = pixel / 6.0;
				out.putPixel(x, y, pixel);
			}
		}
		return out;
	}

	static public ImageAccess detectEdgeHorizontal_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double colIn[]  = new double[ny];
		double colOut[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			input.getColumn(x, colIn);
			doDifference3(colIn, colOut);
			out.putColumn(x, colOut);
		}
		
		double rowIn[]  = new double[nx];
		double rowOut[] = new double[nx];
		for (int y = 0; y < ny; y++) {
			out.getRow(y, rowIn);
			doAverage3(rowIn, rowOut);
			out.putRow(y, rowOut);
		}

		return out;
	}

	/**
	 * Implements an one-dimensional average filter of length 3.
	 * The filtered value of a pixel is the averaged value of
	 * its local neighborhood of length 3.
	 * Mirror border conditions are applied.
	 */
	static private void doAverage3(double vin[], double vout[]) {
		int n = vin.length;
		vout[0] = (vin[0] + 2.0 * vin[1]) / 3.0;
		for (int k = 1; k < n-1; k++) {
			vout[k] = (vin[k-1] + vin[k] + vin[k+1]) / 3.0;
		}
		vout[n-1] = (vin[n-1] + 2.0 * vin[n-2]) / 3.0;
	}

	/**
	 * Implements an one-dimensional centered difference filter of 
	 * length 3. The filtered value of a pixel is the difference of 
	 * its two neighborhing values.
	 * Mirror border conditions are applied.
	 */
	static private void doDifference3(double vin[], double vout[]) {
		int n = vin.length;
		vout[0] = 0.0;
		for (int k = 1; k < n-1; k++) {
			vout[k] = (vin[k+1] - vin[k-1]) / 2.0;
		}
		vout[n-1] = 0.0;
	}

	/*******************************************************************************
	 *
	 * M O V I N G   A V E R A G E   5 * 5   S E C T I O N
	 *
	 ******************************************************************************/

	static public ImageAccess doMovingAverage5_NonSeparable(ImageAccess input) {
		int maskWidth = 5;
		int nx = input.getWidth();
		int ny = input.getHeight();
		double arr[][] = new double[maskWidth][maskWidth];
		double pixel;
		ImageAccess out = new ImageAccess(nx, ny);

		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				input.getNeighborhood(x, y, arr);
				pixel = 0;
				
				for (int i = 0; i < maskWidth; i++)
					for (int j = 0; j < maskWidth; j++)
						pixel += arr[i][j];
				
				pixel = pixel / (maskWidth * maskWidth);

				out.putPixel(x, y, pixel);
			}
		}
		return out;
	}

	static public ImageAccess doMovingAverage5_Separable(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess out = new ImageAccess(nx, ny);
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];

		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			doAverage5(rowin, rowout);
			out.putRow(y, rowout);
		}
		
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		for (int x = 0; x < nx; x++) {
			out.getColumn(x, colin);
			doAverage5(colin, colout);
			out.putColumn(x, colout);
		}
		return out;
	}

	static private void doAverage5(double vin[], double vout[]) {
		int n = vin.length;
		
		vout[0] = (2*vin[0] + 2*vin[1] + vin[2]) / 5;
		vout[1] = (2*vin[0] + vin[1] + vin[2] + vin[3]) / 5;
		for (int i = 2; i < n - 2; i++) {
			vout[i] = (vin[i - 2] + vin[i - 1] + vin[i] + vin[i + 1] + vin[i + 2]) / 5;
		}
		vout[n - 2] = (vin[n - 4] + vin[n - 3] + vin[n - 2] + 2*vin[n - 1]) / 5;
		vout[n - 1] = (vin[n - 3] + 2*vin[n - 2] + 2*vin[n - 1]) / 5;
	}

	static public ImageAccess doMovingAverage5_Recursive(ImageAccess input) {
		IJ.showMessage("Question 2");
		return input.duplicate();
	}

	/*******************************************************************************
	 *
	 * S O B E L
	 *
	 ******************************************************************************/

	static public ImageAccess doSobel(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();

		double[] sobelx = { 1, 2, 1 };
		double[] sobely = { -1, 0, 1 };

		ImageAccess outx = convolveSeparable(input, sobelx, sobely);
		outx.show("resultado de Gx");
		ImageAccess outy = convolveSeparable(input, sobely, sobelx);
		outy.show("resultado de Gy");
		ImageAccess out = new ImageAccess(nx, ny);

		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				double x = outx.getPixel(i, j);
				double y = outy.getPixel(i, j);
				double result = Math.sqrt(x * x + y * y);

				out.putPixel(i, j, result);
			}
		}

		return out;
	}

	static public ImageAccess convolveSeparable(ImageAccess input, double[] kernelx, double[] kernely) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double rowin[]  = new double[nx];
		double rowout[] = new double[nx];
		double colin[]  = new double[ny];
		double colout[] = new double[ny];
		ImageAccess out = new ImageAccess(nx, ny);

		for (int y = 0; y < ny; y++) {
			input.getRow(y, rowin);
			convolve1DKernel(rowin, rowout, kernely);
			out.putRow(y, rowout);
		}

		for (int x = 0; x < nx; x++) {
			out.getColumn(x, colin);
			convolve1DKernel(colin, colout, kernelx);
			out.putColumn(x, colout);
		}

		return out;
	}

	static public void convolve1DKernel(double[] vin, double[] vout, double[] kernel) {
		int n = vin.length;

		vout[0] = kernel[0]*vin[0] + kernel[1]*vin[0] + kernel[2]*vin[1];
		
		for (int i = 1; i < n-1; i++) {
			vout[i] = kernel[0]*vin[i-1] + kernel[1]*vin[i] + kernel[2]*vin[i+1];
		}
		
		vout[n-1] = kernel[0]*vin[n-2] + kernel[1]*vin[n-1] + kernel[2]*vin[n-1];
	}


	/*******************************************************************************
	 *
	 * M O V I N G   A V E R A G E   L * L   S E C T I O N
	 *
	 ******************************************************************************/

	static public ImageAccess doMovingAverageL_Recursive(ImageAccess input, int length) {
		IJ.showMessage("Question 5");
		return input.duplicate();
	}

}
