package com.usta.reparacionautomotriz.models.daos;

import com.usta.reparacionautomotriz.entities.ModeloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloDao extends JpaRepository<ModeloEntity, Long> {
    List<ModeloEntity> findByEstadoModeloTrue();

    // NUEVO: Agregado para que coincida con las llamadas a findActivos() en los servicios/controladores
    default List<ModeloEntity> findActivos() {
        return findByEstadoModeloTrue();
    }

    // RN-04: marca + nombre únicos
    boolean existsByMarcaIgnoreCaseAndNombreModeloIgnoreCase(String marca, String nombreModelo);

    boolean existsByMarcaIgnoreCaseAndNombreModeloIgnoreCaseAndIdModeloNot(
            String marca, String nombreModelo, Long idModelo);
}