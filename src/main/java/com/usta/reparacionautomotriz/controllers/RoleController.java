package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.models.services.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("title", "role.title");
        model.addAttribute("roles", roleService.findAll());
        return "rol/list";
    }
}