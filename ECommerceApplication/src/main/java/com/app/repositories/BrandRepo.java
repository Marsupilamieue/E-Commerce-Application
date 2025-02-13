package com.app.repositories;

import com.app.entites.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entites.Category;

@Repository
public interface BrandRepo extends JpaRepository<Brand, Long> {

    Brand findByBrandName(String brandName);

}
