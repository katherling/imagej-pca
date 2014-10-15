package by.golovataya.imageProcessing.pca;

import Jama.Matrix;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class ImageAnalysis implements ImageAnalyzer {

	private Matrix transformedData;
	
	public Matrix Analyze(ImagePlus ip) {
		ImageProcessor p = ip.getProcessor();

		if (!(p instanceof ColorProcessor)) {
			System.out.println("Processor is not color processor");
			return null;
		}
		
		ColorProcessor processor = (ColorProcessor) p;
		int pointCount = processor.getWidth() * processor.getHeight();

		ByteProcessor[] channels = {
				processor.getChannel(1, null),
				processor.getChannel(2, null),
				processor.getChannel(3, null)
		};
		
		Matrix sourceMatrix = new Matrix(pointCount, 3);
		int k = 0;
		
		for (int i = 0; i < processor.getWidth(); i++) {
			for (int j = 0; j < processor.getHeight(); j++) {
				for (int c=0; c<3; c++) {
					sourceMatrix.set(k, c, channels[c].get(i,j));
				}
				
				k++;
			}
		}
		
		Matrix normalized = sourceMatrix.copy();
		MathUtil.Normalize(normalized);
		
		Matrix transp = normalized.copy().transpose();
		Matrix covariance = transp.times(normalized);
		double multiple = 1 / ((double) pointCount - 1);
		covariance = covariance.times(multiple);
		// matrix of weight values, acting as transformation matrix between RGB and principal component colorspace
		Matrix transform = covariance.eig().getV();	
		
		Matrix pcaData = normalized.times(transform);
		MathUtil.Denormalize(pcaData, 255);
		transformedData = pcaData;
		FloatProcessor[] principals = {
				channels[0].toFloat(0, null),
				channels[0].toFloat(0, null),
				channels[0].toFloat(0, null)
		};
		
		k = 0;
		for (int i=0; i<channels[0].getWidth(); i++) {
			for (int j=0; j<channels[0].getHeight(); j++) {
				for (int c=0; c<principals.length; c++) {
					principals[c].putPixelValue(i, j, pcaData.get(k, c));
				}
				k++;
			}
		}
		
		k = 0;
		for (FloatProcessor principal : principals) {
			ImagePlus image = new ImagePlus();
			image.setTitle("PCA " + Integer.toString(k));
			image.setProcessor(principal);
			image.show();
			k++;
		}
		
		return transform;
	}
	
	public Matrix getTransformedData() {
		return transformedData;
	}

}
