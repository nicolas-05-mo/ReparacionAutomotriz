package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.ReparacionEntity;
import com.usta.reparacionautomotriz.entities.UserEntity;
import com.usta.reparacionautomotriz.enums.EstadoReparacion;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.daos.ReparacionDao;
import com.usta.reparacionautomotriz.models.daos.UserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReparacionServiceImp implements ReparacionService {

    private static final String ROL_ADMIN = "ADMINISTRADOR";
    private static final String ROL_MECANICO = "MECANICO";

    private final ReparacionDao reparacionDao;
    private final UserDao userDao;

    public ReparacionServiceImp(ReparacionDao reparacionDao, UserDao userDao) {
        this.reparacionDao = reparacionDao;
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReparacionEntity> listarPara(String emailActual) {
        UserEntity actor = actor(emailActual);
        if (esAdmin(actor)) {
            return reparacionDao.findAll();
        }
        return reparacionDao.findByMecanico_IdUsuario(actor.getIdUsuario());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarEnProceso(String emailActual) {
        return listarPara(emailActual).stream()
                .filter(r -> r.getEstado() == EstadoReparacion.EN_PROCESO)
                .count();
    }

    @Override
    @Transactional(readOnly = true)
    public ReparacionEntity buscarPara(Long id, String emailActual) {
        UserEntity actor = actor(emailActual);
        ReparacionEntity r = reparacionDao.findById(id).orElse(null);
        if (r == null) {
            return null;
        }
        return puedeAcceder(actor, r) ? r : null;
    }

    @Override
    @Transactional
    public void crear(ReparacionEntity r, String emailActual) {
        UserEntity actor = actor(emailActual);
        if (!r.getVehiculo().isEstadoVehiculo()) {
            throw new ReglaNegocioException("vehiculo", "err.repair.vehicleDisabled");
        }
        if (esAdmin(actor)) {
            validarMecanico(r.getMecanico());
        } else {
            r.setMecanico(actor);          // el mecánico solo crea reparaciones propias
        }
        r.setIdReparacion(null);
        r.setEstado(EstadoReparacion.EN_PROCESO);   // RN-08: toda reparación nace EN_PROCESO
        reparacionDao.save(r);
    }

    @Override
    @Transactional
    public void actualizar(ReparacionEntity r, String emailActual) {
        UserEntity actor = actor(emailActual);
        ReparacionEntity actual = reparacionDao.findById(r.getIdReparacion())
                .orElseThrow(() -> new ReglaNegocioException(null, "err.repair.notFound"));
        if (!puedeAcceder(actor, actual)) {
            throw new ReglaNegocioException(null, "err.repair.notOwner");
        }
        if (actual.getEstado() != EstadoReparacion.EN_PROCESO) {
            throw new ReglaNegocioException(null, "err.repair.notEditable");
        }
        boolean cambioVehiculo = !r.getVehiculo().getIdVehiculo().equals(actual.getVehiculo().getIdVehiculo());
        if (cambioVehiculo && !r.getVehiculo().isEstadoVehiculo()) {
            throw new ReglaNegocioException("vehiculo", "err.repair.vehicleDisabled");
        }
        actual.setFecha(r.getFecha());
        actual.setTipo(r.getTipo());
        actual.setObservaciones(r.getObservaciones());
        actual.setVehiculo(r.getVehiculo());
        if (esAdmin(actor)) {
            validarMecanico(r.getMecanico());
            actual.setMecanico(r.getMecanico());
        }
        reparacionDao.save(actual);
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, EstadoReparacion nuevo, String emailActual) {
        UserEntity actor = actor(emailActual);
        ReparacionEntity actual = reparacionDao.findById(id)
                .orElseThrow(() -> new ReglaNegocioException(null, "err.repair.notFound"));
        if (!puedeAcceder(actor, actual)) {
            throw new ReglaNegocioException(null, "err.repair.notOwnerState");
        }
        if (actual.getEstado() != EstadoReparacion.EN_PROCESO) {
            throw new ReglaNegocioException(null, "err.repair.finalState");
        }
        if (nuevo == EstadoReparacion.EN_PROCESO) {
            throw new ReglaNegocioException(null, "err.repair.noReopen");
        }
        actual.setEstado(nuevo);
        reparacionDao.save(actual);
    }

    private UserEntity actor(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no autenticado"));
    }

    private boolean esAdmin(UserEntity u) {
        return ROL_ADMIN.equals(u.getRol().getRol());
    }

    private boolean puedeAcceder(UserEntity actor, ReparacionEntity r) {
        return esAdmin(actor) || r.getMecanico().getIdUsuario().equals(actor.getIdUsuario());
    }

    // RN-10: el responsable debe ser un usuario activo con rol MECANICO
    private void validarMecanico(UserEntity m) {
        if (m == null || !ROL_MECANICO.equals(m.getRol().getRol()) || !m.isEstadoUsu()) {
            throw new ReglaNegocioException("mecanico", "err.repair.badMechanic");
        }
    }
}