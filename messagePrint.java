import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class messagePrint{
    String ID = null;
    File header = null;
    String msg = null;
    HashMap<String, String> config = new HashMap<String,String>();
    emotionReady tempER = null;
    Stack<Double> sentimentStack = new Stack<Double>();

    public messagePrint(String owner, emotionReady mainER)
    {
        ID = owner;
        tempER = mainER;

        File sentimentFile = new File("sentiment.txt");
        try
        {
            BufferedReader sentimentFileReader = new BufferedReader(new FileReader(sentimentFile));
            do
            {
                String temp;
                try
                {
                    temp = sentimentFileReader.readLine();
                    sentimentStack.push(Double.parseDouble(temp));
                }
                catch(NullPointerException ne)
                {
                    break;
                }
            }while(true);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        for(double i : sentimentStack)
        {
            System.out.printf("%f  ", i);
        }
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

    public void getNewMessage(String headerFile, String newMessage)
    {
        header = new File(headerFile);
        msg = newMessage;
    }

    public void printMessage()
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
                double recommendedBase = 0.0;
                double weight = -0.70;
                for(int i = 1 ; i <= ((sentimentStack.size() < 10) ? sentimentStack.size() : 10); i++)
                {
                    recommendedBase += weight * sentimentStack.elementAt(sentimentStack.size() - i);
                    weight += 0.07;
                }
                System.out.println("recommended base : " + recommendedBase);

                double sentiment = 0.0;
                List<Double> emotionInMessage = tempER.analyze(msg);
                for(Double i : emotionInMessage)
                {
                    if(!(i < -1.0 || i > 1.0))
                        sentiment += i;
                }
                FileWriter sentimentFileWriter = new FileWriter(new File("sentiment.txt"), true);
                sentimentFileWriter.write(sentiment + "\r\n");
                sentimentStack.push(sentiment);

                System.out.print( msg + "\r\n");

                sentimentFileWriter.close();
                reportFileWriter.write( msg + "\r\n");
            }

            reportFileWriter.close();
        }
        catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
}