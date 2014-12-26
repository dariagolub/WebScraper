import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpConnection {

    private final String USER_AGENT = "Mozilla/5.0";
    private String url;
    String domainurl;
    ArrayList<String> urls = new ArrayList<String>();



    public static void main(String[] args) throws Exception {

        HttpConnection http = new HttpConnection();
        String response = http.sendGet("http://www.booking.com/index.en-gb.html?sid=664b9babd5b4e66c0216083e996ba341;dcid=1");
        http.getUrls(response);
        HashSet<String> urlsSet = new HashSet<String>(http.urls);

        Iterator<String> itr2 = urlsSet.iterator();
        while (itr2.hasNext()) {
            System.out.println(itr2.next().toString());
        }


    }

    // HTTP GET request
    private String sendGet(String url) throws Exception {

        this.url = url;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

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

    private void getUrls(String response) throws Exception {
        //get domain
        //Pattern p1 = Pattern.compile(".*?([^.]+\\.[^.]+)");
        URI uri = new URI(this.url);
        //Matcher m1 = p1.matcher(uri.getHost());
        //m1.find();
        domainurl = uri.getHost();

        System.out.println("Domain name for url: '" + this.url + "' is: " + domainurl);

        //search absolute urls inside page

        String regex = "href=\"(http.?://.*?)\"";
        Pattern p2 = Pattern.compile(regex);
        Matcher m2 = p2.matcher(response);
        String urlAbsolute;
        while (m2.find()) {
            urlAbsolute = m2.group(1); // contain the link URL
            //check if url on current domain
            try{
                Pattern p3 = Pattern.compile("http.?://.*?"+ domainurl +"");
                Matcher m3 = p3.matcher(urlAbsolute);
                m3.find();
                String checkUrl = m3.group(0);
                //add urls to array
                urls.add(urlAbsolute);
            } catch (IllegalStateException ex) {}
        }

        //search relative urls inside page
        String regex2 = "href=\"(/.*?)\"";
        Pattern p4 = Pattern.compile(regex2);
        Matcher m4 = p4.matcher(response);
        String urlRelative;
        while (m4.find()) {
            urlRelative = m4.group(1);
            urls.add("http://" + domainurl+urlRelative);
        }

    }
}