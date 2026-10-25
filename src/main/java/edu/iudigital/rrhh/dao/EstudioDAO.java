package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.Estudio;

import java.util.List;
import java.util.Optional;

public interface EstudioDAO {
    List<Estudio> listar();

    Optional<Estudio> buscarPorId(long id);

    void insertar(Estudio estudio);

    void actualizar(Estudio estudio);

    void eliminar(long id);
}
