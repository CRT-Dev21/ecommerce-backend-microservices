package com.example.stock_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("product_stocks")
public class ProductStock {
    @Id
    private Long id;

    private String productName;
    private String description;
    private Double price;
    private Integer qty;
    private String category;
    private String imageName;
    private Integer minimumStock;
    private String location;

    @Column("seller_id")
    private UUID sellerId;

    @Column("product_code")
    private String productCode;

    @Column("version")
    private Integer version = 0;

    @Transient
    private boolean isNew = true;

    public String generateProductCode() {
        if (this.id != null) {
            return "PROD-" + String.format("%03d", this.id);
        }
        return "TEMP-" + System.currentTimeMillis();
    }

    public void prepareForSave() {
        if (this.productCode == null) {
            this.productCode = generateProductCode();
        }
    }

    public void postSave() {
        if (this.productCode.startsWith("TEMP-") && this.id != null) {
            this.productCode = "PROD-" + String.format("%03d", this.id);
        }
        this.isNew = false;
    }

    public String generateImageName() {
        return this.productCode + ".jpg";
    }

    public void reduceStock(int qty) {
        if(this.qty < qty) {
            throw new IllegalStateException("Insufficient Stock");
        }
        this.qty -= qty;
    }

    public void increaseStock(int qty) {
        this.qty += qty;
    }

}
