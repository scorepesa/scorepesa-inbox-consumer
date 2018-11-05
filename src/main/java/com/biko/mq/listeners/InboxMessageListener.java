package com.biko.mq.listeners;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboxMessageListener
{
  private static Logger log = LoggerFactory.getLogger(InboxMessageListener.class);
  static String host;
  static String vhost;
  static String port;
  static String user;
  static String password;
  static String queue;
  ConnectionFactory factory;
  
  public boolean invoke(QueueConfigsModel qConfigs)
  {
    
    log.info("InboxMessageListener has been started");
    try
    {
      this.factory = new ConnectionFactory();
      
      this.factory.setHost(qConfigs.getQHost());
      this.factory.setUsername(qConfigs.getQusername());
      this.factory.setPassword(qConfigs.getQPassword());
    }
    catch (Exception e)
    {
      log.error("Failed to connect to rabbitMQ: ", e);
      return false;
    }
    try
    {
      ExecutorService es = Executors.newFixedThreadPool(qConfigs.getThreads());
      Connection conn = this.factory.newConnection(es);
      for (int i = 0; i < qConfigs.getThreads(); i++)
      {
        Channel channel = conn.createChannel();
        channel.basicQos(30);
        channel.basicConsume(qConfigs.getQname(), false, new MyQueueConsumer(channel, qConfigs));
      }
      log.info("Invoke " + qConfigs.getThreads() + " thread and wait for messages");
    }
    catch (Exception e)
    {
      log.error("Failed to create connection: ", e);
      
      return false;
    }
    return true;
  }
}
