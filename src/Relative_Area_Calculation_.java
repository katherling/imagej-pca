import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;


public class Relative_Area_Calculation_ implements PlugInFilter {

	private ImagePlus imp;
	
	@Override
	public void run(ImageProcessor ip) {
		
		TrainingInterface ti = new TrainingInterface(imp);
		ti.setVisible(true);
		
		
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		
		return DOES_RGB;
	}
	
	
}








