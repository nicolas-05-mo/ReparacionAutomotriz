package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.dto.UsuarioForm;
import com.usta.reparacionautomotriz.entities.RoleEntity;
import com.usta.reparacionautomotriz.entities.UserEntity;

import java.util.List;

public interface UsuarioService {
    List<UserEntity> findAll();
    UserEntity findById(Long id);
    UserEntity findByEmail(String email);
    UsuarioForm obtenerForm(Long id);
    void crear(UsuarioForm form);
    void actualizar(UsuarioForm form);
    void changeState(Long id, String emailActual);
    List<UserEntity> listarMecanicos();
    List<RoleEntity> listarRoles();
}
