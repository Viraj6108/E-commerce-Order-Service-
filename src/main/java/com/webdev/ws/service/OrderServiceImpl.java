package com.webdev.ws.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.webdev.ws.dto.OrdersDTO;
import com.webdev.ws.enums.OrderStatus;
import com.webdev.ws.errors.GlobalErrors;
import com.webdev.ws.events.OrderCreatedEvent;
import com.webdev.ws.model.OrdersEntity;
import com.webdev.ws.repository.OrdersRepository;

@Component
public class OrderServiceImpl implements OrderService{

	private OrdersRepository ordersRepository;
	
	private final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	private String eventName ;
	
	public OrderServiceImpl(OrdersRepository ordersRepository, KafkaTemplate<String,Object>kafkaTemplate
			,@Value("${order.created.topic.name}")String eventName)
	{
		this.ordersRepository = ordersRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.eventName = eventName;
	}
	
	
	/**
	 * @param ordersDTO
	 * @implNote create new order save in DB and produce event with order data
	 */
	@Override
	public OrdersEntity createOrder(OrdersDTO ordersDTO) {
		
		// TODO Auto-generated method stub
		//save order in Dto 
		LOGGER.info("Order Recieved"+ordersDTO);
		
		OrdersEntity entity = new OrdersEntity();
		entity.setOrderId(UUID.randomUUID());
		BeanUtils.copyProperties(ordersDTO, entity);
		entity.setOrderStatus(OrderStatus.CREATED);
		ordersRepository.save(entity);
		
		//Before sending event map order to Order created event dto 
		//Set header and message key
		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
		BeanUtils.copyProperties(entity, orderCreatedEvent);
		//To set headers we have to use producer record 
		ProducerRecord<String, Object> producerRecord = new ProducerRecord(eventName,orderCreatedEvent.getOrderId().toString(),orderCreatedEvent);
		producerRecord.headers().add("ID",UUID.randomUUID().toString().getBytes());
		
		LOGGER.info("Header added with id and random UUID");
		
		try {
			SendResult<String, Object> result =kafkaTemplate.send(producerRecord).get();
			LOGGER.info("Partition "+ result.getRecordMetadata().partition());
			LOGGER.info("Replica"+ result.getRecordMetadata().serializedKeySize());

		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error(e.getMessage()+""+LocalDateTime.now()+"/orders");
		}
		LOGGER.info("Event sent with info"+orderCreatedEvent);
			
		return entity;
	}

	
	/**
	 * @param orderId
	 * @apiNote get order using order id 
	 */
	@Override
	public OrdersEntity getOrder(UUID orderId) {
		OrdersEntity entity = new OrdersEntity();
		try {
		 entity = ordersRepository.findByOrderId(orderId).orElseThrow(()->new GlobalErrors("Order not found with orderId"+orderId));
		
		}catch(Exception e)
		{
			LOGGER.error("Error occured while processing request: "+e.getMessage());
		}
		
		return entity;
	}

}
