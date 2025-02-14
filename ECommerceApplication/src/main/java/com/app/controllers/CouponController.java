package com.app.controllers;

import com.app.entites.Coupon;
import com.app.payloads.CouponDTO;
import com.app.payloads.CouponResponse;
import com.app.services.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.config.AppConstants;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/admin/coupon")
    public ResponseEntity<CouponDTO> createCoupon(@Valid @RequestBody Coupon coupon) {
        CouponDTO savedCouponDTO = couponService.createCoupon(coupon);

        return new ResponseEntity<CouponDTO>(savedCouponDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/coupons")
    public ResponseEntity<CouponResponse> getCoupons(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_COUPONS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        CouponResponse couponResponse = couponService.getCoupons(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<CouponResponse>(couponResponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/coupon/{couponId}")
    public ResponseEntity<CouponDTO> updateCoupon(@RequestBody Coupon coupon,
            @PathVariable Long couponId) {
        CouponDTO couponDTO = couponService.updateCoupon(coupon, couponId);

        return new ResponseEntity<CouponDTO>(couponDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/coupon/{couponId}")
    public ResponseEntity<String> deleteCoupon(@PathVariable Long couponId) {
        String status = couponService.deleteCoupon(couponId);

        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}