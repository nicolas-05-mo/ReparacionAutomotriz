package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.VehiculoEntity;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.daos.VehiculoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class VehiculoServiceImp implements VehiculoService {

    private static final Pattern PLACA = Pattern.compile("^[A-Z]{3}-[0-9]{3}$");   // RN-01

    private final VehiculoDao vehiculoDao;

    public VehiculoServiceImp(VehiculoDao vehiculoDao) {
        this.vehiculoDao = vehiculoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoEntity> findAll() {
        return vehiculoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoEntity> findActivos() {
        return vehiculoDao.findByEstadoVehiculoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoEntity findById(Long id) {
        return vehiculoDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(VehiculoEntity vehiculo) {
        if (vehiculoDao.existsByPlaca(vehiculo.getPlaca())) {
            throw new ReglaNegocioException("placa", "err.vehicle.plateDuplicate", vehiculo.getPlaca());
        }
        if (!vehiculo.getModelo().isEstadoModelo()) {
            throw new ReglaNegocioException("modelo", "err.vehicle.modelDisabled");
        }
        vehiculo.setIdVehiculo(null);
        vehiculo.setEstadoVehiculo(true);
        vehiculoDao.save(vehiculo);
    }

    @Override
    @Transactional
    public void update(VehiculoEntity vehiculo) {
        VehiculoEntity actual = vehiculoDao.findById(vehiculo.getIdVehiculo())
                .orElseThrow(() -> new ReglaNegocioException(null, "err.vehicle.notFound"));
        if (vehiculoDao.existsByPlacaAndIdVehiculoNot(vehiculo.getPlaca(), vehiculo.getIdVehiculo())) {
            throw new ReglaNegocioException("placa", "err.vehicle.plateDuplicate", vehiculo.getPlaca());
        }
        boolean cambioModelo = !vehiculo.getModelo().getIdModelo().equals(actual.getModelo().getIdModelo());
        if (cambioModelo && !vehiculo.getModelo().isEstadoModelo()) {
            throw new ReglaNegocioException("modelo", "err.vehicle.modelDisabled");
        }
        actual.setPlaca(vehiculo.getPlaca());
        actual.setColorPlaca(vehiculo.getColorPlaca());
        actual.setNombreDueno(vehiculo.getNombreDueno());
        actual.setDocumentoDueno(vehiculo.getDocumentoDueno());
        actual.setTelefonoDueno(vehiculo.getTelefonoDueno());
        actual.setModelo(vehiculo.getModelo());
        vehiculoDao.save(actual);
    }

    @Override
    @Transactional
    public void changeState(Long id) {
        VehiculoEntity v = vehiculoDao.findById(id).orElse(null);
        if (v != null) {
            v.setEstadoVehiculo(!v.isEstadoVehiculo());
            vehiculoDao.save(v);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoEntity buscarPorPlaca(String placa) {
        String normalizada = (placa == null) ? "" : placa.trim().toUpperCase();
        if (!PLACA.matcher(normalizada).matches()) {
            // Formato inválido: no se consulta la base de datos
            throw new ReglaNegocioException("placa", "err.vehicle.plateFormat");
        }
        return vehiculoDao.findByPlaca(normalizada).orElse(null);
    }
}