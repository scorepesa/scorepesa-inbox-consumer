package com.biko.mq.listeners;

import static com.biko.mq.listeners.GenericMessageRequest.log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericMessageRequest
{
  static final Logger log = LoggerFactory.getLogger(GenericMessageRequest.class);
  String uri = "";
  
  public boolean deliverMessage(String message, String queue_name, String exchange, String url)
  {
    this.uri = url;
    JSONObject request = null;
    
    log.info("message is: " + message);
    try
    {
      request = new JSONObject(message);
    }
    catch (JSONException e)
    {
      log.error("malformed json received");
      return false;
    }
    request.put("queue_name", queue_name);
    request.put("exchange", exchange);
    log.info("exchange is " + exchange);
    log.info("got message : " + request.toString());
    try
    {
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpPost postRequest = new HttpPost(this.uri);
      StringEntity input = new StringEntity(request.toString());
      input.setContentType("application/json");
      postRequest.setEntity(input);
      
      HttpResponse response = httpClient.execute(postRequest);
      
      BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      
      StringBuffer resp = new StringBuffer();
      String output;
      while ((output = br.readLine()) != null) {
        resp.append(output);
      }
      httpClient.getConnectionManager().shutdown();
      log.debug("GenericMessageRequest: Sending 'POST' request to URL : " + this.uri);
      
      log.debug("GenericMessageRequest: response: " + resp.toString());
      int code = response.getStatusLine().getStatusCode();
      if (code == 200) {
        return true;
      }
      return false;
    }
    catch (Exception e)
    {
      log.error("GenericMessageRequest: could not post: ", e);
    }
    return false;
  }



//THis serves the GW only
public boolean deliverMessageToGw(String message,String url)
  {
    this.uri = url;
    JSONObject request = null;
    
    log.info("message is: " + message);
    try
    {
      request = new JSONObject(message);
    }
    catch (JSONException e)
    {
      log.error("malformed json received");
      return false;
    }
       log.info("got message : " + request.toString());
    try
    {
      DefaultHttpClient httpClient = new DefaultHttpClient();
      HttpPost postRequest = new HttpPost(this.uri);
      StringEntity input = new StringEntity(request.toString());
      input.setContentType("application/json");
      postRequest.setEntity(input);
      
      HttpResponse response = httpClient.execute(postRequest);
      
      BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      
      StringBuffer resp = new StringBuffer();
      String output;
      while ((output = br.readLine()) != null) {
        resp.append(output);
      }
      httpClient.getConnectionManager().shutdown();
      log.debug("GenericMessageRequest: Sending 'POST' request to URL : " + this.uri);
      
      log.debug("GenericMessageRequest: response: " + resp.toString());
      int code = response.getStatusLine().getStatusCode();
      if (code == 200) {
        return true;
      }
      return false;
    }
    catch (Exception e)
    {
      log.error("GenericMessageRequest: could not post: ", e);
    }
    return false;
  }
}

