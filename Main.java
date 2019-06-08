import java.util.*;

class Main
{
    public static void main(String[] args) {
        String message = "</font:Nanum/>Hello world!";

        stringParser parser = new stringParser(message);
        headerGenerator head = new headerGenerator(8080, 8888, filetype.MESSAGE, parser.configureInfo());
        emotionReady emotion = new emotionReady("emotion.txt");
        messagePrint printer = new messagePrint("8888", emotion);

        head.makeJSONfile();
        printer.getNewMessage("header.txt", parser.pureString());
        printer.printMessage();
    }
}