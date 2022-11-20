package Marker_Guesser;

import ij.IJ;
import ij.ImagePlus;

/**
 *
 */

public class Averager{

	private int w, h, d;
	private ImagePlus image;
	private byte[][] pixels_in;
	
	
	public int average(ImagePlus image, int x0, int x1, int y0, int y1, int z0, int z1){

		// Determine dimensions of the image
		
		w = image.getWidth(); 
		h = image.getHeight();
		d = image.getStackSize();
		
		int w_s = x1-x0;
		int h_s = y1-y0;
		int d_s = z1-z0;
		pixels_in = new byte[d][];
		for(int z = 0; z < d; z++) {
			pixels_in[z] = (byte[])image.getStack().getPixels(z+1);
		}
		
		// iterate
		int avg = 0;
		for(int z_s = z0; z_s < z1; z_s++) {
			IJ.showProgress(z_s, d_s-1);
			for(int y_s = y0; y_s < y1; y_s++) {
				for(int x_s = x0; x_s < x1; x_s++) {
					if(		x_s>1 && x_s<w && 
							y_s>1 && y_s<h &&
							z_s>1 && z_s<d
					) {
						avg += get(x_s, y_s, z_s);
					}
				}
			}
		}
		int total = w_s*h_s*d_s;
		avg = avg/total;
		
		return avg;
	}

	public int get(int x, int y, int z) {
		x = x < 0 ? 0 : x; x = x >= w ? w-1 : x;
		y = y < 0 ? 0 : y; y = y >= h ? h-1 : y;
		z = z < 0 ? 0 : z; z = z >= d ? d-1 : z;
		return (int)(pixels_in[z][y*w + x] & 0xff);
	}
}
