package com.usta.reparacionautomotriz.models.daos;

import com.usta.reparacionautomotriz.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    // Validación de correo al editar
    boolean existsByEmailAndIdUsuarioNot(String email, Long idUsuario);

    // Método con dos parámetros (por si lo usas en otro lado)
    List<UserEntity> findByRol_RolAndEstadoUsuTrue(String rol, Boolean estadoUsu);

    // NUEVO: Agrega este método exacto para que soporte la llamada con un solo parámetro
    List<UserEntity> findByRol_Rol(String rol);
}