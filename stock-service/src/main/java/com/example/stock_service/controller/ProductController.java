package com.example.stock_service.controller;

import com.example.stock_service.application.dto.request.CreateProductRequest;
import com.example.stock_service.application.dto.response.ProductResponse;
import com.example.stock_service.domain.service.ImageService;
import com.example.stock_service.domain.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stockService")
@Slf4j
public class ProductController {
    private final ProductService productService;
    private final ImageService imageService;

    public ProductController(ProductService productService, ImageService imageService) {
        this.productService = productService;
        this.imageService = imageService;
    }

    @GetMapping
    public Mono<ResponseEntity<List<ProductResponse>>> getAllAvailableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAvailableProducts(page, size)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{productCode}")
    public Mono<ResponseEntity<ProductResponse>> getProductByCode(@PathVariable String productCode) {
        return productService.getProductByCode(productCode)
                .map(ResponseEntity::ok)
                .onErrorResume(ResponseStatusException.class, e -> Mono.just(ResponseEntity.status(e.getStatusCode()).build()));
    }

    @GetMapping("/image/{productCode}")
    public ResponseEntity<byte[]> getImage(@PathVariable String productCode) {
        return imageService.getImage(productCode);
    }

    @GetMapping("/seller")
    public Mono<ResponseEntity<List<ProductResponse>>> getSellerProducts(ServerWebExchange exchange) {
        String sellerId = (String) exchange.getAttribute("userId");

        if (sellerId == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return productService.getSellerProducts(UUID.fromString(sellerId))
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/seller/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ProductResponse>> createProduct(
            ServerWebExchange exchange,
            @RequestPart("request") CreateProductRequest request,
            @RequestPart("image") FilePart imageFile) {

        String sellerId = (String) exchange.getAttribute("userId");

        if (sellerId == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return productService.createProduct(request, imageFile, UUID.fromString(sellerId))
                .map(ResponseEntity::ok)
                .onErrorResume(ResponseStatusException.class, e -> Mono.just(ResponseEntity.status(e.getStatusCode()).build()));
    }
}