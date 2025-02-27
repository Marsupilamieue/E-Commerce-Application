package com.app.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.app.entites.Product;
import com.app.payloads.ProductDTO;
import com.app.payloads.ProductResponse;

public interface ProductService {

	ProductDTO addProduct(Long categoryId, Long brandId, Product product);

	String applyCoupon(Long productId, Long couponId);

	ProductDTO removeCoupon(Long productId, Long couponId);

	ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	ProductResponse searchByBrand(Long brandId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	ProductResponse searchByCoupon(Long couponId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	ProductDTO updateProduct(Long productId, Product product);

	ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;

	ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder);

	String deleteProduct(Long productId);

}