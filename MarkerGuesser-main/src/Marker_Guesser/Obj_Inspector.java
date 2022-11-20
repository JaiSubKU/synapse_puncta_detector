package Marker_Guesser;

import ij.IJ;
import ij.plugin.Macro_Runner;
import ij.plugin.PlugIn;
import ij.WindowManager;
import ij.ImagePlus;

public class Obj_Inspector implements PlugIn {
	public void run(String arg) {
		int size = Options.getSizeofMarker();
		String macro = "arg = getArgument();//size, objNum\r\n" + 
				"arg = split(arg, \",\");\r\n" + 
				"size = parseInt(arg[0]);\r\n" + 
				"obj_num = parseInt(arg[1]);\r\n" + 
				"\r\n" + 
				"ojSelectObject(obj_num);\r\n" + 
				"\r\n" + 
				"x = ojXPos(1);\r\n" + 
				"y = ojYPos(1);\r\n" + 
				"z = ojZPos(1);\r\n" + 
				
				"\r\n" + 
				"outstring = \"\"+x+\",\"+y+\",\"+z+\"\";\r\n"+
				"\r\n" + 
				"return outstring;";
		Macro_Runner mr1 = new Macro_Runner();
		String macro_arg = ""+size+","+1+"";
		String position = mr1.runMacro(macro, macro_arg);
		
		String[] position_arr = position.split(",");
		
		int x = Integer.parseInt(position_arr[0]);
		int y = Integer.parseInt(position_arr[1]);
		int z = Integer.parseInt(position_arr[2]);
		Averager avg = new Averager();
		
		mr1.runMacro("ojShowImage(1);"
				+ "run(\"Duplicate...\", \"duplicate\");"
				+ "run(\"8-bit\");", 
				"");
		ImagePlus imageStack1 = WindowManager.getCurrentImage();
		
		int avg_value = avg.average(imageStack1, x-(size/2), x+(size/2), y-(size/2), y+(size/2), z+(size/2), z-(size/2));
		IJ.error(""+avg_value);
		//IJ.error(""+x+","+y+","+z);
	}
}
