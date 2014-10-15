import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.Overlay;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.plugin.filter.MaximumFinder;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

import javax.swing.*;

import Jama.Matrix;
import by.golovataya.imageProcessing.pca.MathUtil;


@SuppressWarnings("serial")
public class TrainingInterface extends JFrame {
	private ImageProcessor ip;
	private int[] xCoordinates;
	private int[] yCoordinates;
	private ImageCanvas canvas;
	private int count;
	private double relativeArea;
	private double areaOfObject;
	private double areaOfTube;
	final ImagePlus _ip;
	boolean reflectedLightSelection = false;
	ArrayList <Integer> xOfReflections = new ArrayList<Integer>();
	ArrayList <Integer> yOfReflections = new ArrayList<Integer>();
	ArrayList <Wand> wands = new ArrayList<Wand>();
	private int[] xOfRefl;
	private int[] yOfRefl;
	double toleranceReflection = 10;
	int toleranceMaxima = 100;
	
	
	
	public TrainingInterface(final ImagePlus ip){
		
		  try {
		        UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		    } 
		  catch (Exception ex) {}
		
		_ip = ip;
		canvas = ip.getWindow().getCanvas();
		xCoordinates = new int[6];
		yCoordinates = new int[6];
		count = 0;
		relativeArea = 0;
		areaOfObject = 0;
		areaOfTube = 0;
		
		this.ip = ip.getProcessor();
		this.setSize(400, 200);
		
		
		final JTextField relativeAreaTextField = new JTextField("0");
		relativeAreaTextField.setPreferredSize(new Dimension (10,5));
		relativeAreaTextField.setMinimumSize(new Dimension (10,5));
		relativeAreaTextField.setEditable(false);
		JButton relativeAreaButton = new JButton("Relative Area");
		final JTextField areaOfObjectTextField = new JTextField("0");
		areaOfObjectTextField.setPreferredSize(new Dimension (10,5));
		areaOfObjectTextField.setMinimumSize(new Dimension (10,5));
		areaOfObjectTextField.setEditable(false);
		JButton getAreaOfObjectButton = new JButton("Area of Object");
		final JTextField areaOfTubeTextField = new JTextField("0");
		areaOfTubeTextField.setPreferredSize(new Dimension (10,5));
		areaOfTubeTextField.setMinimumSize(new Dimension (10,5));
		areaOfTubeTextField.setEditable(false);
		JButton getAreaOfTubeButton = new JButton("Area of Tube");
		
		final JLabel pixelLabel = new JLabel("pixels");
		final JLabel pixelLabel1 = new JLabel("pixels");
		final JLabel percentLabel = new JLabel("%");
		
		
		
		
		JPanel panel1 = new JPanel();
		this.setContentPane(panel1);
		
		panel1.setLayout(new BorderLayout());
		
		Box boxMain = Box.createVerticalBox();
		panel1.add(boxMain);
		boxMain.add(Box.createVerticalStrut(20));
		Box boxArea = Box.createVerticalBox();
		boxMain.add(boxArea);
		Box boxAreaOfObject = Box.createHorizontalBox();
		Box boxAreaOfTube = Box.createHorizontalBox();
		Box boxRelativeArea = Box.createHorizontalBox();
		boxArea.add(boxAreaOfObject);
		boxArea.add(Box.createVerticalStrut(20));
		boxArea.add(boxAreaOfTube);
		boxArea.add(Box.createVerticalStrut(20));
		boxArea.add(boxRelativeArea);
		boxAreaOfObject.add(Box.createHorizontalStrut(20));
		boxAreaOfObject.add(getAreaOfObjectButton);
		boxAreaOfObject.add(Box.createHorizontalStrut(20));
		boxAreaOfObject.add(Box.createHorizontalGlue());
		boxAreaOfObject.add(Box.createHorizontalStrut(20));
		boxAreaOfObject.add(areaOfObjectTextField);
		boxAreaOfObject.add(Box.createHorizontalGlue());
		boxAreaOfObject.add(Box.createHorizontalStrut(20));
		boxAreaOfObject.add(pixelLabel1);
		boxAreaOfObject.add(Box.createHorizontalStrut(20));
		boxAreaOfTube.add(Box.createHorizontalStrut(20));
		boxAreaOfTube.add(getAreaOfTubeButton);
		boxAreaOfTube.add(Box.createHorizontalStrut(20));
		boxAreaOfTube.add(Box.createHorizontalGlue());
		boxAreaOfTube.add(Box.createHorizontalStrut(20));
		boxAreaOfTube.add(areaOfTubeTextField);
		boxAreaOfTube.add(Box.createHorizontalGlue());
		boxAreaOfTube.add(Box.createHorizontalStrut(20));
		boxAreaOfTube.add(pixelLabel);
		boxAreaOfTube.add(Box.createHorizontalStrut(20));
		boxRelativeArea.add(Box.createHorizontalStrut(20));
		boxRelativeArea.add(relativeAreaButton);
		boxRelativeArea.add(Box.createHorizontalStrut(20));
		boxRelativeArea.add(Box.createHorizontalGlue());
		boxRelativeArea.add(Box.createHorizontalStrut(20));
		boxRelativeArea.add(relativeAreaTextField);
		boxRelativeArea.add(Box.createHorizontalGlue());
		boxRelativeArea.add(Box.createHorizontalStrut(20));
		boxRelativeArea.add(percentLabel);
		boxRelativeArea.add(Box.createHorizontalStrut(20));
		boxMain.add(Box.createVerticalStrut(20));
				
		relativeAreaButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (areaOfTube!=0) relativeArea = 100*areaOfObject/areaOfTube;
				DecimalFormat df = new DecimalFormat("#.##");
				relativeAreaTextField.setText(String.valueOf(df.format(relativeArea)));
			}
			
		});
		getAreaOfObjectButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				areaOfObject = calculateArea(_ip);
				DecimalFormat df = new DecimalFormat("#.##");
				areaOfObjectTextField.setText(String.valueOf(df.format(areaOfObject)));
				//DecimalFormat df = new DecimalFormat("#.##");
				//ImageStatistics stats = _ip.getStatistics();
		         //IJ.log("Median: "+stats.area);
				areaOfObjectTextField.setText(String.valueOf(df.format(areaOfObject)));
			}
			
		});
		
		getAreaOfTubeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				areaOfTube = calculateArea(_ip);
				DecimalFormat df = new DecimalFormat("#.##");
				areaOfTubeTextField.setText(String.valueOf(df.format(areaOfTube)));
			}
			
		});
		
		JButton pcaButton = new JButton("PCA");
		pcaButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				principalComponentAnalysis(ip);
				
			}
			
		});
		boxMain.add(pcaButton);
		
		
		canvas.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				//if (reflectedLightSelection == true) {
				//	getReflections(arg0);
				//	reflectedLightSelection = false;
				//}
				//else 
					
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	
	private void principalComponentAnalysis(ImagePlus ip) {
		ImageProcessor p = ip.getProcessor();

		if (!(p instanceof ColorProcessor)) {
			System.out.println("Processor is not color processor");
			return;
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
		
		for (FloatProcessor principal : principals) {
			ImagePlus image = new ImagePlus();
			image.setProcessor(principal);
			image.show();
		}
		
	}
	
	private double calculateArea(ImagePlus ip){
		double area = 0;
		Roi selection = ip.getRoi();
		if (selection!=null)
		area = Math.PI*selection.getBounds().getWidth()*selection.getBounds().getHeight();
		return area;
		}
		
	
}
