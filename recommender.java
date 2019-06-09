import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class recommender {
    public ArrayList<String> negativeSentence = new ArrayList<String>();
    public ArrayList<String> positiveSentence = new ArrayList<String>();
    public ArrayList<String> neutralSentence = new ArrayList<String>();
    Scanner s = new Scanner(System.in);
    
    public recommender() {
        File negative = new File("Negative.txt");
        File neutral = new File("Neutral.txt");
        File positive = new File("Positive.txt");
        try
        {
            BufferedReader negReader = new BufferedReader(new FileReader(negative));
            BufferedReader neuReader = new BufferedReader(new FileReader(neutral));
            BufferedReader posReader = new BufferedReader(new FileReader(positive));

            String line = "";
            while(line != null)
            {
                try
                {
                    line = negReader.readLine().trim();
                    negativeSentence.add(line);
                }
                catch(Exception e)
                {
                    break;
                }
            }

            line = "";
            while(line != null)
            {
                try
                {
                    line = neuReader.readLine().trim();
                    neutralSentence.add(line);
                }
                catch(Exception e)
                {
                    break;
                }
            }

            line = "";
            while(line != null)
            {
                try
                {
                    line = posReader.readLine().trim();
                    positiveSentence.add(line);
                }
                catch(Exception e)
                {
                    break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String recommend(double base)
    {
        Random random = new Random();
        String result = "say|Recommended Reply|\n";
        
        if(base < -0.25)        //negative
        {
            for(int i = 1 ; i <= 4; i++)
            {
                int temp = random.nextInt(negativeSentence.size());
                result.concat(negativeSentence.get(temp));
                result.concat("\n");
            }
        }
        else if(base > 0.25)    //positive
        {
            for(int i = 1 ; i <= 4; i++)
            {
                int temp = random.nextInt(positiveSentence.size());
                result.concat(positiveSentence.get(temp));
                result.concat("\n");            
            }
        }
        else                    //neutral
        {
            for(int i = 1 ; i <= 4; i++)
            {
                int temp = random.nextInt(neutralSentence.size());
                result.concat(neutralSentence.get(temp));
                result.concat("\n");           
            }
        }
        return result;
    }
}