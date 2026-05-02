package com.epti.backend.service;

import com.epti.backend.exception.BadRequestException;
import com.epti.backend.exception.ResourceNotFoundException;
import com.epti.backend.model.Role;
import com.epti.backend.repository.RoleRepository;
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
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
    }

    public Role createRole(Role role) {
        log.info("Creating new role: {}", role.getName());
        
        if (roleRepository.existsByName(role.getName())) {
            throw new BadRequestException("Role with name " + role.getName() + " already exists");
        }
        
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role roleDetails) {
        log.info("Updating role with id: {}", id);
        
        Role role = getRoleById(id);
        
        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.existsByName(roleDetails.getName())) {
            throw new BadRequestException("Role with name " + roleDetails.getName() + " already exists");
        }
        
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);
        
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
