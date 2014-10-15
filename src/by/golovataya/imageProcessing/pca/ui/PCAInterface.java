package by.golovataya.imageProcessing.pca.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import Jama.Matrix;
import by.golovataya.imageProcessing.pca.ImageAnalyzer;


@SuppressWarnings("serial")
public class PCAInterface extends JFrame {
	final ImagePlus ip;
	
	private double sliderAlpha;
	private double sliderBeta;
	private int sliderF;
	
	private Matrix transform;
	private Matrix pcaData;
	
	private ByteProcessor mainComponent;
	
	public PCAInterface(final ImagePlus ip, final ImageAnalyzer analysis){
		
		  try {
		        UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		    } 
		  catch (Exception ex) {}
		
		this.ip = ip;
		
		this.setSize(600, 600);
		
		JPanel panel1 = new JPanel();
		this.setContentPane(panel1);
		
		panel1.setLayout(new BorderLayout());
		
		
		JButton pcaButton = new JButton("PCA");
		pcaButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				transform = analysis.Analyze(ip);
				pcaData = analysis.getTransformedData();
			}
			
		});
		
		JButton filterButton = new JButton("Filter");
		filterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ApplyFiltering();
			}
			
		});
		JButton reverseButton = new JButton("Reverse transform");
		reverseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Enhance();
			}
			
		});
		
		
		Box mainBox = Box.createVerticalBox();
		
		Box alphaBox = Box.createHorizontalBox();
		alphaBox.add(new JLabel("alpha: "));
		final JLabel alphaVal = new JLabel("0");
		final JSlider alphaSlider = new JSlider(0);
		alphaSlider.setMinimum(0);
		alphaSlider.setMaximum(500);
		alphaSlider.setValue(0);
		alphaBox.add(alphaVal);
		alphaBox.add(alphaSlider);

		
		Box betaBox = Box.createHorizontalBox();
		betaBox.add(new JLabel("beta: "));
		final JLabel betaVal = new JLabel("0");
		final JSlider betaSlider = new JSlider();
		betaSlider.setValue(0);
		betaSlider.setMinimum(0);
		betaSlider.setMaximum(500);
		betaBox.add(betaVal);
		betaBox.add(betaSlider);
		
		Box fBox = Box.createHorizontalBox();
		fBox.add(new JLabel("F0: "));
		final JLabel fVal = new JLabel("0");
		final JSlider fSlider = new JSlider();
		fSlider.setValue(0);
		fSlider.setMinimum(0);
		fSlider.setMaximum(255);
		fBox.add(fVal);
		fBox.add(fSlider);

		
		Box buttonsBox = Box.createHorizontalBox();
		
		buttonsBox.add(pcaButton);
		buttonsBox.add(filterButton);
		buttonsBox.add(reverseButton);
		
		XYDataset data = chartData(sliderAlpha, sliderBeta);
		JFreeChart chart = ChartFactory.createXYLineChart("Easing function", "x", "y", data, PlotOrientation.VERTICAL, true, true, false);
		
		final ChartPanel chartPanel = new ChartPanel(chart);
		mainBox.add(chartPanel);
		
		mainBox.add(alphaBox);
		mainBox.add(betaBox);
		mainBox.add(fBox);
		
		mainBox.add(buttonsBox);
		
		panel1.add(mainBox);
		
		ChangeListener valueChangeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				sliderAlpha = (double) alphaSlider.getValue() / 15;
				sliderBeta = (double) betaSlider.getValue() / 30000;
				sliderF = fSlider.getValue();
				
				alphaVal.setText(String.format("%.1f", sliderAlpha));
				betaVal.setText(String.format("%.4f", sliderBeta));
				fVal.setText(Integer.toString(sliderF));
				
				updateChart(chartPanel);
			}
			
		};
		
		alphaSlider.addChangeListener(valueChangeListener);
		betaSlider.addChangeListener(valueChangeListener);
		fSlider.addChangeListener(valueChangeListener);
	}
	
	private void updateChart(ChartPanel panel) {
		XYDataset data = chartData(sliderAlpha, sliderBeta);
		JFreeChart chart = ChartFactory.createXYLineChart("Easing function", "x", "y", data, PlotOrientation.VERTICAL, true, true, false);
		panel.setChart(chart);
	}
	
	public XYDataset chartData(double alpha, double beta) {
        DefaultXYDataset ds = new DefaultXYDataset();
        
        double[][] data = new double[2][];
        
        double xfrom = -30;
        double xto = 30;
        double step = 0.01;
        
        int count = (int) Math.round(((xto - xfrom) / step)+ 1); 
        
        data[0] = new double[count];
        data[1] = new double[count];
        
        
        int k = 0;
        
        for (double x = xfrom; x <= xto + step; x += step) {
        	data[0][k] = x;
        	data[1][k] = easingFunction(x, 0, alpha, beta);
        	k++;
        }
        
        ds.addSeries("easing function", data);
 
        double[][] identity = {
    		{xfrom, xto},
    		{xfrom, xto}
        };
        
        ds.addSeries("identity transform", identity);
        
        return ds;
	}
	
	public double easingFunction(double F, double F0, double alpha, double beta) {
		return F * (1 + alpha * Math.exp(- Math.pow((F - F0), 2) * beta));
	}

	public void ApplyFiltering() {

		ImagePlus mainComponentImp = IJ.getImage();
		
		if (mainComponent == null) {
			mainComponent = (ByteProcessor) mainComponentImp.getProcessor().duplicate().convertToByte(true);
		}
		
		ImageProcessor duplicate = mainComponent.duplicate();
		
		
		for (int i=0; i<duplicate.getPixelCount(); i++) {
			duplicate.set(i, (int) easingFunction(mainComponent.get(i), sliderF, sliderAlpha, sliderBeta));
		}
		
		mainComponentImp.setProcessor(duplicate);
	}

	public void ReverseTransform() {
		int principalComponent = 1;
		
		Matrix newData = pcaData.copy();
		
		for (int i=0; i<newData.getRowDimension(); i++) {
			newData.set(i, principalComponent, mainComponent.get(i));
		}
		
		Matrix transformedData = newData.times(transform.inverse());
		
		ColorProcessor cp = (ColorProcessor) ip.getProcessor().duplicate();
		int k = 0;
		for (int i=0; i<cp.getWidth(); i++) {
			for (int j=0; j<cp.getHeight(); j++) {
				int r = (int) transformedData.get(k, 0);
				int g = (int) transformedData.get(k, 1);
				int b = (int) transformedData.get(k, 2);
				
				cp.putPixel(i, j, new int[] {r,g,b});
				k++;
			}
		}
		
		ImagePlus imp = new ImagePlus();
		imp.setTitle("result");
		imp.setProcessor(cp);
		imp.show();
	}

	public void Enhance() {
		ColorProcessor cp = (ColorProcessor) ip.getProcessor().duplicate();

		byte[] r = new byte[cp.getPixelCount()];
		byte[] g = new byte[cp.getPixelCount()];
		byte[] b = new byte[cp.getPixelCount()];
		
		cp.getRGB(r, g, b);
		
		for (int i=0; i<cp.getPixelCount(); i++) {
			int darkness = (int) (40 * Math.exp(-mainComponent.get(i) * 4 / 255));
			r[i] = (byte) ((int)r[i] + darkness > 127 ? 127 : r[i] + darkness);
			g[i] = (byte) ((int)g[i] + darkness > 127 ? 127 : g[i] + darkness);
			b[i] = (byte) ((int)b[i] + darkness > 127 ? 127 : b[i] + darkness);
		}
		
		cp.setRGB(r, g, b);
		
		ImagePlus imp = new ImagePlus();
		imp.setTitle("result");
		imp.setProcessor(cp);
		imp.show();
	}
}
