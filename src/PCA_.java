import by.golovataya.imageProcessing.pca.ImageAnalysis;
import by.golovataya.imageProcessing.pca.ui.PCAInterface;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class PCA_ implements PlugInFilter {

	private ImagePlus imp;
	
	@Override
	public void run(ImageProcessor ip) {
		PCAInterface i = new PCAInterface(imp, new ImageAnalysis());
		i.setVisible(true);
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		
		return DOES_RGB;
	}
	
}

