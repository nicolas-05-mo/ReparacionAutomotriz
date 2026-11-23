package com.usta.reparacionautomotriz.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioForm {
    private Long idUsuario;

    @NotBlank(message = "{val.user.firstName.required}")
    @Size(max = 60)
    private String nombre;

    @NotBlank(message = "{val.user.lastName.required}")
    @Size(max = 60)
    private String apellido;

    @NotBlank(message = "{val.user.phone.required}")
    @Size(max = 20)
    private String telefono;

    @NotBlank(message = "{val.user.email.required}")
    @Email(message = "{val.user.email.format}")
    @Size(max = 100)
    private String email;

    // Vacía al editar = no cambiar la contraseña. Al crear es obligatoria (se valida en el servicio).
    @Pattern(regexp = "^$|^.{6,72}$", message = "{val.user.password.size}")
    private String clave;

    @NotNull(message = "{val.user.role.required}")
    private Long idRol;
}