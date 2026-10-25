package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.GrupoFamiliar;

import java.util.List;
import java.util.Optional;

public interface GrupoFamiliarDAO {
    List<GrupoFamiliar> listar();

    Optional<GrupoFamiliar> buscarPorId(long id);

    void insertar(GrupoFamiliar familiar);

    void actualizar(GrupoFamiliar familiar);

    void eliminar(long id);
}
