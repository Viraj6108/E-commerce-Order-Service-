package com.webdev.ws.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webdev.ws.dto.OrdersDTO;
import com.webdev.ws.model.OrdersEntity;
import com.webdev.ws.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrdersController {

	private OrderService orderService;
	
	public OrdersController(OrderService orderService)
	{
		this.orderService = orderService;
	}
	
	@PostMapping("/place")
	public OrdersEntity createOrder(@RequestBody OrdersDTO ordersDTO)
	{
		
		return orderService.createOrder(ordersDTO);
	}
	
	@GetMapping("/get")
	public OrdersEntity getOrderById(@RequestParam UUID orderId)
	{
		
		return orderService.getOrder(orderId);
	}
	
}
