import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class headerAnalyzer{
    File header = null;
    public headerAnalyzer(String filename)
    {
        header = new File(filename);
    }

    public void analyze()
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

                System.out.println(property);
                System.out.println(content);
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
        //if property related with configuration, call function associated.
        //if not, convert it to useful information.
    }
}