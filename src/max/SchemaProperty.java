package max;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchemaProperty {
	
	public boolean required = false; 
	public Class<?> type = Object.class;
	public double min = Double.NEGATIVE_INFINITY;
	public double max = Double.POSITIVE_INFINITY;
	public int minlength = 0;
	public int maxlength = Integer.MAX_VALUE;
	public String matches = null;
	public Object defaultValue = null;
	
	public SchemaProperty() {
	}
	protected void out(Object e) {
		System.out.println(e);
	}
	
	public SchemaValidationResult validate(Object obj, String key) {
		boolean validateType = type.isInstance(obj);
		boolean validatedLimits = true;
		boolean validatedLength = true;
		boolean validatedPattern = true;
		
		if(!validateType) return new SchemaValidationResult(key, false, "A " + obj.getClass().getName() + " object was provided, but a " + type.getName() + " was expected. ");
		if(obj instanceof Number) {
			// Validamos cosas de números.
			double val = ((Number) obj).doubleValue();
			validatedLimits = (val >= min) && (val <= max);
				if(!validatedLimits) 
					return new SchemaValidationResult(key, false, "The number is out of the allowed range, which is " + min + "-" + max + ". ");
		}
		if(obj instanceof String) {
			String s = (String) obj;
			validatedLength = (s.length() >= minlength) && (s.length() <= maxlength);
			if(matches != null || matches == "") {
				Pattern p = Pattern.compile(matches);
				Matcher m = p.matcher(s);
				validatedPattern = m.matches();
			}		
			if(!validatedLength) return new SchemaValidationResult(key, false, "The string's length is out of the allowed range. ");
			if(!validatedPattern) return new SchemaValidationResult(key, false, "The string does not match the regular expression. ");
			
		}
		return new SchemaValidationResult(key, (
			validateType 
			&& validatedLimits 
			&& validatedLength 
			&& validatedPattern
		), "");
	}
	
	

}
