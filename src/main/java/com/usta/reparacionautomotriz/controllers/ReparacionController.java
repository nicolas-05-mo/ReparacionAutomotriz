package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.entities.ReparacionEntity;
import com.usta.reparacionautomotriz.entities.UserEntity;
import com.usta.reparacionautomotriz.entities.VehiculoEntity;
import com.usta.reparacionautomotriz.enums.EstadoReparacion;
import com.usta.reparacionautomotriz.enums.TipoReparacion;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.services.ReparacionService;
import com.usta.reparacionautomotriz.models.services.UsuarioService;
import com.usta.reparacionautomotriz.models.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reparaciones")
public class ReparacionController {

    private final ReparacionService reparacionService;
    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;

    public ReparacionController(ReparacionService reparacionService,
                                VehiculoService vehiculoService,
                                UsuarioService usuarioService) {
        this.reparacionService = reparacionService;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Principal principal, Model model) {
        model.addAttribute("title", "Reparaciones");
        model.addAttribute("reparaciones", reparacionService.listarPara(principal.getName()));
        return "reparacion/list";
    }

    @GetMapping("/{id:\\d+}")
    public String detalle(@PathVariable Long id, Principal principal, Model model, RedirectAttributes flash) {
        ReparacionEntity r = reparacionService.buscarPara(id, principal.getName());
        if (r == null) {
            flash.addFlashAttribute("errorMessage", "Reparación no encontrada");
            return "redirect:/reparaciones";
        }
        model.addAttribute("title", "Detalle de la reparación");
        model.addAttribute("reparacion", r);
        return "reparacion/detail";
    }

    @GetMapping("/new")
    public String nueva(Principal principal, Model model) {
        ReparacionEntity r = new ReparacionEntity();
        r.setFecha(LocalDate.now());
        prepararForm(model, r, "Nueva reparación", "/reparaciones", principal.getName());
        return "reparacion/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("reparacion") ReparacionEntity reparacion,
                        BindingResult result, Principal principal, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            prepararForm(model, reparacion, "Nueva reparación", "/reparaciones", principal.getName());
            return "reparacion/form";
        }
        try {
            reparacionService.crear(reparacion, principal.getName());
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, reparacion, "Nueva reparación", "/reparaciones", principal.getName());
            return "reparacion/form";
        }
        flash.addFlashAttribute("successMessage", "Reparación registrada en estado EN_PROCESO");
        return "redirect:/reparaciones";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Principal principal, Model model, RedirectAttributes flash) {
        ReparacionEntity r = reparacionService.buscarPara(id, principal.getName());
        if (r == null) {
            flash.addFlashAttribute("errorMessage", "Reparación no encontrada");
            return "redirect:/reparaciones";
        }
        if (r.getEstado() != EstadoReparacion.EN_PROCESO) {
            flash.addFlashAttribute("errorMessage", "Una reparación terminada o cancelada no se puede editar");
            return "redirect:/reparaciones";
        }
        prepararForm(model, r, "Editar reparación", "/reparaciones/" + id, principal.getName());
        return "reparacion/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("reparacion") ReparacionEntity reparacion,
                             BindingResult result, Principal principal, Model model, RedirectAttributes flash) {
        reparacion.setIdReparacion(id);
        if (result.hasErrors()) {
            prepararForm(model, reparacion, "Editar reparación", "/reparaciones/" + id, principal.getName());
            return "reparacion/form";
        }
        try {
            reparacionService.actualizar(reparacion, principal.getName());
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, reparacion, "Editar reparación", "/reparaciones/" + id, principal.getName());
            return "reparacion/form";
        }
        flash.addFlashAttribute("successMessage", "Reparación actualizada correctamente");
        return "redirect:/reparaciones";
    }

    // Terminar o cancelar (las reparaciones no se borran: RN-11)
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, @RequestParam EstadoReparacion estado,
                                Principal principal, RedirectAttributes flash) {
        try {
            reparacionService.cambiarEstado(id, estado, principal.getName());
            flash.addFlashAttribute("successMessage", "Reparación marcada como " + estado);
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/reparaciones";
    }

    private void prepararForm(Model model, ReparacionEntity r, String title, String action, String email) {
        List<VehiculoEntity> vehiculos = new ArrayList<>(vehiculoService.findActivos());
        if (r.getVehiculo() != null && !vehiculos.contains(r.getVehiculo())) {
            vehiculos.add(r.getVehiculo());
        }
        List<UserEntity> mecanicos = new ArrayList<>(usuarioService.listarMecanicos());
        if (r.getMecanico() != null && !mecanicos.contains(r.getMecanico())) {
            mecanicos.add(r.getMecanico());
        }
        model.addAttribute("title", title);
        model.addAttribute("reparacion", r);
        model.addAttribute("formAction", action);
        model.addAttribute("vehiculosOpciones", vehiculos);
        model.addAttribute("mecanicosOpciones", mecanicos);
        model.addAttribute("mecanicoActual", usuarioService.findByEmail(email));
        model.addAttribute("tipos", TipoReparacion.values());
    }

    private void registrarError(BindingResult result, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            result.reject("regla", e.getMessage());
        } else {
            result.rejectValue(e.getCampo(), "regla", e.getMessage());
        }
    }
}
