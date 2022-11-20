package Marker_Guesser;


import ij.plugin.Duplicator;
import ij.plugin.Macro_Runner;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.Macro;
import ij.Prefs;
import ij.gui.*;





public class MarkerGuesser implements PlugIn {
	public String dir_path;
	public String[] marker_names;
	public int numImages;
	public int dilate_iter;
	public int dilate_z_iter;
	public void run(String arg) {
		Macro_Runner m1 = new Macro_Runner();
		m1.runMacro("ojShowImage(1);run(\"Duplicate...\", \"duplicate\");", "");
		ImagePlus imageStack1 = WindowManager.getCurrentImage();
		
		//String lis = new Macro_Runner().runMacro("lis = call(\"ij.Prefs.get\", \"pre.thresh_bias\", \"this is wrong\"); showMessage(lis); return lis", "");
		//IJ.error(lis);
		//int lis2 = Integer.valueOf(lis);
		//boolean save_steps = Options.saveSteps();
		numImages = Options.getNImages();
		marker_names = Options.getMarkersforChannels(imageStack1);


		
		dilate_iter = Options.getXYDilateIter();
		dilate_z_iter = Options.getZDilateIter();
		boolean save_steps = false;
		Mask_and_Filter filter_model = new Mask_and_Filter(imageStack1, dilate_iter, dilate_z_iter, save_steps);
		
		imageStack1 = filter_model.run("model", marker_names);
		//future - Add warning if the directory path is different for second imageStack
		dir_path = filter_model.getDirPath();	
		
		
		
		//temporary
		double[][] transform = {
				{1,0,0,0},
				{0,1,0,0},
				{0,0,1,0},
				{0,0,0,1}
		};

		
		
		String results_path;
		ObjJ_Markers om_model;
		int results_index = 0;
		
		
		
		for(int i = 0; i < marker_names.length; i ++) {	
			if(!marker_names[i].contentEquals("cellFill")) {	
				results_path = filter_model.getResultsDir()[results_index];
				om_model = new ObjJ_Markers(transform, results_path, marker_names[i], i+1);
				om_model.run();
				results_index++;
			}
		}
		
		new Macro_Runner().runMacro(""
				+ "ojShowImage(1);\n" + 
				"nums = \"\";\n" + 
				"for(i = ojFirstObject(1); i <= ojLastObject(1); i ++){\n" + 
				"	ojSelectObject(i);\n" + 
				"	//ojQualify(i, false);\n" + 
				"	id = ojIndexToId(i);\n" + 
				"	nums += \"\"+id+\",\";	\n" + 
				"}\n" + 
				"call(\"ij.Prefs.set\", \"pre.val0\", nums);\n" + 
				"call(\"ij.Prefs.set\", \"pre.qual_tog\", true);", "");
	}
}
