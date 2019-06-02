import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

class messagePrint{
    String ID = null;
    File header = null;
    String msg = null;
    HashMap<String, String> config = new HashMap<String,String>();

    public static final String ANSI_RESET = "\033[0m";
    public static final String ANSI_BLACK = (char)27 + "[30m";
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_GREEN = (char)27 + "[32m";
    public static final String ANSI_YELLOW = (char)27 + "[33m";
    public static final String ANSI_BLUE = (char)27 + "[34m";
    public static final String ANSI_PURPLE = (char)27 + "[35m";
    public static final String ANSI_CYAN = (char)27 + "[36m";
    public static final String ANSI_WHITE = (char)27 + "[37m";

    public messagePrint(String filename, String message, String owner)
    {
        header = new File(filename);
        msg = message;
        ID = owner;
    }

    private void analyze()
    {
        try
        {
            FileReader headReader = new FileReader(header);
            String headInfo = "";
            int singleCh = 0;
            while((singleCh = headReader.read()) != -1){
                headInfo += (char)singleCh;
            }       //read header file
            headReader.close();
            int braceStart = 0;
            int braceEnd = 0;
            String property = "";
            String content = "";
            while(headInfo.indexOf('{', braceEnd) != -1)
            {
                braceStart = headInfo.indexOf('{',braceEnd);
                property = headInfo.substring(((braceEnd != 0 ) ? braceEnd + 1 : braceEnd), braceStart).trim();

                braceEnd = headInfo.indexOf('}', braceStart);
                content = headInfo.substring(braceStart + 1, braceEnd).trim();

                config.put(property, content);
            }
        }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void printMSG()
    {
        analyze();
        if(!ID.equals(config.get("receiver")))
        {
            return;
        }
        System.out.println("Sender : " + config.get("sender"));

        if(!config.get("filetype").equals("MESSAGE"))
            System.out.println("FILE sended");
        else
        {
            System.out.print( msg );
        }
    }
}