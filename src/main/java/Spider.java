import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {

    private final String USER_AGENT = "Mozilla/5.0";
    private String url;
    String domainurl;
    ArrayList<String> urls = new ArrayList<String>();
    HashMap<String,Integer> wordsStat = new HashMap<String, Integer>();

    public void parse(String url, List<String> wordsList, String[] args) throws Exception {
        try {
            String response = this.sendGet(url);
            this.getUrls(response);

            //Set for not viewed urls
            HashSet<String> notViewedUrls = new HashSet<String>(this.urls);
            //Set for viewed urls
            HashSet<String> viewedUrls = new HashSet<String>();
            while (!notViewedUrls.isEmpty()) {
                String currentUrl = notViewedUrls.iterator().next();
                response = this.sendGet(currentUrl);
                ArrayList<String> newUrls = this.getUrls(response);
                HashSet<String> newUrlsSet = new HashSet<String>(newUrls);
                newUrlsSet.removeAll(viewedUrls);
                newUrlsSet.removeAll(notViewedUrls);
                notViewedUrls.addAll(newUrlsSet);
                viewedUrls.add(currentUrl);
                StringBuilder sb = new StringBuilder();
                sb.append(currentUrl);
                if (Arrays.asList(args).contains("-c")) {
                    sb.append(" Number of characters on this page is ").append(response.length());
                }
                if (Arrays.asList(args).contains("-w")) {
                    sb.append(" Number of provided word(s) occurrence on webpage is ").append(this.getWordStat(response, wordsList));
                }
         /*   if(Arrays.asList(args).contains("-e")) {
                sb.append(" Sentences with given words ").append(this.getSentences(response, wordsList));
            }*/
                System.out.println(sb.toString());
                // System.out.println(currentUrl + " Number of characters on this page is " + response.length() + " Number of provided word(s) occurrence on webpage is " + this.getWordStat(response, wordsList) + this.getSentences(response, wordsList));
                notViewedUrls.remove(currentUrl);
                newUrls.clear();
                newUrlsSet.clear();
            }
        } catch (SocketException ex) {
            System.out.println("Error reading response from server");
        }
    }

    // HTTP GET request
    private String sendGet(String url) throws Exception {

        this.url = url;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private ArrayList<String> getUrls(String response) throws Exception {
        //get host
        URI uri = new URI(this.url);
        domainurl = uri.getHost();

        //search absolute urls inside page
        String regex = "href=\"(http.?://.*?)\"";
        Pattern p2 = Pattern.compile(regex);
        Matcher m2 = p2.matcher(response);
        String urlAbsolute;
        while (m2.find()) {
            urlAbsolute = m2.group(1); // contain the link URL
            //check if url on current domain
            if (urlAbsolute.contains(domainurl)) {
                urls.add(urlAbsolute);
            }
        }

        //search relative urls inside page
        String regex2 = "href=\"(/.*?)\"";
        Pattern p4 = Pattern.compile(regex2);
        Matcher m4 = p4.matcher(response);
        String urlRelative;
        while (m4.find()) {
            urlRelative = m4.group(1);
            if (! urlRelative.endsWith("css")&&! urlRelative.endsWith(".js")&&! urlRelative.endsWith(".jpg")&&! urlRelative.endsWith(".png")&&! urlRelative.endsWith(".ico")&&! urlRelative.endsWith(".gif"))
            {urls.add("http://" + domainurl + urlRelative);}
        }
        return urls;
    }

    private String getWordStat(String response, List<String > wordsList) throws Exception {

        String wordStatString = "";

        for (String el: wordsList) {
            String regex = "[^\\w]"+el+"[^\\w]";
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(response);
            int count = 0;
            while (m.find()){
                count++;
            }
            wordsStat.put(el, count);
        }

        Set<Map.Entry<String,Integer>> set = wordsStat.entrySet();
        for (Map.Entry<String, Integer> me: set) {
            wordStatString += "Word: " + me.getKey() + " Count: " + me.getValue() + " ";
        }
        return wordStatString;
    }

   /* private String getSentences(String response, List<String> wordsList) throws Exception {
        StringBuilder sentences = new StringBuilder();
        for (String el:wordsList) {
            String regex = "\\..*?"+el+".*\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(response);
            while (m.find()) {
                String sentence = m.group();
                sentences.append(sentence + " ");
            }
        }
        return sentences.toString();
    }*/
}