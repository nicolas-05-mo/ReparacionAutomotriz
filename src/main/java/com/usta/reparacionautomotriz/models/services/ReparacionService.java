package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.ReparacionEntity;
import com.usta.reparacionautomotriz.enums.EstadoReparacion;

import java.util.List;

public interface ReparacionService {
    /** Administrador: todas. Mecánico: solo las suyas. */
    List<ReparacionEntity> listarPara(String emailActual);
    ReparacionEntity buscarPara(Long id, String emailActual);
    long contarEnProceso(String emailActual);
    void crear(ReparacionEntity reparacion, String emailActual);
    void actualizar(ReparacionEntity reparacion, String emailActual);
    void cambiarEstado(Long id, EstadoReparacion nuevoEstado, String emailActual);
}
