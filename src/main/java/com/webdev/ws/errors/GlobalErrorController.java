package com.webdev.ws.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalErrorController {

	
	@ExceptionHandler(GlobalErrors.class)
	public ResponseEntity<String> orderNotFoundException(Exception e)
	{
		return new ResponseEntity<>("GlobalError"+e.getMessage(),HttpStatus.NOT_FOUND);
	}
}
