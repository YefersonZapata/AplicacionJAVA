package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.model.TipoDocumento;

import java.util.List;

public interface TipoDocumentoDAO {
    List<TipoDocumento> listar();

    void insertar(TipoDocumento tipoDocumento);

    void actualizar(TipoDocumento tipoDocumento);

    void eliminar(long id);
}
