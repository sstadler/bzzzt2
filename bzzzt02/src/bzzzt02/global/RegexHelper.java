package bzzzt02.global;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
	
	public static boolean regexMatch(String testVal, String regex){
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(testVal);
		return matcher.matches();
	}
}
