import java.util.*;

class Main
{
    public static void main(String[] args) {
        emotionReady mainER = new emotionReady("emotion.txt");
        String msg = "I was born to luv u with every single beats of my hearts.";
        messagePrint analyzer = new messagePrint("header.txt",msg,"8888",mainER);
        analyzer.printMSG();
    }
}