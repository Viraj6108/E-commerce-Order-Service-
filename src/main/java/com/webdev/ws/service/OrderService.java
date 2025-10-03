package com.webdev.ws.service;

import java.util.UUID;

import com.webdev.ws.dto.OrdersDTO;
import com.webdev.ws.model.OrdersEntity;

public interface OrderService {
	
	public OrdersEntity createOrder(OrdersDTO ordersEntity);
	
	public OrdersEntity getOrder(UUID orderId);
}
