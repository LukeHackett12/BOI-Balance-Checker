package com.luke.boibalancechecker.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luke.boibalancechecker.helpers.KeyStoreHelper;
import com.luke.boibalancechecker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.luke.boibalancechecker.activities.MainActivity.BOI_ALIAS;

public class TabFragmentBalance extends Fragment {

    private ArrayList<String> cookies;
    private HttpsURLConnection conn;

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:64.0) Gecko/20100101 Firefox/64.0";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balance, container, false);

        TextView balanceText = view.findViewById(R.id.balanceText);
        MaterialButton checkBalance = view.findViewById(R.id.removeSettings);

        ProgressBar spinner = view.findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        checkBalance.setOnClickListener(view1 -> {
            spinner.setVisibility(View.VISIBLE);
            balanceText.setVisibility(View.GONE);

            balanceText.post(() -> {
                try {
                    balanceText.setText(getBalance());
                    spinner.setVisibility(View.GONE);
                    balanceText.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        return view;
    }

    String getBalance() throws Exception {
        StringBuilder balances = new StringBuilder();

        String url = "https://www.365online.com/online365";
        CookieHandler.setDefault(new CookieManager());

        SharedPreferences accountDetails = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(BOI_ALIAS, null);

        String accountID = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("accountID", null));
        String dobDate = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("dobDate", null));
        String dobMonth = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("dobMonth", null));
        String dobYear = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("dobYear", null));
        String phoneNum = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("phoneNum", null));
        String sixDigitCode = KeyStoreHelper.decrypt(privateKey, accountDetails.getString("sixDigitCode", null));

        //================================ STAGE ONE START ===================================//
        String page = getPageContent(url);
        String postParams = getStageOneParams(page, accountID, dobDate, dobMonth, dobYear, phoneNum);

        page = sendPost("https://www.365online.com/online365/spring/authentication?execution=e1s1", postParams);

        //================================ STAGE ONE END ===================================//

        //================================ STAGE TWO START =================================//

        postParams = getStageTwoParams(page, sixDigitCode);
        page = sendPost("https://www.365online.com/online365/spring/authentication?execution=e1s2", postParams);

        //================================ STAGE TWO END ====================================//

        //================================ START PARSE ACCOUNT BALANCES ============================//
        Document doc = Jsoup.parse(page);
        ArrayList<Element> accounts = doc.getElementsByAttributeValueContaining("class", "acc_container_inner minHeight35px");

        for(Element account : accounts){
            String accountString = account.getElementsByAttributeValue("class", "rich-tglctrl").text();
            accountString = accountString.split(" ~ ")[0] + "(" + accountString.split(" ~ ")[1].split(":")[0] + ")";
            balances.append(accountString);
            balances.append(": ");
            balances.append(account.getElementsByAttributeValueContaining("id", "balance").text());
            balances.append("\n\n");
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
