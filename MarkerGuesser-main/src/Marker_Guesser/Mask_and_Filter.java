package Marker_Guesser;

import ij.process.ImageProcessor;
import ij.io.Opener;
import ij.io.FileSaver;
import ij.io.OpenDialog;
import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;



public class Mask_and_Filter{
	private String m_dir_path;//the path for the project folder
	private String type; //scene or model
	private int dilate_iter = 20;
	private int dilate_z_iter = 15;
	private boolean save_steps = false;
	private ImagePlus targ_img;
	private String[] marker_names;
	private String[] results_dir = new String[10]; //results directory is an array of strings - each string in the array is the directory for each channel results file - max 10 channels
	
	public Mask_and_Filter(ImagePlus atarget, int a_dilate_iter, int a_dilate_z_iter, boolean a_save_steps) {
		targ_img = atarget;
		dilate_iter = a_dilate_iter;
		dilate_z_iter = a_dilate_z_iter;
		save_steps = a_save_steps;
	}
	
	public ImagePlus run(String ptype, String[] a_marker_names) {//the functino run returns the masked imagePlus//set type is scene or model
		type = ptype;
		
		ImagePlus img = createMask();
		
		marker_names = a_marker_names;
		
		ImagePlus targ_img = mask(img);
		
		targ_img = filter(targ_img, img);
		
		return targ_img;
	}
	
	private ImagePlus filter(ImagePlus targ_img, ImagePlus img) {
		ImagePlus[] channels = ChannelSplitter.split(targ_img);
		
		
		IJ.run("Set 3D Measurements", "nb_of_obj._voxels centroid dots_size=10 font_size=15 store_results_within_a_table_named_after_the_image_(macro_friendly) redirect_to=none");
		//pass in the correct number of channels
		int j = 0;//for results directory
		for(int i = 0; i < 3; i ++) {
			if(!marker_names[i].contentEquals("cellFill")) {
				results_dir[j] = m_dir_path+"chan"+String.valueOf(i+1)+"_"+type+".csv";
				
				
				Prefs.blackBackground = true;
				channels[i].show();
				
				
				//contrast enhance
				String cont_sat = getOption("pre.cont_sat");//gets the option value from the IJ prefs
				String arg = "saturated="+cont_sat;
				IJ.run(channels[i], "Enhance Contrast", "saturated="+cont_sat);
				IJ.run(channels[i], "Apply LUT", "stack");

				//background subtraction
				String ball_rad = getOption("pre.ball_rad");
				IJ.run(channels[i], "Subtract Background...", "rolling="+ball_rad+" stack");

				//auto local threshold
				String thresh_rad = getOption("pre.thresh_rad");
				String thresh_bias = getOption("pre.thresh_bias");
				IJ.run(channels[i], "Auto Local Threshold", "method=Median radius="+thresh_rad+" parameter_1="+thresh_bias+" parameter_2=0 white stack");
				IJ.run(channels[i], "Make Binary", "method=Triangle background=Dark calculate black");

				
				String min_obj = getOption("pre.min_obj");
				String max_obj = getOption("pre.max_obj");
				channels[i].show();
				String macro = 	
					"path = getArgument();"+
					"name = getTitle();\n" + 
					"name = replace(name, \".tif\", \"\");\n" + 
					"run(\"3D Watershed Split\", \"binary=\"+name+\" seeds=Automatic radius=2\");\n"+
					"selectWindow(\"Split\");\n"+
					"run(\"8-bit\");\n"+
					"run(\"Make Binary\", \"method=Triangle background=Dark calculate black\");"+
					
					"run(\"3D object counter...\", \"threshold=128 slice=15 min.="+min_obj+" max.="+max_obj+" exclude_objects_on_edges statistics\");\n"+
					"saveAs(\"results\", path);\n"+
					"close(\"Split\");"+
					"close(\"EDT\");"+
					"close(\"MAILLY\");";
				
				Macro_Runner mr1 = new Macro_Runner();
				mr1.runMacro(macro, results_dir[j]);
				channels[i].changes = false;
				channels[i].close();
			
			
				j++;
			}
			
		}
		/*
		RGBStackMerge merger = new RGBStackMerge();	//merges the channels back together
		targ_img = merger.mergeHyperstacks(channels, true);
		targ_img.show();
		*/
		for(int i = 0; i < 3; i ++) { //closes the individual channel imgs
			channels[i].changes = false;
			channels[i].close();
		}
		
		img.changes = false;//says no changes have been made so it can close quietly
		img.close();

		return targ_img;
	}
	
	private ImagePlus createMask() {
		//TRACE FILE
		//img is the trace file
		ImagePlus img = open("Open trace file", false); //this returns an ImagePlus
		IJ.run(img, "8-bit", "");
		Dilator dilator1 = new Dilator();
		for(int d = 0; d < dilate_iter; d ++) {
			boolean dilateZ = (d<dilate_z_iter); //only dilates in the z direction if d < dilate_z_iter
			img = dilator1.dilate(img, 20, dilateZ, false);//dilates the trace image making it thicker
		}
		img.show();
		IJ.run(img, "8-bit", "");
		return img;
	}
	
	private ImagePlus mask(ImagePlus img) {
		//TARGET FILE
		//ImagePlus targ_img = open("Open target file", false);//you could use CompositeImage in the future
		IJ.run(targ_img, "8-bit", "");
		targ_img.show();
		
		IJ.error("applying mask and filtering - may take a while");
		
		Mask mask1 = new Mask(img, targ_img);//applies the mask to target image - leaves first channel unchanged
		mask1.mask();
		return targ_img;
	}
	
	private String getOption(String option) {
		String val_str = new Macro_Runner().runMacro("lis = call(\"ij.Prefs.get\", \""+option+"\", \"Could not access preferences value\"); return lis", "");
		return val_str;
	}
	
	private ImagePlus open(String text, boolean composite) {
		IJ.error(text);
		OpenDialog od = new OpenDialog(text, "");	
		m_dir_path = od.getDirectory();
		String path = m_dir_path + od.getFileName();
		
		if (od.getFileName() == null) {	
			return null;
		}
		
		ImagePlus img = IJ.openImage(path); //this returns an ImagePlus
		if(composite==true) {
			CompositeImage c_img = new CompositeImage(img);
			return c_img;
		}else {
			return img;
		}
	}
	
	public String getDirPath() {
		return m_dir_path;
	}
	
	public String[] getResultsDir() {
		return results_dir;
	}
}



