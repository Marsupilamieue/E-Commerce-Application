package com.app.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.app.entites.*;
import com.app.payloads.*;
import com.app.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.exceptions.APIException;
import com.app.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	public UserRepo userRepo;

	@Autowired
	public CartRepo cartRepo;

	@Autowired
	public OrderRepo orderRepo;

	@Autowired
	private PaymentRepo paymentRepo;

	@Autowired
	public OrderItemRepo orderItemRepo;

	@Autowired
	private AddressRepo addressRepo;

	@Autowired
	public CartItemRepo cartItemRepo;

	@Autowired
	public UserService userService;

	@Autowired
	public BankRepo bankRepo;

	@Autowired
	public CartService cartService;

	@Autowired
	public ModelMapper modelMapper;

	@Override
	public OrderDTO placeOrder(String email, Long cartId, String paymentMethod, AddressDTO addressDTO, BankTransferDTO bankTransferDTO) {
		Cart cart = cartRepo.findCartByEmailAndCartId(email, cartId);

		if (cart == null) {
			throw new ResourceNotFoundException("Cart", "cartId", cartId);
		}

		if (!paymentMethod.equalsIgnoreCase("COD") && !paymentMethod.equalsIgnoreCase("BANK_TRANSFER")) {
			throw new APIException("Invalid payment method. Only 'COD' and 'BANK_TRANSFER' are allowed.");
		}

		Order order = new Order();
		order.setEmail(email);
		order.setOrderDate(LocalDate.now());
		order.setTotalAmount(cart.getTotalPrice());
		order.setOrderStatus("Order Accepted !");

		if (paymentMethod.equalsIgnoreCase("COD")) {
			if (addressDTO == null) {
				throw new APIException("Address is required for COD payment.");
			}

			Address address = new Address();
			address.setStreet(addressDTO.getStreet());
			address.setBuildingName(addressDTO.getBuildingName());
			address.setCity(addressDTO.getCity());
			address.setState(addressDTO.getState());
			address.setCountry(addressDTO.getCountry());
			address.setPincode(addressDTO.getPincode());

			address = addressRepo.save(address);
			order.setAddress(address);
		}
		System.out.println(bankTransferDTO);
		Bank bank = null;
		if (paymentMethod.equalsIgnoreCase("BANK_TRANSFER")) {
			if (bankTransferDTO == null || bankTransferDTO.getBankName() == null) {
				throw new APIException("Bank selection is required for BANK_TRANSFER payment.");
			}

			bank = bankRepo.findByName(bankTransferDTO.getBankName());
			if (bank == null) {
				throw new APIException("Unsupported bank: " + bankTransferDTO.getBankName());
			}
		}

		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(paymentMethod);

		if (paymentMethod.equalsIgnoreCase("BANK_TRANSFER")) {
			payment.setBank(bank);
		} else {
			payment.setBank(null);
		}

		payment = paymentRepo.save(payment);
		order.setPayment(payment);
		Order savedOrder = orderRepo.save(order);

		List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setDiscount(cartItem.getDiscount());
			orderItem.setOrderedProductPrice(cartItem.getProductPrice());
			orderItem.setOrder(savedOrder);
			return orderItem;
		}).collect(Collectors.toList());

		orderItemRepo.saveAll(orderItems);

		cart.getCartItems().forEach(item -> {
			int quantity = item.getQuantity();
			Product product = item.getProduct();
			cartService.deleteProductFromCart(cartId, product.getProductId());
			product.setQuantity(product.getQuantity() - quantity);
		});

		OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
		orderDTO.setOrderItems(orderItems.stream().map(item -> modelMapper.map(item, OrderItemDTO.class)).collect(Collectors.toList()));

		if (paymentMethod.equalsIgnoreCase("BANK_TRANSFER")) {
			BankTransferDTO bankTransferResponse = new BankTransferDTO();
			bankTransferResponse.setBankName(bank.getName());
			bankTransferResponse.setStoreAccountNumber(bank.getStoreAccountNumber());
			orderDTO.setBankTransfer(bankTransferResponse);
		}

		return orderDTO;
	}

	@Override
	public List<OrderDTO> getOrdersByUser(String email) {
		List<Order> orders = orderRepo.findAllByEmail(email);

		List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());

		if (orderDTOs.size() == 0) {
			throw new APIException("No orders placed yet by the user with email: " + email);
		}

		return orderDTOs;
	}

	@Override
	public OrderDTO getOrder(String email, Long orderId) {

		Order order = orderRepo.findOrderByEmailAndOrderId(email, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

		return modelMapper.map(order, OrderDTO.class);
	}

	@Override
	public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

		Page<Order> pageOrders = orderRepo.findAll(pageDetails);

		List<Order> orders = pageOrders.getContent();

		List<OrderDTO> orderDTOs = orders.stream().map(order -> modelMapper.map(order, OrderDTO.class))
				.collect(Collectors.toList());
		
		if (orderDTOs.size() == 0) {
			throw new APIException("No orders placed yet by the users");
		}

		OrderResponse orderResponse = new OrderResponse();
		
		orderResponse.setContent(orderDTOs);
		orderResponse.setPageNumber(pageOrders.getNumber());
		orderResponse.setPageSize(pageOrders.getSize());
		orderResponse.setTotalElements(pageOrders.getTotalElements());
		orderResponse.setTotalPages(pageOrders.getTotalPages());
		orderResponse.setLastPage(pageOrders.isLast());
		
		return orderResponse;
	}

	@Override
	public OrderDTO updateOrder(String email, Long orderId, String orderStatus) {

		Order order = orderRepo.findOrderByEmailAndOrderId(email, orderId);

		if (order == null) {
			throw new ResourceNotFoundException("Order", "orderId", orderId);
		}

		order.setOrderStatus(orderStatus);

		return modelMapper.map(order, OrderDTO.class);
	}

}
