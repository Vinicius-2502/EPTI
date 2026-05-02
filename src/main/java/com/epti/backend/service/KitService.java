package com.epti.backend.service;

import com.epti.backend.exception.BadRequestException;
import com.epti.backend.exception.ResourceNotFoundException;
import com.epti.backend.model.Kit;
import com.epti.backend.model.Product;
import com.epti.backend.model.enums.Turma;
import com.epti.backend.repository.KitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KitService {

    private final KitRepository kitRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<Kit> getAllAvailableKits() {
        log.info("Fetching all available kits");
        return kitRepository.findByAvailableTrue();
    }

    @Transactional(readOnly = true)
    public List<Kit> getAvailableKitsForTurma(Turma turma) {
        log.info("Fetching available kits for turma: {}", turma);
        return kitRepository.findAvailableForTurma(turma);
    }

    @Transactional(readOnly = true)
    public List<Kit> getAvailableKitsForTurmaOrdered(Turma turma, String sortBy) {
        log.info("Fetching available kits for turma: {} ordered by: {}", turma, sortBy);
        
        switch (sortBy.toLowerCase()) {
            case "price_asc":
                return kitRepository.findAvailableForTurmaOrderByPriceAsc(turma);
            case "price_desc":
                return kitRepository.findAvailableForTurmaOrderByPriceDesc(turma);
            default:
                return kitRepository.findAvailableForTurma(turma);
        }
    }

    @Transactional(readOnly = true)
    public List<Kit> searchKits(String name, Turma turma) {
        log.info("Searching kits with name: {} for turma: {}", name, turma);
        
        if (turma != null) {
            return kitRepository.findAvailableForTurmaByNameContaining(turma, name);
        } else {
            return kitRepository.findAvailableByNameContaining(name);
        }
    }

    @Transactional(readOnly = true)
    public Kit getKitById(Long id) {
        log.info("Fetching kit with id: {}", id);
        return kitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kit", "id", id));
    }

    @Transactional(readOnly = true)
    public Kit getKitByIdAndValidate(Long id, Turma turma) {
        log.info("Fetching and validating kit with id: {} for turma: {}", id, turma);
        
        Kit kit = getKitById(id);
        
        if (!kit.isAvailable()) {
            throw new BadRequestException("Kit is not available");
        }
        
        if (!kit.isAvailableForTurma(turma)) {
            throw new BadRequestException("Kit is not available for your turma");
        }
        
        return kit;
    }

    public Kit createKit(Kit kit) {
        log.info("Creating new kit: {}", kit.getName());
        
        if (kitRepository.existsByName(kit.getName())) {
            throw new BadRequestException("Kit with name " + kit.getName() + " already exists");
        }
        
        // Validate products exist
        for (Product product : kit.getProducts()) {
            productService.getProductById(product.getId());
        }
        
        kit.setAvailable(true);
        return kitRepository.save(kit);
    }

    public Kit updateKit(Long id, Kit kitDetails) {
        log.info("Updating kit with id: {}", id);
        
        Kit kit = getKitById(id);
        
        if (!kit.getName().equals(kitDetails.getName()) && 
            kitRepository.existsByName(kitDetails.getName())) {
            throw new BadRequestException("Kit with name " + kitDetails.getName() + " already exists");
        }
        
        kit.setName(kitDetails.getName());
        kit.setDescription(kitDetails.getDescription());
        kit.setPrice(kitDetails.getPrice());
        kit.setImageUrl(kitDetails.getImageUrl());
        kit.setAvailable(kitDetails.isAvailable());
        kit.setAllowedTurmas(kitDetails.getAllowedTurmas());
        
        // Update products
        kit.getProducts().clear();
        if (kitDetails.getProducts() != null) {
            for (Product product : kitDetails.getProducts()) {
                Product validatedProduct = productService.getProductById(product.getId());
                kit.getProducts().add(validatedProduct);
            }
        }
        
        return kitRepository.save(kit);
    }

    public void deleteKit(Long id) {
        log.info("Deleting kit with id: {}", id);
        
        Kit kit = getKitById(id);
        kitRepository.delete(kit);
    }

    public Kit toggleKitAvailability(Long id) {
        log.info("Toggling availability for kit with id: {}", id);
        
        Kit kit = getKitById(id);
        kit.setAvailable(!kit.isAvailable());
        
        return kitRepository.save(kit);
    }

    @Transactional(readOnly = true)
    public boolean isKitAvailableForTurma(Long kitId, Turma turma) {
        Kit kit = kitRepository.findById(kitId).orElse(null);
        return kit != null && kit.isAvailable() && kit.isAvailableForTurma(turma);
    }

    @Transactional(readOnly = true)
    public List<Kit> findKitsContainingProduct(Long productId) {
        log.info("Finding kits containing product with id: {}", productId);
        return kitRepository.findKitsContainingProduct(productId);
    }
}
