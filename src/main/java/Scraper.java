import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by darya on 28/12/14.
 */


public class Scraper {
    public static void main(String[] args) throws Exception {

        try {
            List<String> wordsList = Arrays.asList(args[1].split(","));
            if (args[0].contains("http")) {
                String url = args[0];
                Spider spider = new Spider();
                spider.parse(url, wordsList, args);
            } else {
                ReadFromFile rff = new ReadFromFile();
                ArrayList<String> urlsFromFile = rff.readFromFile(args[0]);
                for (String el : urlsFromFile) {
                    Spider spider = new Spider();
                    spider.parse(el, wordsList, args);
                }

            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Please, enter URL or path to file and commands");
        }


    }
}
