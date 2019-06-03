import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

class messagePrint{
    String ID = null;
    File header = null;
    String msg = null;
    HashMap<String, String> config = new HashMap<String,String>();
    emotionReady tempER = null;

    public messagePrint(String filename, String message, String owner, emotionReady mainER)
    {
        header = new File(filename);
        msg = message;
        ID = owner;
        tempER = mainER;
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
        File reportFile = new File("record.txt");
        try
        {
            FileWriter reportFileWriter = new FileWriter(reportFile, true);
            analyze();
            if(!ID.equals(config.get("receiver")))
            {
                return;
            }
            System.out.println("Sender : " + config.get("sender"));
            reportFileWriter.write("Sender : " + config.get("sender") + "\r\n");

            if(!config.get("filetype").equals("MESSAGE"))
            {
                System.out.println("FILE sent");
                reportFileWriter.write("FILE sent"+ "\r\n");
            }
            else
            {
                System.out.print( msg );
                reportFileWriter.write( msg + "\r\n");
                List<Double> emotionInMessage = tempER.analyze(msg);
                for(Double i : emotionInMessage)
                {
                    reportFileWriter.write(i+";");
                }
                reportFileWriter.write("\r\n");
            }

            reportFileWriter.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
}