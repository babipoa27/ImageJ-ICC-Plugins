// Crop_Plugin.java
//
// ImageJ Plugin to Crop all images in a given folder to a sub-image with the greatest variance
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
import java.util.*;




public class Crop_Plugin implements PlugIn {

	// Taken from: http://stackoverflow.com/questions/13553703/how-i-got-all-of-text-files-in-directory-and-sub-directory
    private ArrayList<File> arList = new ArrayList<File>();
    public void find(String s) {
        File f = new File(s);
        File[] fArr = f.listFiles();

        for (File x : fArr) {

            if (x.isFile()) {
                if (x.getName().endsWith(".tif") || x.getName().endsWith(".TIF") ) {
                    arList.add(x);
                }
            }
            else {
                find(x.getAbsolutePath());
            }
        }

    }
	
	// Main function for program. Loads folder directories and initiates process	
	public void run(String arg) 
	{
		String pathIn  = IJ.getDirectory("Select Input Directory");
		String pathOut = IJ.getDirectory("Select Output Directory");
		
		while (pathIn == pathOut) //check against saving in the same directory 
								  //and erasing files
		{
			IJ.log("pick another output directory");
			pathOut = IJ.getDirectory("Select Output Directory");
		}
		
		find(pathIn);
		runOnPath(pathIn, pathOut);
	}
	

	
	// Crops all the images in a given path into a new folder
	public void runOnPath(String inputFolder, String outputFolder)
	{
		
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles(); 
				
		Opener openFile = new Opener();
		
		for (File file : arList)
		{
			String fileName = file.getName().toLowerCase(); // to make the below if{} 
															// statement easier
			if (fileName.contains(".tif"))
			{
				openFile.open(file.getPath());
				handleImage(file.getName(), outputFolder);
			}
		}
	}
	
	// Handle's each individual image
	public void handleImage(String fileName, String outputFolder)
	{	
		IJ.run("Duplicate...", "title=NewStack.tif duplicate");

		ImagePlus imp = IJ.getImage();
		Roi m = getHighThresholdRoi(imp);

		imp.changes = false;
		imp.close();
		
		imp = IJ.getImage(); //changes the image back 
		imp.setRoi(m);
		IJ.run("Crop");

		outputFolder = outputFolder + fileName;
		IJ.saveAsTiff(imp, outputFolder);
		
		imp.changes = false;
		imp.close();
	}
	
	
	// finds ROI (region of interest) of specified size with greatest threshold
	// returns that ROI
	public Roi getHighThresholdRoi(ImagePlus imp)
	{
		IJ.run("Variance...", "radius=30"); // Uses a fixed radiance of variance for analysis
		ImageProcessor ip = imp.getProcessor();
		int size_factor = 500;

		int width = size_factor;
		int height = size_factor;
		int minThresh = 500, minThreshX = 0, minThreshY = 0; 
		int maxThresh = 0, maxThreshX = 0, maxThreshY = 0;
		int m = 0;
			
		for (int x = 0; x <= ip.getWidth() - size_factor; x+=50)
		{
			for (int y = 0; y < ip.getHeight() - size_factor; y+=50)
			{
				IJ.makeRectangle(x,y,width,height);
				m = ip.getAutoThreshold();

				if (m > maxThresh)
				{
					maxThresh = m;
					maxThreshX = x;
					maxThreshY = y;
				}
			}
		}
		return new Roi(maxThreshX,maxThreshY,size_factor,size_factor);
	}
	
}
