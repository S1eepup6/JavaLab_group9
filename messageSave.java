import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;

class messageSave {
    File messageFile = null;

    String fileName = "";

    public messageSave(String __chatRoomName) {
        fileName = __chatRoomName + ".txt";
        messageFile = new File(fileName);
    }

    public void save(String sender, String msg) {
        try {
            FileWriter messageWriter = new FileWriter(messageFile, true);
            messageWriter.append("["+ sender + "] " + msg + "\n");

            messageWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load()
    {
        try
        {
            BufferedReader messageReader = new BufferedReader(new FileReader(messageFile));
            String line= "";
            while(line != null)
            {
                try
                {
                    line = messageReader.readLine();
                    if(line == null)
                        break;
                    System.out.println(line);
                }
                catch(NullPointerException e)
                {
                    break;
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}