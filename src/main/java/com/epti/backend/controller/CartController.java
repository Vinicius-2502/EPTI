package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import com.epti.backend.dto.ecommerce.AddToCartRequest;
import com.epti.backend.dto.ecommerce.UpdateCartRequest;
import com.epti.backend.exception.BadRequestException;
import com.epti.backend.model.CartItem;
import com.epti.backend.service.CartService;
import com.epti.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "Cart management APIs")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user cart", description = "Retrieve authenticated user's cart items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<CartItem>>> getUserCart() {
        var currentUser = userService.getCurrentUser();
        log.info("Fetching cart for user: {}", currentUser.getUsername());

        List<CartItem> cartItems = cartService.getUserCart(currentUser);

        BaseResponse<List<CartItem>> response = BaseResponse.success(cartItems, "Cart retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    @Operation(summary = "Get cart items count", description = "Get total number of items in user's cart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Map<String, Object>>> getCartCount() {
        var currentUser = userService.getCurrentUser();
        long itemCount = cartService.getCartItemsCount(currentUser);

        Map<String, Object> data = Map.of(
            "count", itemCount,
            "maxItems", 50,
            "canAddMore", itemCount < 50
        );

        return ResponseEntity.ok(BaseResponse.success(data, "Cart count retrieved successfully"));
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart", description = "Add product or kit to user's cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Item added to cart"),
        @ApiResponse(responseCode = "400", description = "Invalid request or cart limit exceeded"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Product or kit not found")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<CartItem>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        var currentUser = userService.getCurrentUser();
        log.info("Adding item to cart for user: {} - Item: {}, Type: {}, Quantity: {}",
                currentUser.getUsername(), request.getItemId(), request.getItemType(), request.getQuantity());

        CartItem cartItem;

        switch (request.getItemType().toUpperCase()) {
            case "PRODUCT":
                cartItem = cartService.addProductToCart(currentUser, request.getItemId(), request.getQuantity());
                break;
            case "KIT":
                cartItem = cartService.addKitToCart(currentUser, request.getItemId(), request.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Invalid item type. Must be 'PRODUCT' or 'KIT'");
        }

        BaseResponse<CartItem> response = BaseResponse.success(cartItem, "Item added to cart successfully");
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update cart item quantity", description = "Update quantity of an item in the cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart item updated"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity or cart limit exceeded"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<CartItem>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartRequest request) {

        var currentUser = userService.getCurrentUser();
        log.info("Updating cart item {} quantity to {} for user: {}",
                cartItemId, request.getQuantity(), currentUser.getUsername());

        CartItem updatedItem = cartService.updateCartItemQuantity(currentUser, cartItemId, request.getQuantity());

        BaseResponse<CartItem> response = BaseResponse.success(updatedItem, "Cart item updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove item from cart", description = "Remove an item from the cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removed from cart"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Object>> removeFromCart(@PathVariable Long cartItemId) {
        var currentUser = userService.getCurrentUser();
        log.info("Removing cart item {} for user: {}", cartItemId, currentUser.getUsername());

        cartService.removeFromCart(currentUser, cartItemId);

        BaseResponse<Object> response = BaseResponse.success("Item removed from cart successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from the cart")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cart cleared"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Object>> clearCart() {
        var currentUser = userService.getCurrentUser();
        log.info("Clearing cart for user: {}", currentUser.getUsername());

        cartService.clearCart(currentUser);

        BaseResponse<Object> response = BaseResponse.success("Cart cleared successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout")
    @Operation(summary = "Get cart for checkout", description = "Get cart items ready for checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<CartItem>>> getCartForCheckout() {
        var currentUser = userService.getCurrentUser();

        if (cartService.isCartEmpty(currentUser)) {
            throw new BadRequestException("Cannot checkout with empty cart");
        }

        List<CartItem> cartItems = cartService.getCartItemsForCheckout(currentUser);

        return ResponseEntity.ok(BaseResponse.success(cartItems, "Cart ready for checkout"));
    }
}
