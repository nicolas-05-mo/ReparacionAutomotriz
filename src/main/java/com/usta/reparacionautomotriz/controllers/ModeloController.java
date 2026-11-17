package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.entities.ModeloEntity;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.services.ModeloService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/modelos")
public class ModeloController {

    private final ModeloService modeloService;

    public ModeloController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "model.title");
        model.addAttribute("modelos", modeloService.findAll());
        return "modelo/list";
    }

    @GetMapping("/{id:\\d+}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
        ModeloEntity modelo = modeloService.findById(id);
        if (modelo == null) {
            flash.addFlashAttribute("errorMessage", "msg.model.notFound");
            return "redirect:/modelos";
        }
        model.addAttribute("title", "model.detail");
        model.addAttribute("modelo", modelo);
        return "modelo/detail";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        prepararForm(model, new ModeloEntity(), "model.new", "/modelos");
        return "modelo/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("modelo") ModeloEntity modelo,
                        BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            prepararForm(model, modelo, "model.new", "/modelos");
            return "modelo/form";
        }
        try {
            modeloService.save(modelo);
        } catch (ReglaNegocioException e) {
            result.rejectValue(e.getCampo(), e.getCodigo(), e.getArgs(), e.getCodigo());
            prepararForm(model, modelo, "model.new", "/modelos");
            return "modelo/form";
        }
        flash.addFlashAttribute("successMessage", "msg.model.created");
        return "redirect:/modelos";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        ModeloEntity modelo = modeloService.findById(id);
        if (modelo == null) {
            flash.addFlashAttribute("errorMessage", "msg.model.notFound");
            return "redirect:/modelos";
        }
        prepararForm(model, modelo, "model.edit", "/modelos/" + id);
        return "modelo/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("modelo") ModeloEntity modelo,
                             BindingResult result, Model model, RedirectAttributes flash) {
        modelo.setIdModelo(id);
        if (result.hasErrors()) {
            prepararForm(model, modelo, "model.edit", "/modelos/" + id);
            return "modelo/form";
        }
        try {
            modeloService.update(modelo);
        } catch (ReglaNegocioException e) {
            result.rejectValue(e.getCampo(), e.getCodigo(), e.getArgs(), e.getCodigo());
            prepararForm(model, modelo, "model.edit", "/modelos/" + id);
            return "modelo/form";
        }
        flash.addFlashAttribute("successMessage", "msg.model.updated");
        return "redirect:/modelos";
    }

    // Dar de baja / reactivar (RN-11: no se borra físicamente)
    @PostMapping("/{id}/state")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        ModeloEntity modelo = modeloService.findById(id);
        if (modelo == null) {
            flash.addFlashAttribute("errorMessage", "msg.model.notFound");
            return "redirect:/modelos";
        }
        boolean estabaActivo = modelo.isEstadoModelo();
        modeloService.changeState(id);
        flash.addFlashAttribute("successMessage",
                estabaActivo ? "msg.model.disabled" : "msg.model.enabled");
        return "redirect:/modelos";
    }

    private void prepararForm(Model model, ModeloEntity modelo, String title, String action) {
        model.addAttribute("title", title);
        model.addAttribute("modelo", modelo);
        model.addAttribute("formAction", action);
    }
}