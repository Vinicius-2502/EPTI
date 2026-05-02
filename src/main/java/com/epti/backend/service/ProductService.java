package com.epti.backend.service;

import com.epti.backend.exception.BadRequestException;
import com.epti.backend.exception.ResourceNotFoundException;
import com.epti.backend.model.Product;
import com.epti.backend.model.enums.Turma;
import com.epti.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "'all_available'")
    @Transactional(readOnly = true)
    public List<Product> getAllAvailableProducts() {
        log.info("Fetching all available products");
        return productRepository.findByAvailableTrue();
    }

    @Cacheable(value = "products", key = "'turma_' + #turma.name()")
    @Transactional(readOnly = true)
    public List<Product> getAvailableProductsForTurma(Turma turma) {
        log.info("Fetching available products for turma: {}", turma);
        return productRepository.findAvailableForTurma(turma);
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProductsForTurmaOrdered(Turma turma, String sortBy) {
        log.info("Fetching available products for turma: {} ordered by: {}", turma, sortBy);

        switch (sortBy.toLowerCase()) {
            case "price_asc":
                return productRepository.findAvailableForTurmaOrderByPriceAsc(turma);
            case "price_desc":
                return productRepository.findAvailableForTurmaOrderByPriceDesc(turma);
            default:
                return productRepository.findAvailableForTurma(turma);
        }
    }

    @Transactional(readOnly = true)
    public List<Product> searchProducts(String name, Turma turma) {
        log.info("Searching products with name: {} for turma: {}", name, turma);

        if (turma != null) {
            return productRepository.findAvailableForTurmaByNameContaining(turma, name);
        } else {
            return productRepository.findAvailableByNameContaining(name);
        }
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Transactional(readOnly = true)
    public Product getProductByIdAndValidate(Long id, Turma turma) {
        log.info("Fetching and validating product with id: {} for turma: {}", id, turma);

        Product product = getProductById(id);

        if (!product.isAvailable()) {
            throw new BadRequestException("Product is not available");
        }

        if (!product.isAvailableForTurma(turma)) {
            throw new BadRequestException("Product is not available for your turma");
        }

        return product;
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());

        if (productRepository.existsByName(product.getName())) {
            throw new BadRequestException("Product with name " + product.getName() + " already exists");
        }

        product.setAvailable(true);
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, Product productDetails) {
        log.info("Updating product with id: {}", id);

        Product product = getProductById(id);

        if (!product.getName().equals(productDetails.getName()) &&
            productRepository.existsByName(productDetails.getName())) {
            throw new BadRequestException("Product with name " + productDetails.getName() + " already exists");
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setImageUrl(productDetails.getImageUrl());
        product.setAvailable(productDetails.isAvailable());
        product.setAllowedTurmas(productDetails.getAllowedTurmas());
        product.setKitDiscountPercentage(productDetails.getKitDiscountPercentage());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public Product toggleProductAvailability(Long id) {
        log.info("Toggling availability for product with id: {}", id);

        Product product = getProductById(id);
        product.setAvailable(!product.isAvailable());

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public boolean isProductAvailableForTurma(Long productId, Turma turma) {
        Product product = productRepository.findById(productId).orElse(null);
        return product != null && product.isAvailable() && product.isAvailableForTurma(turma);
    }
}
