package com.app.services;

import java.util.List;

import com.app.entites.Address;
import com.app.payloads.AddressDTO;
import com.app.payloads.BankTransferDTO;
import com.app.payloads.OrderDTO;
import com.app.payloads.OrderResponse;

public interface OrderService {
	
	OrderDTO placeOrder(String email, Long cartId, String paymentMethod, AddressDTO addressDTO, BankTransferDTO bankTransferDTO);
	
	OrderDTO getOrder(String email, Long orderId);
	
	List<OrderDTO> getOrdersByUser(String email);
	
	OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	OrderDTO updateOrder(String email, Long orderId, String orderStatus);
}
