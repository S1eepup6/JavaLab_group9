public class stringParser {
	private String dest;
	public stringParser(String d) { dest = d; }
	public void getString(String d)	{ dest = d; }
	public String pureString()		//return only pure string, not with configure information
	{
		int pureStart = dest.indexOf('>');
		if(dest.charAt(0) == '<' && dest.charAt(1) == '/'
				&& pureStart != -1)
		{
			if(dest.charAt(pureStart - 1) != '/') return dest;
			else return dest.substring(pureStart + 1);
		}
		else
			return dest;
	}

	public String configureInfo()			// return only configure information
	{
		String noInfo = "no";
		int configEnd = dest.indexOf('>');
		if(dest.charAt(0) == '<' && dest.charAt(1) == '/'
				&& configEnd != -1)
		{
			if(dest.charAt(configEnd - 1) != '/') return noInfo;
			else return dest.substring(2, configEnd - 1).toLowerCase().trim();
		}
		else
			return noInfo;
	}
}
