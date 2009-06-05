package de.toolforge.googlechartwrapper;


import java.awt.Dimension;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.toolforge.googlechartwrapper.util.AppendableFeature;
import de.toolforge.googlechartwrapper.util.ArrayUtils;
import de.toolforge.googlechartwrapper.util.IExtendedFeatureAppender;

/**
 * The AbstractChart is the basechart for most charts and supports some basic
 * features as the dimension. Additionally the url generating part is done
 * by this class by using a reflection mechanism in {@link #collectUrlElements()} and 
 * {@link #collectUrlElements(List)}. Each implementing subclass shall use
 * {@link IExtendedFeatureAppender} to collect the chart parameters. These appenders 
 * are gathered by the reflection mechanism by default and added to the url, separated
 * by the default {@value #AMPERSAND_SEPARATOR}.
 * 
 * @author steffan
 * @author martin
 */
public abstract class AbstractChart implements IChart {

	/**
	 * Default Chart API's location of the google service.
	 */
	public static final String GOOGLE_API = "http://chart.apis.google.com/chart?";
	
	/**
	 * default separator for parameters.
	 */
	public static final String AMPERSAND_SEPARATOR = "&";
	protected Queue<String> urlElements = new LinkedList<String>();
	
	protected Dimension chartDimension;
	
	/**
	 * Generates an AbstractChart with the given chartDimension.
	 * @param chartDimension size of the chart in pixel
	 * 
	 * @throws IllegalArgumentException if chartDimension is {@code null}
	 * @throws IllegalArgumentException if height > 1000 and/or weight > 1000
	 * @throws IllegalArgumentException if area is > 300000
	 */
	public AbstractChart(Dimension chartDimension) {
		
		if(chartDimension == null)
			throw new IllegalArgumentException("chartDimension can not be null");
		if(chartDimension.getHeight() > 1000 || chartDimension.getWidth() > 1000)
			throw new IllegalArgumentException("height and/or width can not be > 1000");
		if((chartDimension.getHeight()*chartDimension.getWidth()) > 300000)
			throw new IllegalArgumentException("the largest possible area can not be > 300000");
		this.chartDimension = chartDimension;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see googlechartwrapper.Chart#getUrl()
	 */
	public String getUrl(){
		collectUrlElements(getAllAppenders());
		return generateUrlString(GOOGLE_API);
	}
	
	/**
	 * Returns the generated chart url with the given Chart API's location. 
	 * The base URL is simply added ahead of the generated chart parameters
	 * by the {@link IExtendedFeatureAppender}. 
	 * <p>The following format is used:
	 * &lt;base URL&gt;&lt;other url parameters&gt;. <br>
	 * For example: 
	 * http://chart.apis.google.com/chart?cht=s&chs=300x300&chd=e:DICW|BkHC|DIEs <br>
	 * where http://chart.apis.google.com/chart? is the apiLocation.
	 * @param apiLocation the Chart API's location 
	 * @return generated chart string
	 * @see #GOOGLE_API
	 */
	public String getUrl (String apiLocation){
		collectUrlElements(getAllAppenders());
		return generateUrlString(apiLocation);
	}	
	
	/**
	 * Returns the chart type which is appended to the URL. 
	 * @return representing string for a chart type  
	 */
	protected abstract String getUrlChartType();

	/**
	 * Returns the chart type of the chart.
	 * @return chart type of the chart.
	 */
	protected abstract ChartType getChartType();
	
	/**
	 * Returns all appenders from the extending class, but not inherited fields. It
	 * requires that these appenders implement {@link IExtendedFeatureAppender} and
	 * the fields are public or protected. If subclass fields are necessary this method
	 * must be overwritten. 
	 * 
	 * It's recommended that this method is overwritten as it uses reflection which may
	 * not be safe in all environments.
	 * @return list of all appenders
	 */ 
	protected List<IExtendedFeatureAppender> getAllAppenders(){
		List<IExtendedFeatureAppender> allExtendedFeatureAppenders = 
			new ArrayList<IExtendedFeatureAppender>(5); 
		
		List<Field> fields = new ArrayList<Field>(); //every field (appenders)
		fields.addAll(Arrays.asList(this.getClass().getDeclaredFields()));
		Class<?> current = this.getClass().getSuperclass(); //to deal with inheritance
		while (current.getSuperclass()!= null){
			fields.addAll(Arrays.asList(current.getDeclaredFields()));
			current = current.getSuperclass();
		}
		//Field[] fields = this.getClass().getDeclaredFields(); //alle Felder
		
		for (Field f: fields){
			if (ArrayUtils.linearSearch(f.getType().getInterfaces(), IExtendedFeatureAppender.class)>=0){
				//if field implements the IExtendedFeatureAppender - so e.g. a genericAppender
				try { 
					
					allExtendedFeatureAppenders.add((IExtendedFeatureAppender)f.get(this));
					//der Liste hinzufügen, und zwar das feld aus der aktuellen instanz					
				} 
				catch (IllegalArgumentException e) {
					throw new RuntimeException(e); //todo mva: think about this!
				} 
				catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return allExtendedFeatureAppenders;
	}

	/**
	 * Collects all base url elements: chart type and chart dimension.
	 */
	protected void collectUrlElements() {
		urlElements.clear();
		urlElements.offer(MessageFormat.format("cht={0}", this
				.getUrlChartType()));
		urlElements.offer(MessageFormat.format("chs={0}x{1}",
				this.chartDimension.width, this.chartDimension.height));
				
	}

	protected void collectUrlElements(List<IExtendedFeatureAppender> appenders) {
		collectUrlElements(); //alle Grundelemente laden
		Map<String, FeatureAppender> m = 
			new HashMap<String, FeatureAppender>();
			//new HashMap<String, FeatureAppender<IExtendedFeatureAppender>>();
		//map fuer key=featureprefixstring (z.b. chm) 
		//value=Appender für alle von diesem Typen
		
		for (IExtendedFeatureAppender ap : appenders) {
			List<AppendableFeature> ft = ap.getAppendableFeatures(appenders);
			for (AppendableFeature feature : ft){
				if (m.containsKey(feature.getPrefix())){
					m.get(feature.getPrefix()).add(feature);
				}
				else { 
					//ansonsten muss neuer appender für diesen feature typ angelegt werden
					FeatureAppender fa = new FeatureAppender(
							feature.getPrefix());
					fa.add(feature);
					m.put(fa.getPrefix(),fa);
				}
			}			
		}
		
		List<FeatureAppender> values = new ArrayList<FeatureAppender>(m.values());
		
		Collections.sort(values, new Comparator<FeatureAppender>(){
			public int compare(FeatureAppender arg0, 
					FeatureAppender arg1) {
				return arg0.prefix.compareTo(arg1.prefix);
			}			
		}); //for unittests
		
		for (FeatureAppender ap : values) {
			
			urlElements.offer(ap.getUrlString());
		}
	}

	protected String generateUrlString(String baseUrl) {
		StringBuilder url = new StringBuilder();
		url.append(baseUrl); //Standardpfad zur API
		url.append(urlElements.poll());//charttype anhängen

		while (urlElements.size() > 0) {
			//solange noch etwas drin, an die url mit dem Trennzeichen & anhängen
			String urlElem = urlElements.poll();
			if (urlElem.length()>0){
				url.append(AMPERSAND_SEPARATOR + urlElem);
			}			 
		}
		return url.toString();
	}

	private static class FeatureAppender{

		/**
		 * list of elements/features
		 */
		protected List<AppendableFeature> list;
		/**
		 * type of the parameter: &lt;type&gt;=&lt;parameter data&gt;, e.g
		 * chs=250x100
		 */
		protected String prefix;
		/**
		 * separator for each single feature, e.g. the pipe symbol or a comma
		 */
		protected String separator;
		
		public FeatureAppender(String stm) {
			this(stm,"|");
		}
		
		public FeatureAppender (String m, String separator){
			if (separator == null){
				throw new IllegalArgumentException("sep cannot be null");
			}
			list = new ArrayList<AppendableFeature>();
			prefix = m;
			this.separator = separator;
		}
		
		public String getPrefix (){
			return prefix;
		}
		
		public String getUrlString (){
			 List<AppendableFeature> features = list;
			 StringBuilder bf = new StringBuilder();
			 for (AppendableFeature f:features){
				 bf.append(f.getData());
				 bf.append(separator);
			 }
			 if (bf.length() > 0){
				 if (prefix.equals("")){
					 return  bf.substring(0,bf.length()-1);
				 }
				 else {
					 return prefix +"="+ bf.substring(0,bf.length()-1);
				 }
			 }
			return "";
		}
		
		public void add (AppendableFeature m){
			list.add(m);
		}
	}
}
