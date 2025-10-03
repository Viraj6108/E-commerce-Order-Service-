package com.webdev.ws.model;

import java.io.Serializable;
import java.util.UUID;

import com.webdev.ws.enums.OrderStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="order-event")
public class OrdersEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2238791369803031959L;

	@Id
	@GeneratedValue
	private long id;
	private  UUID orderId;
	private UUID productId;
	private Integer quantity;
	private String productName;
	OrderStatus orderStatus;
	
	public OrdersEntity()
	{
		
	}

	public OrdersEntity(UUID orderId, UUID productId, Integer quantity
		,String productName, OrderStatus orderStatus) {
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
		this.productName = productName;
	}

	public UUID getOrderId() {
		return orderId;
	}

	public void setOrderId(UUID orderId) {
		this.orderId = orderId;
	}

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	
}
