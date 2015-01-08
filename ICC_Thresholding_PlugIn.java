// ICC_Thresholding_Plugin.java
//
// ImageJ Plugin to Analyze the amount of ImmunoCytoChemistry staining in a directory of results
// Used to analyze ImmunoCytoChemistry results at the HarborView Medical Center during the summer of 2014
//
// BY: YOTAM BENTOV
//

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.io.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import ij.ImagePlus.*;

public class ICC_Thresholding_PlugIn implements PlugIn {
	
	//
	// main plugin method; called upon initiation
	// get input and output directories
	//
	public void run(String arg) 
	{
		guiManager();


	}
	
	//
	// if I find the time this will become the main gui interface for the program
	//
	public void guiManager()
	{
		String pathIn  = IJ.getDirectory("Select Input Directory");
		String pathOut = IJ.getDirectory("Select Output Directory");
		
		
		runOnPath(pathIn, pathOut);
	}
	
	//
	// manages the measurment over all the images in a given directory
	//
	public void runOnPath(String inputFolder, String outputFolder)
	{
		
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles(); 		
		Opener openFile = new Opener();
		
		for (File file : listOfFiles)
		{
			String fileName = file.getName().toLowerCase(); // parsing for easier comparison below
			if (fileName.contains(".tif"))
			{
				openFile.open(file.getPath());
				handleImage();
			}
		}
		
	}
	
	// Takes in an image, and preforms measurment on it 
	public void handleImage()
	{
		ImagePlus imp = IJ.getImage();
		setContrastMeasure(imp);
		imp.changes = false;
		imp.close();
	}
	
	
	// Adjusts contrast and measures pixel area precentage for a given image imp
	public void setContrastMeasure(ImagePlus imp)
	{
		double t = 165.0;
		
		IJ.setMinAndMax(imp, t, t);
		IJ.run("RGB Stack");
		
		IJ.setSlice(2);	
		IJ.setAutoThreshold(imp, "Default");
		IJ.run("Set Measurements...", "area area_fraction limit display redirect=None decimal=3");
		IJ.run("Measure"); 
	}

}
