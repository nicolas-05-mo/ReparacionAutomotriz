package com.usta.reparacionautomotriz.controllers;

import com.usta.reparacionautomotriz.entities.VehiculoEntity;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.services.ModeloService;
import com.usta.reparacionautomotriz.models.services.RepuestoService;
import com.usta.reparacionautomotriz.models.services.VehiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/busquedas")
public class BusquedaController {

    private final ModeloService modeloService;
    private final RepuestoService repuestoService;
    private final VehiculoService vehiculoService;

    public BusquedaController(ModeloService modeloService, RepuestoService repuestoService,
                              VehiculoService vehiculoService) {
        this.modeloService = modeloService;
        this.repuestoService = repuestoService;
        this.vehiculoService = vehiculoService;
    }

    // B1: repuestos compatibles con un modelo
    @GetMapping("/repuestos-por-modelo")
    public String repuestosPorModelo(@RequestParam(required = false) Long idModelo, Model model) {
        model.addAttribute("title", "search.parts.title");
        model.addAttribute("modelos", modeloService.findActivos());
        model.addAttribute("idModeloSel", idModelo);
        if (idModelo != null) {
            model.addAttribute("buscado", true);
            model.addAttribute("repuestos", repuestoService.buscarPorModelo(idModelo));
        }
        return "busqueda/repuestos-modelo";
    }

    // B2: modelo por placa
    @GetMapping("/modelo-por-placa")
    public String modeloPorPlaca(@RequestParam(required = false) String placa, Model model) {
        model.addAttribute("title", "search.plate.title");
        model.addAttribute("placaIngresada", placa);
        if (placa != null && !placa.isBlank()) {
            try {
                VehiculoEntity v = vehiculoService.buscarPorPlaca(placa);
                if (v == null) {
                    model.addAttribute("noRegistrada", true);
                } else {
                    model.addAttribute("vehiculo", v);
                }
            } catch (ReglaNegocioException e) {
                model.addAttribute("errorFormato", e.getCodigo());
            }
        }
        return "busqueda/modelo-placa";
    }
}