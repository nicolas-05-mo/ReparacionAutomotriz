package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.dto.UsuarioForm;
import com.usta.reparacionautomotriz.entities.RoleEntity;
import com.usta.reparacionautomotriz.entities.UserEntity;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.daos.RoleDao;
import com.usta.reparacionautomotriz.models.daos.UserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImp implements UsuarioService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImp(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioForm obtenerForm(Long id) {
        return userDao.findById(id).map(u -> {
            UsuarioForm f = new UsuarioForm();
            f.setIdUsuario(u.getIdUsuario());
            f.setNombre(u.getNombre());
            f.setApellido(u.getApellido());
            f.setTelefono(u.getTelefono());
            f.setEmail(u.getEmail());
            f.setIdRol(u.getRol().getIdRol());
            return f;
        }).orElse(null);
    }

    @Override
    @Transactional
    public void crear(UsuarioForm f) {
        String email = f.getEmail().trim().toLowerCase();
        if (f.getClave() == null || f.getClave().isBlank()) {
            throw new ReglaNegocioException("clave", "La contraseña es obligatoria");
        }
        if (userDao.existsByEmail(email)) {
            throw new ReglaNegocioException("email", "Ya existe un usuario con ese correo");
        }
        RoleEntity rol = roleDao.findById(f.getIdRol())
                .orElseThrow(() -> new ReglaNegocioException("idRol", "Rol no válido"));

        UserEntity u = new UserEntity();
        u.setNombre(f.getNombre().trim());
        u.setApellido(f.getApellido().trim());
        u.setTelefono(f.getTelefono().trim());
        u.setEmail(email);
        u.setClave(passwordEncoder.encode(f.getClave()));      // RN-12: siempre hash BCrypt
        u.setEstadoUsu(true);
        u.setRol(rol);
        userDao.save(u);
    }

    @Override
    @Transactional
    public void actualizar(UsuarioForm f) {
        UserEntity u = userDao.findById(f.getIdUsuario())
                .orElseThrow(() -> new ReglaNegocioException(null, "Usuario no encontrado"));
        String email = f.getEmail().trim().toLowerCase();
        if (userDao.existsByEmailAndIdUsuarioNot(email, u.getIdUsuario())) {
            throw new ReglaNegocioException("email", "Ya existe un usuario con ese correo");
        }
        RoleEntity rol = roleDao.findById(f.getIdRol())
                .orElseThrow(() -> new ReglaNegocioException("idRol", "Rol no válido"));

        u.setNombre(f.getNombre().trim());
        u.setApellido(f.getApellido().trim());
        u.setTelefono(f.getTelefono().trim());
        u.setEmail(email);
        u.setRol(rol);
        if (f.getClave() != null && !f.getClave().isBlank()) {
            u.setClave(passwordEncoder.encode(f.getClave()));
        }
        userDao.save(u);
    }

    @Override
    @Transactional
    public void changeState(Long id, String emailActual) {
        UserEntity u = userDao.findById(id)
                .orElseThrow(() -> new ReglaNegocioException(null, "Usuario no encontrado"));
        if (u.getEmail().equalsIgnoreCase(emailActual) && u.isEstadoUsu()) {
            throw new ReglaNegocioException(null, "No puede darse de baja a sí mismo");
        }
        u.setEstadoUsu(!u.isEstadoUsu());
        userDao.save(u);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> listarMecanicos() {
        return userDao.findByRol_Rol("MECANICO");
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> listarRoles() {
        return roleDao.findAll();
    }
}
