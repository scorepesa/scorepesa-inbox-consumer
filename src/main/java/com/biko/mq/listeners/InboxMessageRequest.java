package com.biko.mq.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboxMessageRequest {

    static final Logger log = LoggerFactory.getLogger(InboxMessageRequest.class);
    static String uri = "";

    public boolean deliverMessageNoAuth(String message, String url) {

        JSONObject request = new JSONObject(message);
        log.info("URL: " + url + " message : " + request.toString());
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            StringEntity input = new StringEntity(request.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);
            // log.debug("HTTPRES::  "+response.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer resp = new StringBuffer();
            String output;
            while ((output = br.readLine()) != null) {
                resp.append(output);
            }
            httpClient.getConnectionManager().shutdown();
            log.debug("inboxMessageRequest: Sending 'POST' request to URL : " + uri);

            // log.debug("inboxMessageRequest: response: " + resp.toString());
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                return true;
            }
            log.debug("inboxMessageRequest: response CODE: " + code);
            return false;
        } catch (IOException e) {
            log.error("inboxMessageRequest: could not post: ", e);
        } catch (IllegalStateException e) {
            log.error("inboxMessageRequest: could not post: ", e);
        }
        return false;
    }

    public boolean deliverMessageBasicAuth(String message, String url, String Username, String Password) {
        uri = url;

        JSONObject request = new JSONObject(message);
        log.info("got message : " + request.toString());
        try {

            byte[] encoding = Base64.getEncoder().encode((Username + ":" + Password).getBytes());
            DefaultHttpClient AuthhttpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(uri);
            String stringEncode = new String(encoding, Charset.forName("UTF-8"));
            postRequest.setHeader("Authorization", "Basic " + stringEncode);
            HttpResponse auth = AuthhttpClient.execute(postRequest);
            BufferedReader brr = new BufferedReader(new InputStreamReader(auth.getEntity().getContent()));

            StringBuilder respp = new StringBuilder();
            String Authoutput;
            while ((Authoutput = brr.readLine()) != null) {
                respp.append(Authoutput);
            }
            AuthhttpClient.getConnectionManager().shutdown();
            int Authcode = auth.getStatusLine().getStatusCode();
            if (Authcode == 200) {
                log.debug("Successfully Authorized" + uri);
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    StringEntity input = new StringEntity(request.toString());
                    input.setContentType("application/json");
                    postRequest = new HttpPost(uri);
                    postRequest.setEntity(input);
                    HttpResponse response = httpClient.execute(postRequest);
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer resp = new StringBuffer();
                    String output;
                    while ((output = br.readLine()) != null) {
                        resp.append(output);
                    }
                    httpClient.getConnectionManager().shutdown();
                    log.debug("inboxMessageRequest: Sending 'POST' request to URL : " + uri);

                    log.debug("inboxMessageRequest: response: " + resp.toString());
                    int code = response.getStatusLine().getStatusCode();
                    return code == 200;

                } catch (IOException er) {
                    log.error("inboxMessageRequest: POST data : ", er);
                } catch (IllegalStateException er) {
                    log.error("inboxMessageRequest: POST data : ", er);
                }

                return true;

            }
            log.debug("Unable to Authorize/login : " + uri);
            return false;
        } catch (IOException e) {
            log.error("inboxMessageRequest: Authorize : ", e);
        } catch (IllegalStateException e) {
            log.error("inboxMessageRequest: Authorize : ", e);
        }
        return false;
    }

    public boolean deliverMessageTokenAuth(String message, String url, String token) {
        uri = url;

        JSONObject request = new JSONObject(message);
        log.info("got message : " + request.toString());
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(uri);
            StringEntity input = new StringEntity(request.toString());
            input.setContentType("application/json");
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);

            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder resp = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                resp.append(output);
            }
            httpClient.getConnectionManager().shutdown();
            log.debug("inboxMessageRequest: Sending 'POST' request to URL : " + uri);

            log.debug("inboxMessageRequest: response: " + resp.toString());
            int code = response.getStatusLine().getStatusCode();
            return code == 200;
        } catch (IOException e) {
            log.error("inboxMessageRequest: could not post: ", e);
        } catch (IllegalStateException e) {
            log.error("inboxMessageRequest: could not post: ", e);
        }
        return false;
    }

}
