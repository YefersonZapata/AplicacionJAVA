package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.EstadoCivil;

import java.util.List;

public interface EstadoCivilDAO {
    List<EstadoCivil> listar();

    void insertar(EstadoCivil estadoCivil);

    void actualizar(EstadoCivil estadoCivil);

    void eliminar(long id);
}
