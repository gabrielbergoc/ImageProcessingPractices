
public class PointwiseTransform extends Object {

	/**
	* Question 2.1 Contrast reversal
	*/
	static public ImageAccess inverse(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess output = new ImageAccess(nx, ny);
		double value = 0.0;
		for (int x=0; x<nx; x++)
		for (int y=0; y<ny; y++) {
			value = input.getPixel(x, y);
			value = 255 - value;
			output.putPixel(x, y, value);
		}
		return output;	
	}

	/**
	* Question 2.2 Stretch normalized constrast
	*/
	static public ImageAccess rescale(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		double max = input.getMaximum();
		double min = input.getMinimum();
		ImageAccess output = new ImageAccess(nx, ny);
		
		double alpha = 255 / (max - min);
		double beta = min;

		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				double pixel = input.getPixel(i, j);
				double newPixel = alpha * (pixel - beta);

				output.putPixel(i, j, newPixel);
			}
		}

		return output;
	}

	/**
	* Question 2.3 Saturate an image
	*/
	static public ImageAccess saturate(ImageAccess input) {
		int nx = input.getWidth();
		int ny = input.getHeight();
		ImageAccess output = new ImageAccess(nx, ny);
		
		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				double pixel = input.getPixel(i, j);
				double newValue = Math.min(pixel, 10000.0);

				output.putPixel(i, j, newValue);
			}
		}

		return rescale(output);
	}
	
	/**
	* Question 4.1 Maximum Intensity Projection
	*/
	static public ImageAccess zprojectMaximum(ImageAccess[] zstack) {
		int nx = zstack[0].getWidth();
		int ny = zstack[0].getHeight();
		int nz = zstack.length;
		ImageAccess output = new ImageAccess(nx, ny);
		
		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				double outputPixel = 0;
				
				for (int k = 0; k < nz; k++) {
					double pixel = zstack[k].getPixel(i, j);

					outputPixel = Math.max(pixel, outputPixel);
				}

				output.putPixel(i, j, outputPixel);
			}
		}

		return output;	
	}

	/**
	* Question 4.2 Z-stack mean
	*/
	static public ImageAccess zprojectMean(ImageAccess[] zstack) {
		int nx = zstack[0].getWidth();
		int ny = zstack[0].getHeight();
		int nz = zstack.length;
		ImageAccess output = new ImageAccess(nx, ny);
		
		for (int i = 0; i < nx; i++) {
			for (int j = 0; j < ny; j++) {
				double sum = 0;
				
				for (int k = 0; k < nz; k++) {
					double pixel = zstack[k].getPixel(i, j);

					sum += pixel;
				}

				output.putPixel(i, j, sum / nz);
			}
		}

		return output;	
	}

	static public int[] histogram(ImageAccess img, int nIntensityValues) {
		return histogram(img, nIntensityValues, 256);
	}

	static public int[] histogram(ImageAccess img, int nIntensityValues, int bins) {
		int[] hist = new int[bins];

		for (double pixel : img.getPixels()) {
			int bin = getBin(pixel, bins, nIntensityValues);
			hist[bin]++;
		}

		return hist;
	}

	static public int[] cumulativeHistogram(ImageAccess img, int nIntensityValues) {
		return cumulativeHistogram(img, nIntensityValues, 256);
	}

	static public int[] cumulativeHistogram(ImageAccess img, int nIntensityValues, int bins) {
		int[] hist = histogram(img, nIntensityValues, bins);
		int[] acc = new int[bins];

		acc[0] = hist[0];
		for (int i = 0; i < bins; i++) {
			acc[i] = acc[i - 1] + hist[i];
		}

		return acc;
	}

	static public ImageAccess equalize(ImageAccess img) {
		int nIntensityValues = 256;
		int bins = 256;
		int[] cummulativeHist = cumulativeHistogram(img, nIntensityValues, bins);
		int nx = img.getWidth();
		int ny = img.getHeight();
		int n = nx * ny;
		ImageAccess out = new ImageAccess(nx, ny);

		for (int x = 0; x < nx; x++) {
			for (int y = 0; y < ny; y++) {
				double pixel = img.getPixel(x, y);
				int bin = getBin(pixel, bins, nIntensityValues);
				double newPixel = cummulativeHist[bin] * (nIntensityValues - 1) / n;
				img.putPixel(x, y, newPixel);
			}
		}

		return out;
	}

	static public int getBin(double pixel, int bins, int nIntensityValues) {
		return (int)Math.round(pixel * bins / nIntensityValues);
	}
}
