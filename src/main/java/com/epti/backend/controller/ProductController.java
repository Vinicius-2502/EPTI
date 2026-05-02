package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import com.epti.backend.model.Product;
import com.epti.backend.model.enums.Turma;
import com.epti.backend.service.ProductService;
import com.epti.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all available products", description = "Retrieve all available products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public ResponseEntity<BaseResponse<List<Product>>> getAllProducts() {
        log.info("Fetching all available products");

        List<Product> products = productService.getAllAvailableProducts();

        BaseResponse<List<Product>> response = BaseResponse.success(products, "Products retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-turma")
    @Operation(summary = "Get products for user's turma", description = "Retrieve products available for the authenticated user's turma")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<Product>>> getProductsForMyTurma(
            @RequestParam(defaultValue = "name") String sortBy) {

        var currentUser = userService.getCurrentUser();
        log.info("Fetching products for turma: {}", currentUser.getTurma());

        List<Product> products = productService.getAvailableProductsForTurmaOrdered(currentUser.getTurma(), sortBy);

        BaseResponse<List<Product>> response = BaseResponse.success(products, "Products for your turma retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/turma/{turma}")
    @Operation(summary = "Get products for specific turma", description = "Retrieve products available for a specific turma")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public ResponseEntity<BaseResponse<List<Product>>> getProductsForTurma(
            @Parameter(description = "Turma name") @PathVariable Turma turma,
            @RequestParam(defaultValue = "name") String sortBy) {

        log.info("Fetching products for turma: {}", turma);

        List<Product> products = productService.getAvailableProductsForTurmaOrdered(turma, sortBy);

        BaseResponse<List<Product>> response = BaseResponse.success(products, "Products for turma " + turma.getDisplayName() + " retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    })
    public ResponseEntity<BaseResponse<List<Product>>> searchProducts(
            @Parameter(description = "Search term") @RequestParam String name,
            @Parameter(description = "Filter by turma (optional)") @RequestParam(required = false) Turma turma) {

        log.info("Searching products with name: {} for turma: {}", name, turma);

        List<Product> products = productService.searchProducts(name, turma);

        BaseResponse<List<Product>> response = BaseResponse.success(products, "Products search completed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve product details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<BaseResponse<Product>> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {

        log.info("Fetching product with id: {}", id);

        Product product = productService.getProductById(id);

        BaseResponse<Product> response = BaseResponse.success(product, "Product retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate product for user", description = "Check if product is available for authenticated user's turma")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product validation completed"),
        @ApiResponse(responseCode = "400", description = "Product not available"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Product>> validateProductForUser(
            @Parameter(description = "Product ID") @PathVariable Long id) {

        var currentUser = userService.getCurrentUser();
        log.info("Validating product {} for user with turma: {}", id, currentUser.getTurma());

        Product product = productService.getProductByIdAndValidate(id, currentUser.getTurma());

        BaseResponse<Product> response = BaseResponse.success(product, "Product is available for your turma");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Product>> createProduct(@RequestBody Product product) {
        log.info("Creating new product: {}", product.getName());

        Product createdProduct = productService.createProduct(product);

        BaseResponse<Product> response = BaseResponse.success(createdProduct, "Product created successfully");
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Product>> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @RequestBody Product productDetails) {

        log.info("Updating product with id: {}", id);

        Product updatedProduct = productService.updateProduct(id, productDetails);

        BaseResponse<Product> response = BaseResponse.success(updatedProduct, "Product updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Object>> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {

        log.info("Deleting product with id: {}", id);

        productService.deleteProduct(id);

        BaseResponse<Object> response = BaseResponse.success("Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-availability")
    @Operation(summary = "Toggle product availability", description = "Toggle product availability (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product availability updated"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Product>> toggleProductAvailability(
            @Parameter(description = "Product ID") @PathVariable Long id) {

        log.info("Toggling availability for product with id: {}", id);

        Product product = productService.toggleProductAvailability(id);

        BaseResponse<Product> response = BaseResponse.success(product, "Product availability updated successfully");
        return ResponseEntity.ok(response);
    }
}
