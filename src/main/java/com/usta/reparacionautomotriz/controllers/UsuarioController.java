package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.dto.UsuarioForm;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "user.title");
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/list";
    }

    @GetMapping("/new")
    public String nuevo(Model model) {
        prepararForm(model, new UsuarioForm(), "user.new", "/usuarios", true);
        return "usuario/form";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("usuario") UsuarioForm form,
                        BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            prepararForm(model, form, "user.new", "/usuarios", true);
            return "usuario/form";
        }
        try {
            usuarioService.crear(form);
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, form, "user.new", "/usuarios", true);
            return "usuario/form";
        }
        flash.addFlashAttribute("successMessage", "msg.user.created");
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/edit")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        UsuarioForm form = usuarioService.obtenerForm(id);
        if (form == null) {
            flash.addFlashAttribute("errorMessage", "msg.user.notFound");
            return "redirect:/usuarios";
        }
        prepararForm(model, form, "user.edit", "/usuarios/" + id, false);
        return "usuario/form";
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("usuario") UsuarioForm form,
                             BindingResult result, Model model, RedirectAttributes flash) {
        form.setIdUsuario(id);
        if (result.hasErrors()) {
            prepararForm(model, form, "user.edit", "/usuarios/" + id, false);
            return "usuario/form";
        }
        try {
            usuarioService.actualizar(form);
        } catch (ReglaNegocioException e) {
            registrarError(result, e);
            prepararForm(model, form, "user.edit", "/usuarios/" + id, false);
            return "usuario/form";
        }
        flash.addFlashAttribute("successMessage", "msg.user.updated");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/state")
    public String cambiarEstado(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        try {
            usuarioService.changeState(id, principal.getName());
            flash.addFlashAttribute("successMessage", "msg.user.stateChanged");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("errorMessage", e.getCodigo());
        }
        return "redirect:/usuarios";
    }

    private void prepararForm(Model model, UsuarioForm form, String title, String action, boolean esNuevo) {
        model.addAttribute("title", title);
        model.addAttribute("usuario", form);
        model.addAttribute("formAction", action);
        model.addAttribute("roles", usuarioService.listarRoles());
        model.addAttribute("esNuevo", esNuevo);
    }

    private void registrarError(BindingResult result, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            result.reject(e.getCodigo(), e.getArgs(), e.getCodigo());
        } else {
            result.rejectValue(e.getCampo(), e.getCodigo(), e.getArgs(), e.getCodigo());
        }
    }
}