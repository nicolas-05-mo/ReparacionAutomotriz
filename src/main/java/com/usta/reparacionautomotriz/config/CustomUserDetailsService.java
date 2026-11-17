package com.usta.reparacionautomotriz.config;

import com.usta.reparacionautomotriz.entities.UserEntity;
import com.usta.reparacionautomotriz.models.daos.UserDao;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity u = userDao.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(u.getEmail())
                .password(u.getClave())
                .roles(u.getRol().getRol())            // ADMINISTRADOR -> ROLE_ADMINISTRADOR
                .disabled(!u.isEstadoUsu())            // usuario dado de baja no puede entrar
                .build();
    }
}
