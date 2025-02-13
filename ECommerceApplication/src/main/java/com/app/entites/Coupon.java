package com.app.entites;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon")
@Data
@NoArgsConstructor
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long couponId;

    @NotBlank
    @Size(min = 3, message = "Coupon name must contain atleast 3 characters")
    private String couponName;

    @NotBlank
    @Size(min = 6, message = "Coupon description must contain atleast 6 characters")
    private String description;

    @OneToMany(mappedBy = "coupon", cascade =  CascadeType.ALL )
    private List<Product> products;
}
