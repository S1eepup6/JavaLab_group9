import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class headerGenerator {		//can only make configuration func
	String message;
	String configInfo;
	filetype fileType;
	long sender;
	long receiver;
	public headerGenerator(long _sender, long _receiver, filetype _fileType, String _configInfo)
	{
		fileType = _fileType;
		configInfo = _configInfo;
		sender = _sender;
		receiver = _receiver;
	}

	public void refresh(long _sender, long _receiver, filetype _fileType, String _configInfo)
	{
		fileType = _fileType;
		configInfo = _configInfo;
		sender = _sender;
		receiver = _receiver;
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

	public String makeAsJSON(String property, String arg1, String arg2)
	{
		String result = property + '{' + arg1 + ',' + arg2 + '}';
		result.toLowerCase();
		result += '\n';
		return result;
	}

	public String makeAsJSON(String property, String arg1)
	{
		String result = property + '{' + arg1 + '}';
		result.toLowerCase();
		result += '\n';
		return result;
	}

	public String makeAsJSON(String property, long arg1, long arg2)
	{
		String result = property + '{' + arg1 + ',' + arg2 + '}';
		result.toLowerCase();
		result += '\n';
		return result;
	}
	public String makeAsJSON(String property, long arg1)
	{
		String result = property + '{' + arg1 + '}';
		result.toLowerCase();
		result += '\n';
		return result;
	}
	public String makeAsJSON(String property, filetype arg1)
	{
		String result = property + '{' + arg1 + '}';
		result.toLowerCase();
		result += '\n';
		return result;
	}

	public void makeJSONfile()
	{
		//headerfile order
		//sender, receiver, fileType, configure(if exists)
		try
		{
			File headerFile = new File("header.txt");
			FileWriter headerWriter = new FileWriter(headerFile);
			headerWriter.write(makeAsJSON("sender", sender));
			headerWriter.write(makeAsJSON("receiver", receiver));
			headerWriter.write(makeAsJSON("filetype", fileType));
			headerWriter.write(makeAsJSON(configProperty(),configValue()));

			headerWriter.close();
		}
		catch(FileNotFoundException fe)
		{
			fe.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
