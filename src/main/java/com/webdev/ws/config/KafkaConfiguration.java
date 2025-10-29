
package com.webdev.ws.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.webdev.ws.errors.NotRetryableException;
import com.webdev.ws.errors.RetryableException;

@Configuration
public class KafkaConfiguration {

	private final Integer TOPIC_PARTITIONS = 3;
	private final Integer TOPIC_REPLICAS = 2;

	@Value("${order.created.topic.name}")
	private String TOPIC_NAME;

	private final Environment env;

	public KafkaConfiguration(Environment env) {
		this.env = env;
	}

	@Bean
	Map<String, Object> producerConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.producer.bootstrap-servers"));
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, env.getProperty("spring.kafka.producer.acks"));
		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
				env.getProperty("spring.kafka.producer.enable.idempotence"));
		config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
				env.getProperty("spring.kafka.producer.properties.max.in.flight.requests.per.connection"));
		config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG,
				env.getProperty("spring.kafka.producer.properties.delivery.timeout.ms"));
		config.put(ProducerConfig.LINGER_MS_CONFIG, env.getProperty("spring.kafka.producer.properties.linger.ms"));
		config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
				env.getProperty("spring.kafka.producer.properties.request.timeout.ms"));
		return config;
	}

	@Bean
	ProducerFactory<String, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfig());
	}

	
	@Bean
	KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
	 

	@Bean
	NewTopic orderCreatedEvent() {
		return TopicBuilder.name(TOPIC_NAME).partitions(TOPIC_PARTITIONS).replicas(TOPIC_REPLICAS).build();
	}
	
	
	//Consumer configuration
	
	@Bean
	ConsumerFactory<String, Object> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.consumer.bootstrap-servers"));
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
		config.put(JsonDeserializer.TRUSTED_PACKAGES,
				env.getProperty("spring.kafka.consumer.properties.spring.json.trusted-packages"));
		config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("consumer.group.id"));
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"));
		config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

		return new DefaultKafkaConsumerFactory<>(config);
	}

	// Configuration for DLT

	
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, Object> concurrentKafkaListenerContainerFactory(
			KafkaTemplate<String, Object> kafkaTemplate, ConsumerFactory<String, Object> consumerFactory) {
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
		FixedBackOff back = new FixedBackOff(5000L, 3);
		DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, back);
		handler.addNotRetryableExceptions(NotRetryableException.class);
		handler.addRetryableExceptions(RetryableException.class);
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setCommonErrorHandler(handler);
		factory.setConsumerFactory(consumerFactory);
		return factory;

	}
	 
	 
	
	
	
	
}
