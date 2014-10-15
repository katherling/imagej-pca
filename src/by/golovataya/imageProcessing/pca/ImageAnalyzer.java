package by.golovataya.imageProcessing.pca;

import Jama.Matrix;
import ij.ImagePlus;

public interface ImageAnalyzer {
	public Matrix Analyze(ImagePlus ip);
	public Matrix getTransformedData();
}
