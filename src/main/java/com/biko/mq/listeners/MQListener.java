package com.biko.mq.listeners;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQListener
{
  static final Logger log = LoggerFactory.getLogger(MQListener.class);
  ConnectionFactory factory;
  String Host;
  String username;
  String password;
  String channel;
  String queue_name;
  String exchange;
  Properties props;
  
  public MQListener(String Host, String username, String password, String queue_name, String exchange)
  {
    int maxthread = 4;
    
    this.Host = Host;
    this.username = username;
    this.password = password;
    this.queue_name = queue_name;
    this.exchange = exchange;
  }
  
  public boolean invoke(int maxthread, Properties properties)
  {
    this.props = properties;
    
    log.info("MQMessageListener has been started");
    try
    {
      this.factory = new ConnectionFactory();
      
      this.factory.setHost(this.Host);
      this.factory.setUsername(this.username);
      this.factory.setPassword(this.password);
    }
    catch (Exception e)
    {
      log.error("Failed to connect to rabbitMQ: ", e);
      return false;
    }
    try
    {
      ExecutorService es = Executors.newFixedThreadPool(maxthread);
      Connection conn = this.factory.newConnection(es);
      for (int i = 0; i < maxthread; i++)
      {
        Channel channel = conn.createChannel();
        channel.basicQos(20);
        channel.basicConsume(this.queue_name, false, new GenericConsumer(channel, this.queue_name, this.exchange, this.props));
      }
      log.info("Invoke " + maxthread + " thread and wait for messages");
    }
    catch (Exception e)
    {
      log.error("Failed to create connection: ", e);
      
      return false;
    }
    return true;
  }
  
  public void time()
  {
    log.debug("timing");
  }
}
