package by.golovataya.imageProcessing.pca;

import Jama.Matrix;


public class MathUtil {
	private MathUtil() {}
	
	public static void Normalize(Matrix data) {
		
		if (data == null) {
			throw new IllegalArgumentException("data");
		}

		double[] sums = new double[data.getColumnDimension()];
		
		for (int i=0; i<data.getRowDimension(); i++) {			
			for (int j=0; j<data.getColumnDimension(); j++) {
				sums[j] += data.get(i,j);
			}
		}
		
		double[] means = new double[data.getColumnDimension()];
		for (int i=0; i<data.getColumnDimension(); i++) {
			means[i] = sums[i] / data.getRowDimension();
		}
		
		double[] sigmas = new double[data.getColumnDimension()];
		for (int i=0; i<data.getRowDimension(); i++) {
			for (int j=0; j<data.getColumnDimension(); j++) {
				sigmas[j] += Math.abs(data.get(i,j) - means[j]);
			}
		}
		
		for (int i=0; i<sigmas.length; i++) {
			sigmas[i] /= data.getRowDimension();
			sigmas[i] = Math.sqrt(sigmas[i]);
		}
		
		for (int i=0; i<data.getRowDimension(); i++) {
			for (int j=0; j<data.getColumnDimension(); j++) {
				data.set(i, j, 
						(data.get(i, j) - means[j]) / sigmas[j]);
			}
		}
	}
	
	public static void Denormalize(Matrix data, double upper) {
		double[] mins = new double[data.getColumnDimension()];
		
		for (int i=0; i<data.getRowDimension(); i++) {			
			for (int j=0; j<data.getColumnDimension(); j++) {
				double value = data.get(i,j);
				
				if (i == 0) {
					mins[j] = value;
				}
				else if (value < mins[j]) {
					mins[j] = value;
				}
			}
		}
		
		for (int i=0; i<data.getRowDimension(); i++) {
			for (int j=0; j<data.getColumnDimension(); j++) {
				data.set(i, j, data.get(i, j) - mins[j]);
			}
		}

		double[] maxes = new double[data.getColumnDimension()];
		
		// начало костыля
		
		double max = maxes[0];
		for (int i=1; i<maxes.length; i++) {
			if (max < maxes[i]) {
				max = maxes[i];
			}
		}
		
		for (int i=0; i<maxes.length; i++) {
			maxes[i] = max;
		}
		// конец костыля
		
		for (int i=0; i<data.getRowDimension(); i++) {
			for (int j=0; j<data.getColumnDimension(); j++) {
				double value = data.get(i,j);
				
				if (i == 0) {
					maxes[j] = value;
				}
				else if (value > maxes[j]) {
					maxes[j] = value;
				}
			}
		}
		
		
		for (int i=0; i<data.getRowDimension(); i++) {
			for (int j=0; j<data.getColumnDimension(); j++) {
				data.set(i, j, data.get(i, j) * upper / maxes[j]);
			}
		}
		
	}
	
	public static float[][] MatrixTranspone(float[][] m) {
		if (m == null) {
			throw new IllegalArgumentException("m");
		}
		
		if (m.length == 0) {
			return m;
		}
		
		int height = m.length;
		int width = m[0].length;
		float[][] result = new float[width][];
		for (int i=0; i<width; i++) {
			result[i] = new float[height];
		}
		
		for (int i=0; i<height; i++) {
			if (m[i].length != width) {
				throw new IllegalArgumentException("m");
			}
			
			for (int j=0; j<width; j++) {
				result[j][i] = m[i][j];
			}
		}
		
		
		return result;
	}
	
	public static float[][] MatrixMultipy(float[][] m1, float[][] m2) {
		
		int m1height = m1.length;
		int m2height = m2.length;
		
		if (m1 == null || m1height == 0) {
			throw new IllegalArgumentException("m1");
		}
		
		if (m2 == null || m2height == 0) {
			throw new IllegalArgumentException("m2");
		}
		
		int m1width = m1[0].length;
		int m2width = m2[0].length;
		
		for (int i=0; i<m1.length; i++) {
			if (m1[i].length != m1width) {
				throw new IllegalArgumentException("m1");
			}
		}
			
		for (int i=0; i<m2.length; i++) {
			if (m2[i].length != m2width) {
				throw new IllegalArgumentException("m2");
			}
		}
		
		if (m1width != m2height) {
			throw new IllegalArgumentException("1st matrix width isn't equal to 2nd matrix height");
		}
		
		int n = m1width;// = m2height
		
		float[][] result = new float[m1height][];
		for (int i=0; i<m1height; i++) {
			result[i] = new float[m2width];
		}
		
		for (int i=0; i<m1height; i++) {
			for (int j=0; j<m2width; j++) {
				for (int k=0; k<n;k++) {
					result[i][j] += m1[i][k] * m2[k][j];
				}
			}
		}
		
		return result;
	}

}
