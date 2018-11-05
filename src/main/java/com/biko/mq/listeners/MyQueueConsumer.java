package com.biko.mq.listeners;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyQueueConsumer
        extends DefaultConsumer {

    static final Logger LOG = LoggerFactory.getLogger(MyQueueConsumer.class);
    QueueConfigsModel qconfigs;
    Channel channel;

    public MyQueueConsumer(Channel channel, QueueConfigsModel configsModel) {
        super(channel);
        this.qconfigs = configsModel;
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumeTag, Envelope envelope,
            BasicProperties properties, byte[] body)
            throws IOException {
        String routingKey = envelope.getRoutingKey().trim().toUpperCase();
        String contentType = properties.getContentType();
        long deliveryTag = envelope.getDeliveryTag();

        String msg = new String(body);
        UUID uuid = UUID.randomUUID();
        LOG.debug(uuid + " Channel :" + this.channel + " Thread:" + Thread.currentThread() + " msg:" + msg);
        LOG.debug("routing key: " + routingKey + " contentType:  " + contentType + " CONFIG QUEUE: " + qconfigs.getQname().trim().toUpperCase());

        boolean results = false;

        InboxMessageRequest request = new InboxMessageRequest();
        results = request.deliverMessageNoAuth(msg, qconfigs.getEndpoint());

        if (results) {
            LOG.debug("Process succsssful deliveryTag is: " + deliveryTag);
            this.channel.basicAck(deliveryTag, false);
        } else {
            LOG.debug("result is false for tag " + deliveryTag);
            this.channel.basicNack(deliveryTag, false, results);
        }
    }
}
