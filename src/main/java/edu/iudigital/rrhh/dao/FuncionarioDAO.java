package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioDAO {
    List<Funcionario> listar();

    Optional<Funcionario> buscarPorId(long id);

    void insertar(Funcionario funcionario);

    void actualizar(Funcionario funcionario);

    void eliminar(long id);
}
