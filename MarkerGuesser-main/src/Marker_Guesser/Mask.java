package Marker_Guesser;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

import java.awt.image.ColorModel;

/*
 * this masks the image, it takes a black and white imagePlus to use to mask a target imagePlus, doesn't return anything, just effects the target passed to it
 */

public class Mask {
	private static ImagePlus img;
	private static ImagePlus targ_img;
	
	public Mask(ImagePlus a_img, ImagePlus a_targ_img) {
		img = a_img;
		targ_img = a_targ_img;
	}
	
	public void mask() {	//later you could add the ability to check what kind of color image it is and accommodate multiple kinds
		for(int x = 1; x < img.getWidth(); x ++) {
			for(int y = 1; y < img.getHeight(); y ++) {
				for(int z = 0; z < img.getStackSize(); z ++) {
							//if you want to go from iterating from 0-stacksize, to the composite image, replace z with z*3+1, 2, or 3
					if(img.getStack().getVoxel(x, y, z)<1) {//the channels are interleaved - channel 1->remainder 0,  channel2->remainder 1, channel3->remainder 2;
						targ_img.getStack().setVoxel(x, y, z*3, 0.0);//channel 1
						targ_img.getStack().setVoxel(x, y, z*3+1, 0.0);//channel 2
						targ_img.getStack().setVoxel(x, y, z*3+2, 0.0);//channel 3
					}
							
				}
			}
		}
	}
}
