package com.nnocpeed.draco.handler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Configuration
public class MqttMessageHandler {
    @Value("${mqtt.serverURIs}")
    private String url;

    @Value("${mqtt.client.topics}")
    private String topics;

    @Value("${mqtt.client.commandTopics}")
    private String commandTopics;

    @Value("${mqtt.client.inboundclientid}")
    private String inclientid;

    @Value("${mqtt.client.outboundclientid}")
    private String outclientid;

    static MqttClient client = null;

    public void sendMessage(String content) {
        try {
            if( this.client == null) {
                this.client = new MqttClient(url, inclientid, new MemoryPersistence());
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setMaxInflight(1000);
                connOpts.setAutomaticReconnect(true);
                log.info("Connecting to broker: {}" + url);
                client.connect(connOpts);
                log.info("Connected");
            }

            MqttMessage message = new MqttMessage(content.getBytes());
            client.publish(topics, message);
        } catch (MqttException me) {
            log.error("Mqtt public exception = {}", me.getMessage());
        }
    }


    /**
     * InBound Begin 消息接收端
     ****/
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        String clientid = inclientid + "_" + System.currentTimeMillis();
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(url, clientid, commandTopics);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        //adapter.addTopic(topics);

        return adapter;
    }

    // ServiceActivator注解表明当前方法用于处理MQTT消息，inputChannel参数指定了用于接收消息信息的channel。

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                try {
                    log.info(message.getPayload().toString());
                    //commandHandlerService.doCommand(message.getPayload().toString());
                    //sensorLogService.createSensorLogFromMqtt(message.getPayload().toString());
                } catch (Exception e) {
                    log.error("Decode sensor log failed: " + e.getMessage());
                }
            }
        };
    }

    /** InBound End ****/

    /**
     * OutBound Begin 消息发送端
     ****/

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        String[] urls = {url};
        options.setServerURIs(urls);
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);

        return factory;
    }






    /*****
     * 发送消息和消费消息Channel可以使用相同MqttPahoClientFactory
     *
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        // 在这里进行mqttOutboundChannel的相关设置
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(MqttAsyncClient.generateClientId(), mqttPahoClientFactory());
        messageHandler.setAsync(true); // 如果设置成true，发送消息时将不会阻塞。
        messageHandler.setDefaultTopic("application/2");
        return messageHandler;
    }

    @Component
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {

        // 定义重载方法，用于消息发送
        void sendToMqtt(String data);

        // 指定topic进行消息发送
        void sendToMqtt(String payload, @Header(MqttHeaders.TOPIC) String topic);

        void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos, String payload);

    }
}
