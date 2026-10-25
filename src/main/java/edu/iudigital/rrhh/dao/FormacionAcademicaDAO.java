package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.FormacionAcademica;

import java.util.List;
import java.util.Optional;

public interface FormacionAcademicaDAO {
    List<FormacionAcademica> listar();

    Optional<FormacionAcademica> buscarPorId(long id);

    void insertar(FormacionAcademica formacion);

    void actualizar(FormacionAcademica formacion);

    void eliminar(long id);
}
