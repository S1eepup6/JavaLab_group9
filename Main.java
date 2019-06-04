import java.util.*;

class Main
{
    public static void main(String[] args) {
        emotionReady mainER = new emotionReady("emotion.txt");
        String msg = "I was born to love you with every single beats of my hearts.";
        messagePrint analyzer = new messagePrint("header.txt",msg,"8888",mainER);
        analyzer.printMessage();
        msg = "I was born to take care of you, honey!";
        analyzer.getNewMessage(msg);
        analyzer.printMessage();
        msg = "With every single days of my life!";
        analyzer.getNewMessage(msg);
        analyzer.printMessage();
    }
}