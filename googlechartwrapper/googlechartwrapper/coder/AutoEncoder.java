package googlechartwrapper.coder;

import java.util.List;

/**
 * The default encoder implementation switching between the different 
 * encoder variants (for example {@link SimpleEncoder}, {@link ExtendedEncoder}) 
 * based on the recommendation of {@link EncoderFactory#getSuggestedEncodingType(int[])}.
 * As a result the length of the datastring in the url is usually minimized by
 * this encoder. In regards to the implementation of the transformation of 
 * data into an encoded string the documentation of these encoders
 * should be consulted. 
 * @author steffan
 * @author martin
 * @see EncoderFactory
 */
public class AutoEncoder extends AbstractEncoder implements IEncoder {	

	/**
	 * The recommended encodingtype (lowest possible) by the {@link EncoderFactory}
	 */
	private EncodingType encodingType = null;
	/**
	 * is a single value array encoded or more datasets (-> list)
	 */
	private boolean isCollection = false;
	

	public String encode(int[] values) {
		if (encodingType == null){
			encodingType = EncoderFactory.getSuggestedEncodingType(values);	
		}
		IEncoder encoder = EncoderFactory.getEncoder(encodingType);
		
		String result = encoder.encode(values);
				
		if (!isCollection){
			encodingType = null;
		}
		return result;
	}

	public String encode(float[] values) {
		if (encodingType == null){
			encodingType = EncoderFactory.getSuggestedEncodingType(values);	
		}
		IEncoder encoder = EncoderFactory.getEncoder(encodingType);
		
		String result = encoder.encode(values);
		
		if (!isCollection){
			encodingType = null;
		}
		return result;
	}

	@Override
	public String encodeFloatCollection(List<float[]> values, String sep) {
		EncodingType highest = EncodingType.SimpleEncoding;
		for (int i = 0; i < values.size(); i++){
			EncodingType temp = EncoderFactory.getSuggestedEncodingType(values.get(i));	
			if(temp.getRank() > highest.getRank()){
				highest = temp;
			}			
		}
		encodingType = highest;
		isCollection = true;
		
		String s = super.encodeFloatCollection(values, sep);
		
		encodingType = null;
		isCollection = false;
		return s;
	}
	
	@Override
	public String encodeIntegerCollection(List<int[]> values, String sep) {
		EncodingType highest = EncodingType.SimpleEncoding;
		for (int i = 0; i < values.size(); i++){
			EncodingType temp = EncoderFactory.getSuggestedEncodingType(values.get(i));	
			if(temp.getRank() > highest.getRank()){
				highest = temp;
			}	
		}
		encodingType = highest;
		isCollection = true;
		
		String s = super.encodeIntegerCollection(values, sep);
		
		encodingType = null;
		isCollection = false;
		return  s;
	}

	/**
	 * Does nothing currently.
	 * @param values data to encode
	 * @return <code>null</code>
	 */
	@Override
	protected String collectionEncode(float[] values) {
		return null; //TODO mva: think about implementation (should there be one?)
	}

	@Override
	protected String collectionEncode(int[] values) {
		return null;
	}
}
