package com.webdev.ws.dto;

import java.util.UUID;



public class OrdersDTO {

	
	private UUID productId;
	private Integer quantity;
	private String productName;
	public OrdersDTO() {

	}
	public OrdersDTO(UUID productId, Integer quantity,String productName) {
		
		this.productId = productId;
		this.quantity = quantity;
		this.productName = productName;
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
	
	
}
