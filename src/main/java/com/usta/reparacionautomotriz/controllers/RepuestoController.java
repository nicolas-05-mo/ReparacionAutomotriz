package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.entities.ModeloEntity;
import com.usta.reparacionautomotriz.entities.RepuestoEntity;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.services.ModeloService;
import com.usta.reparacionautomotriz.models.services.RepuestoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/repuestos")
public class RepuestoController {

    private final RepuestoService repuestoService;
    private final ModeloService modeloService;

    public RepuestoController(RepuestoService repuestoService, ModeloService modeloService) {
        this.repuestoService = repuestoService;
        this.modeloService = modeloService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "part.title");
        model.addAttribute("repuestos", repuestoService.findAll());
        return "repuesto/list";
    }

    @GetMapping("/{id:\\d+}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
        RepuestoEntity r = repuestoService.findById(id);
        if (r == null) {
            flash.addFlashAttribute("errorMessage", "msg.part.notFound");
            return "redirect:/repuestos";
        }
        model.addAttribute("title", "part.detail");
        model.addAttribute("repuesto", r);
        return "repuesto/detail";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        prepararForm(model, new RepuestoEntity(), "part.new", "/repuestos");
        return "repuesto/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("repuesto") RepuestoEntity repuesto,
                        BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            prepararForm(model, repuesto, "part.new", "/repuestos");
            return "repuesto/form";
        }
        try {
            repuestoService.save(repuesto);
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, repuesto, "part.new", "/repuestos");
            return "repuesto/form";
        }
        flash.addFlashAttribute("successMessage", "msg.part.created");
        return "redirect:/repuestos";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        RepuestoEntity r = repuestoService.findById(id);
        if (r == null) {
            flash.addFlashAttribute("errorMessage", "msg.part.notFound");
            return "redirect:/repuestos";
        }
        prepararForm(model, r, "part.edit", "/repuestos/" + id);
        return "repuesto/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("repuesto") RepuestoEntity repuesto,
                             BindingResult result, Model model, RedirectAttributes flash) {
        repuesto.setIdRepuesto(id);
        if (result.hasErrors()) {
            prepararForm(model, repuesto, "part.edit", "/repuestos/" + id);
            return "repuesto/form";
        }
        try {
            repuestoService.update(repuesto);
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, repuesto, "part.edit", "/repuestos/" + id);
            return "repuesto/form";
        }
        flash.addFlashAttribute("successMessage", "msg.part.updated");
        return "redirect:/repuestos";
    }

    @PostMapping("/{id}/state")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        RepuestoEntity r = repuestoService.findById(id);
        if (r == null) {
            flash.addFlashAttribute("errorMessage", "msg.part.notFound");
            return "redirect:/repuestos";
        }
        boolean estabaActivo = r.isEstadoRepuesto();
        repuestoService.changeState(id);
        flash.addFlashAttribute("successMessage", estabaActivo ? "msg.part.disabled" : "msg.part.enabled");
        return "redirect:/repuestos";
    }

    private void prepararForm(Model model, RepuestoEntity r, String title, String action) {
        List<ModeloEntity> opciones = new ArrayList<>(modeloService.findActivos());
        for (ModeloEntity m : r.getModelos()) {
            if (!opciones.contains(m)) {
                opciones.add(m);
            }
        }
        model.addAttribute("title", title);
        model.addAttribute("repuesto", r);
        model.addAttribute("formAction", action);
        model.addAttribute("modelosOpciones", opciones);
    }

    private void registrarError(BindingResult result, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            result.reject(e.getCodigo(), e.getArgs(), e.getCodigo());
        } else {
            result.rejectValue(e.getCampo(), e.getCodigo(), e.getArgs(), e.getCodigo());
        }
    }
}