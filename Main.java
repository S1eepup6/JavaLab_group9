import java.util.*;

class Main
{
    public static void main(String[] args) {
        String sendMSG = "</color:blue/>Hello world!";
        stringParser parser = new stringParser(sendMSG);
        headerGenerator generator = new headerGenerator(8080, 8888, filetype.MESSAGE, parser.configureInfo());
        generator.makeJSONfile();
    }
}