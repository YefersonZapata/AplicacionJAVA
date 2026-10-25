package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.Familiar;

import java.util.List;
import java.util.Optional;

public interface FamiliarDAO {
    List<Familiar> listar();

    Optional<Familiar> buscarPorId(long id);

    void insertar(Familiar familiar);

    void actualizar(Familiar familiar);

    void eliminar(long id);
}
