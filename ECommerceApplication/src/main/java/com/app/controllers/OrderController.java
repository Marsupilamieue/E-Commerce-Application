package com.app.controllers;

import java.util.List;

import com.app.entites.Address;
import com.app.exceptions.APIException;
import com.app.payloads.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.config.AppConstants;
import com.app.services.OrderService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class OrderController {
	
	@Autowired
	public OrderService orderService;

	@PostMapping("/public/users/{email}/carts/{cartId}/order")
	public ResponseEntity<OrderDTO> orderProducts(
			@PathVariable String email,
			@PathVariable Long cartId,
			@RequestBody OrderRequestDTO orderRequest
	) {
		OrderDTO order = orderService.placeOrder(
				email,
				cartId,
				orderRequest.getPaymentMethod(),
				orderRequest.getAddressDTO(),
				orderRequest.getBankTransferDTO()
		);
		return new ResponseEntity<>(order, HttpStatus.CREATED);
	}

	@GetMapping("/admin/orders")
	public ResponseEntity<OrderResponse> getAllOrders(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		
		OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);

		return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.FOUND);
	}
	
	@GetMapping("public/users/{email}/orders")
	public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable String email) {
		List<OrderDTO> orders = orderService.getOrdersByUser(email);
		
		return new ResponseEntity<List<OrderDTO>>(orders, HttpStatus.FOUND);
	}
	
	@GetMapping("public/users/{email}/orders/{orderId}")
	public ResponseEntity<OrderDTO> getOrderByUser(@PathVariable String email, @PathVariable Long orderId) {
		OrderDTO order = orderService.getOrder(email, orderId);
		
		return new ResponseEntity<OrderDTO>(order, HttpStatus.FOUND);
	}
	
	@PutMapping("admin/users/{email}/orders/{orderId}/orderStatus/{orderStatus}")
	public ResponseEntity<OrderDTO> updateOrderByUser(@PathVariable String email, @PathVariable Long orderId, @PathVariable String orderStatus) {
		OrderDTO order = orderService.updateOrder(email, orderId, orderStatus);
		
		return new ResponseEntity<OrderDTO>(order, HttpStatus.OK);
	}

}
