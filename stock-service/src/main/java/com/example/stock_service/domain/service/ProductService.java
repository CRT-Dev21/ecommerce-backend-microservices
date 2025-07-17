package com.example.stock_service.domain.service;

import com.example.stock_service.application.dto.request.CreateProductRequest;
import com.example.stock_service.application.dto.response.ProductResponse;
import com.example.stock_service.application.mapper.ProductMapper;
import com.example.stock_service.model.ProductStock;
import com.example.stock_service.repository.ProductStockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {
    private final ProductStockRepository repository;
    private final FileStorageService fileStorageService;

    public ProductService(ProductStockRepository repository, FileStorageService fileStorageService) {
        this.repository = repository;
        this.fileStorageService = fileStorageService;
    }

    public Mono<List<ProductResponse>> getAvailableProducts(int page, int size) {
        int offset = page * size;
        return repository.findAvailableProducts(size, offset)
                .map(ProductResponse::new)
                .collectList();
    }

    public Mono<ProductResponse> getProductByCode(String productCode) {
        return repository.findByProductCode(productCode)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .map(ProductMapper::mapperProductStockToResponse);
    }

    public Mono<List<ProductResponse>> getSellerProducts(UUID sellerId) {
        return repository.findBySellerId(sellerId)
                .map(ProductResponse::new)
                .collectList();
    }

    @Transactional
    public Mono<ProductResponse> createProduct(CreateProductRequest request, FilePart imageFile, UUID sellerId) {

        ProductStock product = ProductMapper.mapperRequestToProductStock(request, sellerId);

        product.prepareForSave();

        return repository.save(product)
                .doOnNext(ProductStock::postSave)
                .flatMap(savedProduct ->
                        fileStorageService.storeProductImage(imageFile, savedProduct)
                                .flatMap(imageName -> {
                                    savedProduct.setImageName(imageName);
                                    return repository.save(savedProduct);
                                })
                )
                .map(ProductResponse::new)
                .onErrorResume(e -> {
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating product"));
                });
    }
}