package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.dto.Chat;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.Properties;

//작업자 : 홍제기
@Service
@RequiredArgsConstructor
public class KafkaChatProducer {

    private final KafkaTemplate<String, Chat> kafkaTemplate;

    //Kafka를 통해 메시지 전송
    public void sendMessage(Chat message) {
        String topic = "chat";
        message.setId(UUID.randomUUID().toString());
        kafkaTemplate.send(topic, message);
    }
}