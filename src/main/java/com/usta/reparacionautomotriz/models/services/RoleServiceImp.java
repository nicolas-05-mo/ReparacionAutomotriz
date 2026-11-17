package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.RoleEntity;
import com.usta.reparacionautomotriz.models.daos.RoleDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImp implements RoleService {

    private final RoleDao roleDao;

    public RoleServiceImp(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> findAll() {
        return roleDao.findAll();
    }
}
