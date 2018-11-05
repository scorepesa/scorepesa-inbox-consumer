package com.biko.mq.listeners;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericConsumer
  extends DefaultConsumer
{
  static final Logger log = LoggerFactory.getLogger(GenericConsumer.class);
  Channel channel;
  String queue_name;
  String exchange;
  static Properties props;
  
  public GenericConsumer(Channel channel, String queue_name, String exchange, Properties properties)
  {
    super(channel);
    this.channel = channel;
    this.queue_name = queue_name;
    this.exchange = exchange;
    props = properties;
  }
  
/* 
 public static void main(String args[]){
      java.util.Date date= new java.util.Date();
       
       // DateFormat f = new SimpleDateFormat("Y-m-d h:m:s");
       DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       f.setTimeZone(TimeZone.getTimeZone("EAT")); 
       String timestamp = f.format(date);
       System.out.println("TTT::::--->\n\n\n"+timestamp);
 } 
  */
  public void handleDelivery(String consumeTag, Envelope envelope, BasicProperties properties, byte[] body)
    throws IOException
  {
    String routingKey = envelope.getRoutingKey();
    String contentType = properties.getContentType();
    long deliveryTag = envelope.getDeliveryTag();
    
    String msg = new String(body);
    UUID uuid = UUID.randomUUID();
    log.debug(uuid + " S Channel :" + this.channel + " Thread:" + Thread.currentThread() + " msg:" + msg.toString());
    log.debug("routing key: " + routingKey + " contentType:  " + contentType);
    
    boolean results = false;
    //this check determines if we should send the data to GW or not and helps format the json to be sent ot GW
    if(this.queue_name.equalsIgnoreCase(props.getProperty("deposit_mq_queue"))){
         JSONObject fromQRequest = new JSONObject(msg);
         JSONObject packet,header,payload,extra;
         //this is used as the credentials body on GW
         
          java.util.Date date= new java.util.Date();
       
       // DateFormat f = new SimpleDateFormat("Y-m-d h:m:s");
       DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       f.setTimeZone(TimeZone.getTimeZone("EAT")); 
       String timestamp = f.format(date);
      
         header = new JSONObject();
         header.put("username", props.getProperty("gw_username"));
         header.put("password", props.getProperty("gw_password"));
         packet = new JSONObject();
         packet.put("source_account", fromQRequest.getString("BusinessShortCode"));
         packet.put("destination", props.getProperty("gw_destination"));
         packet.put("destination_account", fromQRequest.getString("MSISDN"));
         packet.put("payment_date", timestamp);
         packet.put("amount", Double.valueOf(fromQRequest.getString("TransAmount")));
         packet.put("channel_id", 2);
         packet.put("narration", "NO TEXT");
         packet.put("account_no", fromQRequest.getString("BillRefNumber"));
         packet.put("reference_number", fromQRequest.getString("TransID"));
         extra = new JSONObject();
         try{
         extra.put("names", fromQRequest.getJSONArray("KYCInfo").getJSONObject(0).getString("KYCValue")+" "+fromQRequest.getJSONArray("KYCInfo").getJSONObject(1).getString("KYCValue"));
         }catch(Exception er){
           log.debug("NO KYC data found.");  
             extra.put("names","No KYC");
         }
         extra.put("business_number", fromQRequest.getString("BusinessShortCode"));
         extra.put("account_no",fromQRequest.getString("BillRefNumber"));
         packet.put("extra", extra);
         payload = new JSONObject();
         payload.put("credentials", header);
         payload.put("packet", packet);
       GenericMessageRequest request = new GenericMessageRequest();
    results = request.deliverMessageToGw(payload.toString(), props.getProperty("gw_endpoint")); 
    }else{
    GenericMessageRequest request = new GenericMessageRequest();
    results = request.deliverMessage(msg, this.queue_name, this.exchange, props.getProperty("generic_message_request"));
    }
    if (results)
    {
      log.debug("Process succsssful deliveryTag is: " + deliveryTag);
      this.channel.basicAck(deliveryTag, false);
    }
    else
    {
      log.debug("result is false for tag " + deliveryTag);
      this.channel.basicNack(deliveryTag, false, results);
    }
    
  }
}
