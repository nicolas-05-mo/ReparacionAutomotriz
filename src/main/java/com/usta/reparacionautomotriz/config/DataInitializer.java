package com.usta.reparacionautomotriz.config;

import com.usta.reparacionautomotriz.entities.RoleEntity;
import com.usta.reparacionautomotriz.entities.UserEntity;
import com.usta.reparacionautomotriz.models.daos.RoleDao;
import com.usta.reparacionautomotriz.models.daos.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Datos iniciales: roles y usuarios de prueba. Las claves se guardan con hash BCrypt. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleDao roleDao;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final String adminPassword;
    private final String mecanicoPassword;

    public DataInitializer(RoleDao roleDao, UserDao userDao, PasswordEncoder passwordEncoder,
                           @Value("${app.admin.password:Admin2026*}") String adminPassword,
                           @Value("${app.mecanico.password:Mecanico2026*}") String mecanicoPassword) {
        this.roleDao = roleDao;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.adminPassword = adminPassword;
        this.mecanicoPassword = mecanicoPassword;
    }

    @Override
    public void run(String... args) {
        RoleEntity admin = roleDao.findByRol("ADMINISTRADOR")
                .orElseGet(() -> roleDao.save(new RoleEntity("ADMINISTRADOR")));
        RoleEntity mecanico = roleDao.findByRol("MECANICO")
                .orElseGet(() -> roleDao.save(new RoleEntity("MECANICO")));

        crearSiNoExiste("admin@taller.com", "Administrador", "Taller", "3000000000", adminPassword, admin);
        crearSiNoExiste("mecanico@taller.com", "Mecánico", "Prueba", "3000000001", mecanicoPassword, mecanico);
    }

    private void crearSiNoExiste(String email, String nombre, String apellido, String telefono,
                                 String clave, RoleEntity rol) {
        if (userDao.existsByEmail(email)) {
            return;
        }
        UserEntity u = new UserEntity();
        u.setEmail(email);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setTelefono(telefono);
        u.setClave(passwordEncoder.encode(clave));
        u.setEstadoUsu(true);
        u.setRol(rol);
        userDao.save(u);
    }
}
