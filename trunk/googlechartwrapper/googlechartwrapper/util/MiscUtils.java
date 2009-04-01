package googlechartwrapper.util;

import googlechartwrapper.AbstractChart;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Miscellaneous utility methods for the api.
 * @author mart
 *
 */
public class MiscUtils {
	
	/**
	 * Transforms a color object into an 8 letter hex value string (with transparency).
	 * RRGGBBTT
	 * @param color color to transform
	 * @return 8 letter string
	 */
	public static String getEightCharacterHexValue (Color color){
		return Integer.toHexString(color.getRGB()).substring(2, 8)+
			Integer.toHexString(color.getRGB()).substring(0, 2);
	}
	
	/**
	 * Transform a color object into an 6 letter hex value string without transparency.
	 * @param color color to transform
	 * @return 6 letter string 
	 */
	public static  String getSixCharacterHexValue (Color color){
		//TODO mva: fix bug (when transparency in hex with leading 0)
		return Integer.toHexString(color.getRGB()).substring(2, 8);
	}
	
	public static String getMatchingColorHexValue (Color color){
		if (color.getAlpha()==255){
			return (MiscUtils.getSixCharacterHexValue(color));
		}
		else {
			return (MiscUtils.getEightCharacterHexValue(color));
		}
	}
	
	/**
	 * Takes the chart url to load the image.
	 * 
	 * 
	 * @param chart
	 * 
	 * @return bufferedImage 
	 */
	public static Image getBufferedImage(AbstractChart chart) throws IOException{				
		
		BufferedImage bufferedImage = null;
		
		//we can ensure that the url is correct
		try {
			bufferedImage = ImageIO.read( new URL(chart.getUrl()) );
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} 
		return bufferedImage;
		
	}
	

}
