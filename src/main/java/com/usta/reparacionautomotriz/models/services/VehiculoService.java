package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.VehiculoEntity;

import java.util.List;

public interface VehiculoService {
    List<VehiculoEntity> findAll();
    List<VehiculoEntity> findActivos();
    VehiculoEntity findById(Long id);
    void save(VehiculoEntity vehiculo);
    void update(VehiculoEntity vehiculo);
    void changeState(Long id);

    /** Búsqueda B2. Devuelve null si la placa no está registrada; lanza ReglaNegocioException si el formato es inválido. */
    VehiculoEntity buscarPorPlaca(String placa);
}
