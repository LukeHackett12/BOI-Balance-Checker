package com.luke.boibalancechecker;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> cookies;
    private HttpsURLConnection conn;

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.checkBalance);
        button.setOnClickListener(v -> {
            String balance = null;
            try {
                balance = getBalance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(balance);

            TextView tv = findViewById(R.id.balance);
            tv.setText(balance);
        });
    }

    String getBalance() throws Exception {
        StringBuilder balances = new StringBuilder();

        String url = "https://www.365online.com/online365";
        CookieHandler.setDefault(new CookieManager());

        //================================ STAGE ONE START ===================================//
        String page = getPageContent(url);
        String postParams = getStageOneParams(page, "", "", "", "", "");

        page = sendPost("https://www.365online.com/online365/spring/authentication?execution=e1s1", postParams);

        //================================ STAGE ONE END ===================================//

        //================================ STAGE TWO START =================================//

        postParams = getStageTwoParams(page, "");
        page = sendPost("https://www.365online.com/online365/spring/authentication?execution=e1s2", postParams);

        //================================ STAGE TWO END ====================================//

        //================================ START PARSE ACCOUNT BALANCES ============================//
        Document doc = Jsoup.parse(page);
        ArrayList<Element> accounts = doc.getElementsByAttributeValueContaining("class", "acc_container_inner minHeight35px");

        for(Element account : accounts){
            balances.append(account.getElementsByAttributeValue("class", "rich-tglctrl").text());
            balances.append(": ");
            balances.append(account.getElementsByAttributeValueContaining("id", "balance").text());
            balances.append("\n");
        }

        //================================ END PARSE ACCOUNT BALANCES ============================//

        return balances.toString();
    }

    String getPageContent(String url) throws Exception {

        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // default is GET
        conn.setRequestMethod("GET");

        conn.setUseCaches(true);

        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        if (cookies != null) {
            for (String cookie : this.cookies) {
                conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        } else {
            cookies = new ArrayList<>();
            cookies.add("JSESSIONID=602D64D72E56D9BFD45B2DABBC4F77D7");
        }
        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies
        setCookies((conn.getHeaderFields().get("Set-Cookie")));
        return response.toString();
    }

    String getStageOneParams(String html, String userID, String dobDate, String dobMonth, String dobYear, String phoneNumber) throws UnsupportedEncodingException {

        System.out.println("Extracting first page data...");

        Document doc = Jsoup.parse(html);

        Element form = doc.getElementById("form");
        Elements inputElements = form.getElementsByAttributeValueContaining("class", "inputbox");
        List<String> paramList = new ArrayList<>();
        paramList.add("form:AajaxRequestStatus=AJAX+REQUEST+PROCESSOR+INACTIVE");

        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");
            switch (key) {
                case "form:userId":
                    value = userID;
                    break;
                case "form:dateOfBirth_date":
                    value = dobDate;
                    break;
                case "form:dateOfBirth_month":
                    value = dobMonth;
                    break;
                case "form:dateOfBirth_year":
                    value = dobYear;
                    break;
                case "form:phoneNumber":
                    value = phoneNumber;
                    break;
            }
            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        paramList.add("form=form");
        paramList.add("autoScroll=");
        paramList.add("javax.faces.ViewState=e1s1");
        paramList.add("form:continue=form:continue");

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }

    String getStageTwoParams(String html, String sixDigitCode) throws UnsupportedEncodingException {
        System.out.println("Extracting first page data...");

        Document doc = Jsoup.parse(html);

        Element form = doc.getElementById("form");
        Elements inputElements = form.getElementsByAttributeValue("class", "inputboxPIN");
        List<String> paramList = new ArrayList<>();
        paramList.add("form:AajaxRequestStatus=AJAX+REQUEST+PROCESSOR+INACTIVE");
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");
            if (key.equals("form:security_number_digit1"))
                value = String.valueOf(sixDigitCode.charAt(0));
            else if (key.equals("form:security_number_digit2"))
                value = String.valueOf(sixDigitCode.charAt(1));
            else if (key.equals("form:security_number_digit3"))
                value = String.valueOf(sixDigitCode.charAt(2));
            else if (key.equals("form:security_number_digit4"))
                value = String.valueOf(sixDigitCode.charAt(3));
            else if (key.equals("form:security_number_digit5"))
                value = String.valueOf(sixDigitCode.charAt(4));
            else if (key.equals("form:security_number_digit6"))
                value = String.valueOf(sixDigitCode.charAt(5));

            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        paramList.add("form=form");
        paramList.add("autoScroll=");
        paramList.add("javax.faces.ViewState=e1s2");
        paramList.add("form:continue=form:continue");

        // build parameters list
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                result.append("&" + param);
            }
        }
        return result.toString();
    }

    String sendPost(String url, String postParams) throws Exception {
        URL obj = new URL(url);
        conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(true);
        conn.setRequestMethod("POST");
        conn.addRequestProperty("Host", "www.365online.com");
        conn.addRequestProperty("User-Agent", USER_AGENT);
        conn.addRequestProperty("Accept",
                " text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.5");
        conn.addRequestProperty("Accept-Encoding", "gzip, deflate, br");

        for (String cookie : this.cookies) {
            conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
        }
        conn.addRequestProperty("Connection", "keep-alive");
        conn.addRequestProperty("Referer", "https://www.365online.com/online365/spring/authentication?execution=e1s1");
        conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.addRequestProperty("Content-Length", Integer.toString(postParams.length()));
        conn.addRequestProperty("Upgrade-Insecure-Requests", "1");

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    void setCookies(List<String> cookies) {
        this.cookies = new ArrayList<>();
        this.cookies.addAll(cookies);
        this.cookies.add("JSESSIONID=602D64D72E56D9BFD45B2DABBC4F77D7");
    }
}
