package com.shopme.common.entity.product;

import com.shopme.common.Constants;
import com.shopme.common.IdBasedEntity;
import com.shopme.common.entity.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_images")
public class ProductImage extends IdBasedEntity {

    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductImage(Integer id,String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    @Transient
    public String getImagePath() {
        if (this.id == null || this.name.isEmpty() ) return Constants.S3_BASE_URI + "/images/default-user.png";
        return Constants.S3_BASE_URI + "/product-images/" + product.getId() +  "/extras/" + this.name;
    }
}
