package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.models.services.ModeloService;
import com.usta.reparacionautomotriz.models.services.ReparacionService;
import com.usta.reparacionautomotriz.models.services.RepuestoService;
import com.usta.reparacionautomotriz.models.services.VehiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class InicioController {

    private final ModeloService modeloService;
    private final VehiculoService vehiculoService;
    private final RepuestoService repuestoService;
    private final ReparacionService reparacionService;

    public InicioController(ModeloService modeloService, VehiculoService vehiculoService,
                            RepuestoService repuestoService, ReparacionService reparacionService) {
        this.modeloService = modeloService;
        this.vehiculoService = vehiculoService;
        this.repuestoService = repuestoService;
        this.reparacionService = reparacionService;
    }

    @GetMapping("/")
    public String inicio(Principal principal, Model model) {
        model.addAttribute("totalModelos", modeloService.findActivos().size());
        model.addAttribute("totalVehiculos", vehiculoService.findActivos().size());
        model.addAttribute("totalRepuestos", repuestoService.findActivos().size());
        model.addAttribute("reparacionesEnProceso", reparacionService.contarEnProceso(principal.getName()));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
