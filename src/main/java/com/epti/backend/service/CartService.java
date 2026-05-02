package com.epti.backend.service;

import com.epti.backend.exception.BadRequestException;
import com.epti.backend.exception.ResourceNotFoundException;
import com.epti.backend.model.CartItem;
import com.epti.backend.model.Kit;
import com.epti.backend.model.Product;
import com.epti.backend.model.User;
import com.epti.backend.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final KitService kitService;
    private final AuditService auditService;

    private static final int MAX_CART_ITEMS = 50;

    @Transactional(readOnly = true)
    public List<CartItem> getUserCart(User user) {
        log.info("Fetching cart for user: {}", user.getUsername());
        return cartItemRepository.findValidCartItemsByUser(user);
    }

    @Transactional(readOnly = true)
    public long getCartItemsCount(User user) {
        log.info("Counting cart items for user: {}", user.getUsername());
        Long totalQuantity = cartItemRepository.sumQuantityByUser(user);
        return totalQuantity != null ? totalQuantity : 0;
    }

    public CartItem addProductToCart(User user, Long productId, Integer quantity) {
        log.info("Adding product {} to cart for user: {} with quantity: {}", productId, user.getUsername(), quantity);
        
        // Validate product
        Product product = productService.getProductByIdAndValidate(productId, user.getTurma());
        
        // Check cart limit
        if (getCartItemsCount(user) + quantity > MAX_CART_ITEMS) {
            throw new BadRequestException("Cannot add more than " + MAX_CART_ITEMS + " items to cart");
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductId(user, productId);
        
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (getCartItemsCount(user) - cartItem.getQuantity() + newQuantity > MAX_CART_ITEMS) {
                throw new BadRequestException("Cannot add more than " + MAX_CART_ITEMS + " items to cart");
            }
            
            cartItem.setQuantity(newQuantity);
            CartItem savedItem = cartItemRepository.save(cartItem);
            
            auditService.logAction("CART_ITEM_UPDATED", user, "Added " + quantity + " more of product " + product.getName());
            return savedItem;
        } else {
            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
            
            CartItem savedItem = cartItemRepository.save(cartItem);
            
            auditService.logAction("CART_ITEM_ADDED", user, "Added product " + product.getName() + " with quantity " + quantity);
            return savedItem;
        }
    }

    public CartItem addKitToCart(User user, Long kitId, Integer quantity) {
        log.info("Adding kit {} to cart for user: {} with quantity: {}", kitId, user.getUsername(), quantity);
        
        // Validate kit
        Kit kit = kitService.getKitByIdAndValidate(kitId, user.getTurma());
        
        // Check cart limit
        if (getCartItemsCount(user) + quantity > MAX_CART_ITEMS) {
            throw new BadRequestException("Cannot add more than " + MAX_CART_ITEMS + " items to cart");
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndKitId(user, kitId);
        
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            
            if (getCartItemsCount(user) - cartItem.getQuantity() + newQuantity > MAX_CART_ITEMS) {
                throw new BadRequestException("Cannot add more than " + MAX_CART_ITEMS + " items to cart");
            }
            
            cartItem.setQuantity(newQuantity);
            CartItem savedItem = cartItemRepository.save(cartItem);
            
            auditService.logAction("CART_ITEM_UPDATED", user, "Added " + quantity + " more of kit " + kit.getName());
            return savedItem;
        } else {
            CartItem cartItem = CartItem.builder()
                    .user(user)
                    .kit(kit)
                    .quantity(quantity)
                    .build();
            
            CartItem savedItem = cartItemRepository.save(cartItem);
            
            auditService.logAction("CART_ITEM_ADDED", user, "Added kit " + kit.getName() + " with quantity " + quantity);
            return savedItem;
        }
    }

    public CartItem updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        log.info("Updating cart item {} quantity to {} for user: {}", cartItemId, quantity, user.getUsername());
        
        CartItem cartItem = getCartItemByIdAndUser(cartItemId, user);
        
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }
        
        // Check cart limit
        long currentCartCount = getCartItemsCount(user) - cartItem.getQuantity();
        if (currentCartCount + quantity > MAX_CART_ITEMS) {
            throw new BadRequestException("Cannot add more than " + MAX_CART_ITEMS + " items to cart");
        }
        
        String itemName = cartItem.getItemName();
        cartItem.setQuantity(quantity);
        CartItem savedItem = cartItemRepository.save(cartItem);
        
        auditService.logAction("CART_ITEM_UPDATED", user, "Updated quantity of " + itemName + " to " + quantity);
        return savedItem;
    }

    public void removeFromCart(User user, Long cartItemId) {
        log.info("Removing cart item {} for user: {}", cartItemId, user.getUsername());
        
        CartItem cartItem = getCartItemByIdAndUser(cartItemId, user);
        String itemName = cartItem.getItemName();
        
        cartItemRepository.delete(cartItem);
        
        auditService.logAction("CART_ITEM_REMOVED", user, "Removed " + itemName + " from cart");
    }

    public void clearCart(User user) {
        log.info("Clearing cart for user: {}", user.getUsername());
        
        cartItemRepository.deleteByUser(user);
        
        auditService.logAction("CART_CLEARED", user, "Cleared entire cart");
    }

    @Transactional(readOnly = true)
    public CartItem getCartItemByIdAndUser(Long cartItemId, User user) {
        return cartItemRepository.findById(cartItemId)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "id", cartItemId));
    }

    @Transactional(readOnly = true)
    public List<CartItem> getCartItemsForCheckout(User user) {
        log.info("Getting cart items for checkout for user: {}", user.getUsername());
        return cartItemRepository.findCartItemsForCheckout(user);
    }

    @Transactional(readOnly = true)
    public boolean isCartEmpty(User user) {
        return cartItemRepository.countByUser(user) == 0;
    }

    @Transactional(readOnly = true)
    public boolean canUserAddToCart(User user, Integer quantity) {
        return getCartItemsCount(user) + quantity <= MAX_CART_ITEMS;
    }
}
