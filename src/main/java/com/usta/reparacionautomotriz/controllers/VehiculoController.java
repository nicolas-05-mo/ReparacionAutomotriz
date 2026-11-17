package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.entities.ModeloEntity;
import com.usta.reparacionautomotriz.entities.VehiculoEntity;
import com.usta.reparacionautomotriz.enums.ColorPlaca;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.services.ModeloService;
import com.usta.reparacionautomotriz.models.services.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final ModeloService modeloService;

    public VehiculoController(VehiculoService vehiculoService, ModeloService modeloService) {
        this.vehiculoService = vehiculoService;
        this.modeloService = modeloService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "vehicle.title");
        model.addAttribute("vehiculos", vehiculoService.findAll());
        return "vehiculo/list";
    }

    @GetMapping("/{id:\\d+}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
        VehiculoEntity v = vehiculoService.findById(id);
        if (v == null) {
            flash.addFlashAttribute("errorMessage", "msg.vehicle.notFound");
            return "redirect:/vehiculos";
        }
        model.addAttribute("title", "vehicle.detail");
        model.addAttribute("vehiculo", v);
        return "vehiculo/detail";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        prepararForm(model, new VehiculoEntity(), "vehicle.new", "/vehiculos");
        return "vehiculo/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("vehiculo") VehiculoEntity vehiculo,
                        BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            prepararForm(model, vehiculo, "vehicle.new", "/vehiculos");
            return "vehiculo/form";
        }
        try {
            vehiculoService.save(vehiculo);
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, vehiculo, "vehicle.new", "/vehiculos");
            return "vehiculo/form";
        }
        flash.addFlashAttribute("successMessage", "msg.vehicle.created");
        return "redirect:/vehiculos";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        VehiculoEntity v = vehiculoService.findById(id);
        if (v == null) {
            flash.addFlashAttribute("errorMessage", "msg.vehicle.notFound");
            return "redirect:/vehiculos";
        }
        prepararForm(model, v, "vehicle.edit", "/vehiculos/" + id);
        return "vehiculo/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("vehiculo") VehiculoEntity vehiculo,
                             BindingResult result, Model model, RedirectAttributes flash) {
        vehiculo.setIdVehiculo(id);
        if (result.hasErrors()) {
            prepararForm(model, vehiculo, "vehicle.edit", "/vehiculos/" + id);
            return "vehiculo/form";
        }
        try {
            vehiculoService.update(vehiculo);
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, vehiculo, "vehicle.edit", "/vehiculos/" + id);
            return "vehiculo/form";
        }
        flash.addFlashAttribute("successMessage", "msg.vehicle.updated");
        return "redirect:/vehiculos";
    }

    @PostMapping("/{id}/state")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        VehiculoEntity v = vehiculoService.findById(id);
        if (v == null) {
            flash.addFlashAttribute("errorMessage", "msg.vehicle.notFound");
            return "redirect:/vehiculos";
        }
        boolean estabaActivo = v.isEstadoVehiculo();
        vehiculoService.changeState(id);
        flash.addFlashAttribute("successMessage", estabaActivo ? "msg.vehicle.disabled" : "msg.vehicle.enabled");
        return "redirect:/vehiculos";
    }

    private void prepararForm(Model model, VehiculoEntity v, String title, String action) {
        List<ModeloEntity> opciones = new ArrayList<>(modeloService.findActivos());
        if (v.getModelo() != null && !opciones.contains(v.getModelo())) {
            opciones.add(v.getModelo());      // conserva el modelo actual aunque esté dado de baja
        }
        model.addAttribute("title", title);
        model.addAttribute("vehiculo", v);
        model.addAttribute("formAction", action);
        model.addAttribute("modelosOpciones", opciones);
        model.addAttribute("colores", ColorPlaca.values());
    }

    private void registrarError(BindingResult result, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            result.reject(e.getCodigo(), e.getArgs(), e.getCodigo());
        } else {
            result.rejectValue(e.getCampo(), e.getCodigo(), e.getArgs(), e.getCodigo());
        }
    }
}