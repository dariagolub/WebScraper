import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by darya on 28/12/14.
 */
public class ReadFromFile {
    public ArrayList<String> readFromFile(String pathToFile) throws Exception {
        ArrayList<String> urlsFromFile = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(pathToFile));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line!=null){
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String allUrls = sb.toString();
            String regex = "HYPERLINK\\s\"(.*?)\"";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(allUrls);
            String urlFromFile;
            while (m.find()) {
                urlFromFile = m.group(1); // contain the link URL
                urlsFromFile.add(urlFromFile);}
        } finally {
            br.close();
        }

        return urlsFromFile;

    }
}
