package com.webdev.ws.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import com.webdev.ws.model.OrdersEntity;

@Repository
public interface OrdersRepository extends JpaRepositoryImplementation<OrdersEntity,String> {

}
