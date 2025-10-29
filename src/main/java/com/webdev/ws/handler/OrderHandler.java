package com.webdev.ws.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.webdev.ws.commands.OrderReservationFailedCommand;
import com.webdev.ws.commands.OrderStatusCommand;
import com.webdev.ws.enums.OrderStatus;
import com.webdev.ws.errors.GlobalErrors;
import com.webdev.ws.errors.NotRetryableException;
import com.webdev.ws.errors.RetryableException;
import com.webdev.ws.model.OrdersEntity;
import com.webdev.ws.repository.OrdersRepository;

@KafkaListener(topics = "order-command", groupId = "order-group",containerFactory = "concurrentKafkaListenerContainerFactory")
@Component
public class OrderHandler {

	private OrdersRepository repository;

	public OrderHandler(OrdersRepository repository) {
		this.repository = repository;
	}

	Logger LOGGER = LoggerFactory.getLogger(getClass());

	@KafkaHandler
	public void handleSuccess(@Payload OrderStatusCommand command) throws NotRetryableException, GlobalErrors {
		try {
			OrdersEntity order = repository.findByOrderId(command.getOrderId())
					.orElseThrow(() -> new GlobalErrors("Order not found with order id " + command.getOrderId()));

			order.setOrderStatus(OrderStatus.ACCEPTED);
			repository.save(order);

			LOGGER.info("Order status changed to ACCEPTED for orderId: {}", command.getOrderId());

		} catch (NotRetryableException e) {
			LOGGER.error("Non-retryable error occurred: {}", e.getMessage(), e);
			throw new NotRetryableException("Failure due to: ");
		}
	}

	@KafkaHandler
	public void handleFailure(@Payload OrderReservationFailedCommand command) {
		try {
			OrdersEntity entity = repository.findByOrderId(command.getOrderId())
					.orElseThrow(() -> new GlobalErrors("Order id not found"));
			entity.setOrderStatus(OrderStatus.REJECTED);
			repository.save(entity);
		} catch (GlobalErrors e) {
			LOGGER.info("Failed to change status of order id"+command.getOrderId());
			LOGGER.info("Error :"+e.getLocalizedMessage());
			
			throw new RetryableException("Retrying 3 times");
		}
	}

	@KafkaHandler(isDefault = true)
	public void defaulthandler(Object object) {
		System.err.println("object class" + object.getClass());
	}
}
