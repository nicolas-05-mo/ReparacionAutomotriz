package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.RoleEntity;

import java.util.List;

public interface RoleService {
    List<RoleEntity> findAll();
}
