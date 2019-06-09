import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

class emotionReady
{
    String chatRoom;
    File emotionFile = null;
    String chatRoomName;
    HashMap<String, Double> emotion = new HashMap<String, Double>();
    Stack<Double> sentimentStack = new Stack<Double>();
    public emotionReady()
    {
    	chatRoomName = null;
        emotionFile = new File("emotion.txt");
    }
    public void init(String chatRoomName)
    {
    	this.chatRoomName = chatRoomName;
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
        chatRoom = chatRoomName;
        File sentimentFile = new File(chatRoom + "_sentiment.txt");
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
    }
    public double analyze(String rawMessage)
    {
        ArrayList<Double> sentenceEmo = new ArrayList<Double>();
        double recommendedBase = 0.0;
        try
        {
            double weight = 0.70;
            for(int i = 1 ; i <= ((sentimentStack.size() < 10) ? sentimentStack.size() : 10); i++)
            {
                recommendedBase += weight * sentimentStack.elementAt(sentimentStack.size() - i);
                weight -= 0.07;
            }
            double sentiment = 0.0;

            //calculate value for recommendation.

            String words[] = rawMessage.trim().split("\\s+");
            int idx = 0;
            while(idx < words.length)
            {
                words[idx] = words[idx].replace("[-+^:,]", "");
                if(emotion.get(words[idx]) == null)
                    sentenceEmo.add(-2.0);
                else
                    sentenceEmo.add(emotion.get(words[idx]));
                idx++;
            }
            for(Double i : sentenceEmo)
            {
                if(!(i < -1.0 || i > 1.0))
                    sentiment += i;
            }
            FileWriter sentimentFileWriter = new FileWriter(new File(chatRoom + "_sentiment.txt"), true);
            sentimentFileWriter.write(sentiment + "\r\n");
            sentimentFileWriter.close();
            sentimentStack.push(sentiment);
            //calculate emotion value for sentence and push it to stack and file
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recommendedBase;
    }
}