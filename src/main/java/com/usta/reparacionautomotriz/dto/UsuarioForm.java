package com.usta.reparacionautomotriz.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioForm {
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60)
    private String apellido;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    @Size(max = 100)
    private String email;

    // Vacía al editar = no cambiar la contraseña. Al crear es obligatoria (se valida en el servicio).
    @Pattern(regexp = "^$|^.{6,72}$", message = "La contraseña debe tener entre 6 y 72 caracteres")
    private String clave;

    @NotNull(message = "Seleccione un rol")
    private Long idRol;
}
