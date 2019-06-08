import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

class emotionReady
{
    File emotionFile = null;
    HashMap<String, Double> emotion = new HashMap<String, Double>();

    public emotionReady(String path)
    {
        emotionFile = new File(path);

        try
        {
            BufferedReader emotionFileReader = new BufferedReader(new FileReader(emotionFile));
            do
            {
                try
                {
                    String line = emotionFileReader.readLine().trim();
                    if(line == null)
                        break;
                    if(line.charAt(0) == '#')
                        continue;

                    String vals[] = line.split("\\s+");

                    emotion.put(vals[0], Double.parseDouble(vals[1]));
                }
                catch(IOException e)
                {
                    break;
                }
                catch(NullPointerException ne)
                {
                    break;
                }

            }while(true);
        }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
    }

    public List<Double> analyze(String rawMessage)
    {
        ArrayList<Double> sentenceEmo = new ArrayList<Double>();
        String words[] = rawMessage.trim().split("\\s+");

        int idx = 0;
        while(idx < words.length)
        {
            words[idx] = words[idx].replace("[-+.^:,]", "");
            if(emotion.get(words[idx]) == null)
                sentenceEmo.add(-2.0);
            else
                sentenceEmo.add(emotion.get(words[idx]));
            idx++;
        }

        return sentenceEmo;
    }
}