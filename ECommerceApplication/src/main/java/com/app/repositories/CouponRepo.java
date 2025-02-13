package com.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entites.Coupon;

public interface CouponRepo extends JpaRepository<Coupon, Long> {

    Coupon findByCouponName(String couponName);
    
}
