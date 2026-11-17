package com.usta.reparacionautomotriz.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "modelos")

public class ModeloEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo")
    private Long idModelo;

    @NotBlank(message = "{val.model.name.required}")
    @Size(max = 100, message = "{val.model.name.size}")
    @Column(name = "nombre_modelo", nullable = false, length = 100)
    private String nombreModelo;

    @NotBlank(message = "{val.model.brand.required}")
    @Size(max = 60, message = "{val.model.brand.size}")
    @Column(name = "marca", nullable = false, length = 60)
    private String marca;

    // true = activo, false = dado de baja (RN-11)
    @Column(name = "estado_modelo", nullable = false)
    private boolean estadoModelo = true;
}