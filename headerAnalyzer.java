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