package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.RepuestoEntity;

import java.util.List;

public interface RepuestoService {
    List<RepuestoEntity> findAll();
    List<RepuestoEntity> findActivos();
    RepuestoEntity findById(Long id);
    void save(RepuestoEntity repuesto);
    void update(RepuestoEntity repuesto);
    void changeState(Long id);

    /** Búsqueda B1: repuestos activos compatibles con un modelo. */
    List<RepuestoEntity> buscarPorModelo(Long idModelo);
}
