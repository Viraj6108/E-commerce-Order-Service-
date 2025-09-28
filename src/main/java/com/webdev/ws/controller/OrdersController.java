package com.webdev.ws.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webdev.ws.dto.OrdersDTO;
import com.webdev.ws.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrdersController {

	private OrderService orderService;
	
	public OrdersController(OrderService orderService)
	{
		this.orderService = orderService;
	}
	
	@PostMapping
	public OrdersDTO createOrder(@RequestBody OrdersDTO ordersDTO)
	{
		orderService.createOrder(ordersDTO);
		return ordersDTO;
	}
}
