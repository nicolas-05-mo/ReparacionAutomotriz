package com.usta.reparacionautomotriz.models.services;

import com.usta.reparacionautomotriz.entities.RepuestoEntity;
import com.usta.reparacionautomotriz.exceptions.ReglaNegocioException;
import com.usta.reparacionautomotriz.models.daos.RepuestoDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class RepuestoServiceImp implements RepuestoService {

    private final RepuestoDao repuestoDao;

    public RepuestoServiceImp(RepuestoDao repuestoDao) {
        this.repuestoDao = repuestoDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> findAll() {
        return repuestoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> findActivos() {
        return repuestoDao.findByEstadoRepuestoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public RepuestoEntity findById(Long id) {
        return repuestoDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(RepuestoEntity repuesto) {
        if (repuestoDao.existsByCodigoEan(repuesto.getCodigoEan())) {          // RN-05
            throw new ReglaNegocioException("codigoEan", "err.part.eanDuplicate");
        }
        repuesto.setIdRepuesto(null);
        repuesto.setEstadoRepuesto(true);
        repuestoDao.save(repuesto);
    }

    @Override
    @Transactional
    public void update(RepuestoEntity repuesto) {
        RepuestoEntity actual = repuestoDao.findById(repuesto.getIdRepuesto())
                .orElseThrow(() -> new ReglaNegocioException(null, "err.part.notFound"));
        if (repuestoDao.existsByCodigoEanAndIdRepuestoNot(repuesto.getCodigoEan(), repuesto.getIdRepuesto())) {
            throw new ReglaNegocioException("codigoEan", "err.part.eanDuplicate");
        }
        actual.setNombre(repuesto.getNombre());
        actual.setDescripcion(repuesto.getDescripcion());
        actual.setPrecio(repuesto.getPrecio());
        actual.setCodigoEan(repuesto.getCodigoEan());
        actual.setStock(repuesto.getStock());
        actual.setImagen(repuesto.getImagen());
        actual.setModelos(new HashSet<>(repuesto.getModelos()));
        repuestoDao.save(actual);
    }

    @Override
    @Transactional
    public void changeState(Long id) {
        RepuestoEntity r = repuestoDao.findById(id).orElse(null);
        if (r != null) {
            r.setEstadoRepuesto(!r.isEstadoRepuesto());
            repuestoDao.save(r);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepuestoEntity> buscarPorModelo(Long idModelo) {
        return repuestoDao.findByModelos_IdModeloAndEstadoRepuestoTrue(idModelo);
    }
}