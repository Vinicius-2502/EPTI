package com.epti.backend.controller;

import com.epti.backend.dto.BaseResponse;
import com.epti.backend.model.Kit;
import com.epti.backend.model.enums.Turma;
import com.epti.backend.service.KitService;
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
@RequestMapping("/kits")
@RequiredArgsConstructor
@Tag(name = "Kit Management", description = "Kit management APIs")
public class KitController {

    private final KitService kitService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all available kits", description = "Retrieve all available kits")
    public ResponseEntity<BaseResponse<List<Kit>>> getAllKits() {
        List<Kit> kits = kitService.getAllAvailableKits();
        return ResponseEntity.ok(BaseResponse.success(kits, "Kits retrieved successfully"));
    }

    @GetMapping("/my-turma")
    @Operation(summary = "Get kits for user's turma", description = "Retrieve kits available for authenticated user's turma")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<List<Kit>>> getKitsForMyTurma(
            @RequestParam(defaultValue = "name") String sortBy) {

        var currentUser = userService.getCurrentUser();
        List<Kit> kits = kitService.getAvailableKitsForTurmaOrdered(currentUser.getTurma(), sortBy);
        return ResponseEntity.ok(BaseResponse.success(kits, "Kits for your turma retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get kit by ID", description = "Retrieve kit details by ID")
    public ResponseEntity<BaseResponse<Kit>> getKitById(@PathVariable Long id) {
        Kit kit = kitService.getKitById(id);
        return ResponseEntity.ok(BaseResponse.success(kit, "Kit retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Create new kit", description = "Create a new kit (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Kit>> createKit(@RequestBody Kit kit) {
        Kit createdKit = kitService.createKit(kit);
        return ResponseEntity.status(201).body(BaseResponse.success(createdKit, "Kit created successfully"));
    }
}
