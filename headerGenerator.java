package proj;
import java.util.*;

public class headerGenerator {		//can only make configuration func
	String configInfo;
	String fileType;
	long sender;
	long receiver;
	public headerGenerator(long s, long r, String f, String c)
	{
		fileType = f;
		configInfo = c;
		sender = s;
		receiver = r;
	}

	public String configProperty()
	{
		String errmsg = "Error";
		if(configInfo.contentEquals("no") == true)
			return configInfo;
		if(configInfo.indexOf(':') == -1)
			return errmsg;

		return configInfo.substring(0, configInfo.indexOf(':')).trim();
	}

	public String configValue()
	{
		String errmsg = "Error";
		if(configInfo.contentEquals("no") == true)
			return configInfo;

		if(configInfo.indexOf(':') == -1)
			return errmsg;

		return configInfo.substring(configInfo.indexOf(':') + 1).trim();
	}

	public void makeAsJSON(String property, String arg1, String arg2)
	{
		String result = property + '{' + arg1 + ',' + arg2 + '}';
		result.toLowerCase();
		System.out.println(result);
	}

	public void makeAsJSON(String property, String arg1)
	{
		String result = property + '{' + arg1 + '}';
		result.toLowerCase();
		System.out.println(result);
	}

	public void makeAsJSON(String property, long arg1, long arg2)
	{
		String result = property + '{' + arg1 + ',' + arg2 + '}';
		result.toLowerCase();
		System.out.println(result);
	}
	public void makeAsJSON(String property, long arg1)
	{
		String result = property + '{' + arg1 + '}';
		result.toLowerCase();
		System.out.println(result);
	}
}
