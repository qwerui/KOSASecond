package com.app.sketchbook.chat.configuration;

import com.app.sketchbook.chat.dto.Chat;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

//작업자 : 홍제기
@EnableKafka // Spring Kafka 활성화 -> Kafka Listener 사용 가능
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Value("${spring.kafka.consumer.id}")
    private String kafkaConsumerGroupId;

    @Value("${spring.kafka.consumer.num}")
    private int kafkaConsumerNum;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Chat> kafkaChatContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Chat> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(kafkaChatConsumer());
        factory.setConcurrency(kafkaConsumerNum); // 컨슈머 수 설정
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Chat> kafkaChatConsumer() {
        // Kafka Consumer 설정
        Map<String, Object> consumerConfigurations =
                ImmutableMap.<String, Object>builder()
                        .put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer)
                        .put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId)
                        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class)
                        .put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
                        .put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "10000")
                        .put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "200")
                        .put(JsonDeserializer.TRUSTED_PACKAGES, "*")
                        .put(JsonDeserializer.VALUE_DEFAULT_TYPE, Chat.class.getName())
                        .build();

        // 키는 문자열, 값은 Json에서 역직렬화
        return new DefaultKafkaConsumerFactory<>(consumerConfigurations, new StringDeserializer(), new JsonDeserializer<>(Chat.class));
    }
}