package Marker_Guesser;

import ij.IJ;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.ImagePlus;
import ij.CompositeImage;
import ij.io.Opener;
import ij.io.OpenDialog;
import ij.ImageJ;
import ij.*;
import ij.Macro;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ij.plugin.*;

import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;

//This class reads the results .csv file and places markers in an objectJ project. Each instance of this class will place markers for each channel


public class ObjJ_Markers {
	private double[][] m_transform = new double[4][4]; //? transform matrix
	private String m_results_path;
	private String m_marker_name;
	private int m_marker_index;
	
	public ObjJ_Markers(double[][] transform, String results_path, String marker_name, int marker_index) {
		m_transform = transform;
		m_results_path = results_path;
		m_marker_name = marker_name;
		m_marker_index = marker_index;
	}
	
	public void run() {
		//you might have to pass the file directory in with the other arguments
		String macro = 
				"args = getArgument();\n"+
						"arg_arr = split(args, \",\");\n"+
						"filestring=File.openAsString(arg_arr[0]);\n"+
						"rows=split(filestring, \"\\n\");\n"+
						"x = 0; \r\n" + 
						"y = 0; \r\n" + 
						"z = 0;\r\n" + 
						"x_col = 0;\r\n" + 
						"y_col = 0;\r\n" + 
						"z_col = 0;"+
						"header=split(rows[0],\",\"); //finds the x, y, z, column indexes from the first row\r\n" + 
						"for(i = 0; i < header.length; i ++){\r\n" + 
						"	if(header[i]==\"X\"){\r\n" + 
						"		x_col = i;\r\n" + 
						"	}else if(header[i]==\"Y\"){\r\n" + 
						"		y_col = i;\r\n" + 
						"	}else if(header[i]==\"Z\"){\r\n" + 
						"		z_col = i;\r\n" + 
						"	}\r\n" + 
						"}\r\n" + 
						"\r\n" + 
						"//placing the markers\r\n" + 
						"name=arg_arr[1];\r\n" + 
						"channel=arg_arr[2];\r\n" + 
						"print(name);\r\n"+
						"ojShowImage(1);\r\n" + 
						"for(i=1; i<rows.length; i++){\r\n" + 
						"	columns=split(rows[i],\",\");\r\n" + 
						"	x = round(columns[x_col]);\r\n" + //*((a0*x)+(b0*y)+(c0*z)+(d0) - afterward, make new variables for x, y, z
						"	y = round(columns[y_col]);\r\n" + 
						"	z = round(columns[z_col]);\r\n" + //20-7 40-14
						"	print(x+\",\"+y+\",\"+z+\",\"+channel);\r\n" + 
						"ojShowImage(1);\r\n" + 
						"	Stack.setPosition(channel, z, 1);" +
						"	ojSwitchToItem(name);\r\n" + 
						"	ojSetMarker(x, y);\r\n" + 
						"	updateResults();"+
						"} \r\n" + 
						"\r\n";
						
		Macro_Runner mr1 = new Macro_Runner();
		mr1.runMacro(macro, m_results_path+","+m_marker_name+","+m_marker_index);
	}
}
