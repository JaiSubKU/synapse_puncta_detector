package Marker_Guesser;

import ij.plugin.Duplicator;
import ij.plugin.Macro_Runner;
import ij.plugin.PlugIn;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.Macro;
import ij.gui.*;

public class Options {
	public int celFChan = 0;//cellFill channel
	
	public static int getNImages() {	
		String macro = 	"num = ojNImages();\n	print(num);\n	text = \"\"+num;\n	return text;";
		Macro_Runner mr1 = new Macro_Runner();
		String numImages = mr1.runMacro(macro, "");
		int nImages = Integer.parseInt(numImages);
		return nImages;
	}
	
	public static boolean saveSteps() {	
		GenericDialog d1 = new GenericDialog("saving steps");
		
		YesNoCancelDialog dl = new YesNoCancelDialog(WindowManager.getFrame("saving steps"), "","Save intermediate steps as images?");
		return dl.yesPressed();
	}
	
	public static int getSizeofMarker() {	
		String macro = "diam = getNumber(\"diameter of area around object: \", 15);\n"+
				"string_out = \"\"+diam;\r\n" + 
				"return string_out;";
		Macro_Runner mr1 = new Macro_Runner();
		String diameter_string = mr1.runMacro(macro, "");
		int diam = Integer.parseInt(diameter_string);
		return diam;
	}
	
	public static int getXYDilateIter() {
		String macro = 
				"radius = getNumber(\"Enter xy radius: \", 20);\n" + 
				"string_out = \"\"+radius;\n" + 
				"return string_out;";
		Macro_Runner mr1 = new Macro_Runner();
		String dilateIter_string = mr1.runMacro(macro, "");
		int dilateIter = Integer.parseInt(dilateIter_string);
		return dilateIter;
	}
	public static int getZDilateIter() {
		String macro = 
				"dilateZ = getNumber(\"Enter z radius: \", 10);\n" + 
				"string_out = \"\"+dilateZ;\n" + 
				"return string_out;";
		Macro_Runner mr1 = new Macro_Runner();
		String dilateIter_string = mr1.runMacro(macro, "");
		int dilateIter = Integer.parseInt(dilateIter_string);
		return dilateIter;
	}
	
	public static String[] getMarkersforChannels(ImagePlus img) {
		int numChannels = img.getNChannels();
		String[] marker_names = new String[numChannels];
		
		for(int i = 0; i < numChannels; i ++) {
			String macro = 	"names = ojGetItemNames();\n	"
					+ "indv_names = split(names, \" \");\n"
					+"indv_names = Array.concat(indv_names, \'cellFill\');\n"
					+ "Dialog.create(\"Channel to marker assignment\");\r\n" + 
					"  Dialog.addRadioButtonGroup(\"Choose a marker for channel \"+"+(i+1)+", indv_names, 3, 1, indv_names[0]);\r\n" + 
					"  Dialog.show;\r\n" + 
					"  return Dialog.getRadioButton;";
			Macro_Runner mr1 = new Macro_Runner();
			marker_names[i] = mr1.runMacro(macro, "");
			//cellFill will always be the last 
		}
		return marker_names;
	}
}
