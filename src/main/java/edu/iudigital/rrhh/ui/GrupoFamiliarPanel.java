package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.dao.GrupoFamiliarDAO;
import edu.iudigital.rrhh.dao.TipoDocumentoDAO;
import edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import edu.iudigital.rrhh.dao.impl.GrupoFamiliarDAOImpl;
import edu.iudigital.rrhh.dao.impl.TipoDocumentoDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.Funcionario;
import edu.iudigital.rrhh.model.GrupoFamiliar;
import edu.iudigital.rrhh.model.TipoDocumento;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GrupoFamiliarPanel extends JPanel {
    private final GrupoFamiliarDAO dao = new GrupoFamiliarDAOImpl();
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private final TipoDocumentoDAO tipoDAO = new TipoDocumentoDAOImpl();
    private final JTextField id = new JTextField(),
            numero = new JTextField(),
            nombres = new JTextField(),
            apellidos = new JTextField(),
            rol = new JTextField(),
            fecha = new JTextField(),
            telefono = new JTextField();
    private final JComboBox<Object> tipo = new JComboBox<>();
    private final JComboBox<FuncionarioOpcion> funcionario = new JComboBox<>();
    private final DefaultTableModel modelo =
            UIUtil.modeloTabla(
                    "ID familiar",
                    "Funcionario responsable",
                    "Tipo documento",
                    "Número",
                    "Nombres",
                    "Apellidos",
                    "Rol familiar",
                    "Nacimiento",
                    "Teléfono",
                    "idFuncionario",
                    "idTipoDoc");
    private final JTable tabla = new JTable(modelo);

    public GrupoFamiliarPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        id.setEditable(false);
        JPanel form = UIUtil.formulario();
        UIUtil.campo(form, "ID familiar (automático)", id);
        UIUtil.campo(form, "Funcionario responsable *", funcionario);
        UIUtil.campo(form, "Tipo de documento (opcional)", tipo);
        UIUtil.campo(form, "Número de identificación", numero);
        UIUtil.campo(form, "Nombres *", nombres);
        UIUtil.campo(form, "Apellidos *", apellidos);
        UIUtil.campo(form, "Rol familiar *", rol);
        UIUtil.campo(form, "Fecha nacimiento (AAAA-MM-DD)", fecha);
        UIUtil.campo(form, "Teléfono", telefono);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton guardar = new JButton("Guardar nuevo"),
                actualizar = new JButton("Actualizar seleccionado"),
                eliminar = new JButton("Eliminar seleccionado"),
                limpiar = new JButton("Limpiar formulario"),
                recargar = new JButton("Recargar");
        acciones.add(guardar);
        acciones.add(actualizar);
        acciones.add(eliminar);
        acciones.add(limpiar);
        acciones.add(recargar);
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(acciones, BorderLayout.SOUTH);
        UIUtil.configurarTabla(tabla);
        UIUtil.ocultarColumna(tabla, 9);
        UIUtil.ocultarColumna(tabla, 10);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        guardar.addActionListener(e -> guardar());
        actualizar.addActionListener(e -> actualizar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiar());
        recargar.addActionListener(e -> recargar());
        tabla.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0)
                                seleccionarFila();
                        });
        recargar();
    }

    public void recargar() {
        cargarFuncionarios();
        cargarTipos();
        cargarTabla();
    }

    private void cargarFuncionarios() {
        Long anterior =
                funcionario.getSelectedItem() == null
                        ? null
                        : ((FuncionarioOpcion) funcionario.getSelectedItem()).id();
        try {
            funcionario.removeAllItems();
            for (Funcionario f : funcionarioDAO.listar())
                funcionario.addItem(
                        new FuncionarioOpcion(
                                f.getIdFuncionario(),
                                f.getNombres()
                                        + " "
                                        + f.getApellidos()
                                        + " — "
                                        + f.getNumeroIdentificacion()));
            if (anterior != null) seleccionarFuncionario(anterior);
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void cargarTipos() {
        Long anterior =
                tipo.getSelectedItem() instanceof TipoDocumento t ? t.getIdTipoDocumento() : null;
        try {
            tipo.removeAllItems();
            tipo.addItem("Sin documento");
            for (TipoDocumento t : tipoDAO.listar()) tipo.addItem(t);
            if (anterior != null) tipo.setSelectedItem(buscarTipo(anterior));
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private GrupoFamiliar leerFormulario() throws ValidacionException {
        FuncionarioOpcion f = UIUtil.seleccionado(funcionario, "funcionario responsable");
        GrupoFamiliar x = new GrupoFamiliar();
        x.setIdFuncionario(f.id());
        Object t = tipo.getSelectedItem();
        if (t instanceof TipoDocumento td) {
            x.setIdTipoDocumento(td.getIdTipoDocumento());
            x.setNumeroIdentificacion(UIUtil.texto(numero));
            if (x.getNumeroIdentificacion() == null)
                throw new ValidacionException(
                        "Escribe el número de identificación o selecciona «Sin documento».");
        } else {
            x.setIdTipoDocumento(null);
            if (UIUtil.texto(numero) != null)
                throw new ValidacionException(
                        "Selecciona un tipo de documento para el número que escribiste.");
            x.setNumeroIdentificacion(null);
        }
        x.setNombres(UIUtil.obligatorio(nombres, "nombres del familiar"));
        x.setApellidos(UIUtil.obligatorio(apellidos, "apellidos del familiar"));
        x.setRol(UIUtil.obligatorio(rol, "rol familiar"));
        x.setFechaNacimiento(UIUtil.fecha(fecha, "fecha de nacimiento del familiar", false));
        x.setTelefono(UIUtil.texto(telefono));
        return x;
    }

    private void guardar() {
        try {
            GrupoFamiliar f = leerFormulario();
            dao.insertar(f);
            cargarTabla();
            limpiar();
            UIUtil.info(this, "Familiar guardado correctamente (ID " + f.getIdFamiliar() + ").");
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void actualizar() {
        try {
            long registro = UIUtil.id(id, "familiares");
            GrupoFamiliar f = leerFormulario();
            f.setIdFamiliar(registro);
            dao.actualizar(f);
            cargarTabla();
            limpiar();
            UIUtil.info(this, "Familiar actualizado correctamente.");
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void eliminar() {
        try {
            long registro = UIUtil.id(id, "familiares");
            if (UIUtil.confirmar(this, "¿Eliminar el familiar seleccionado?")) {
                dao.eliminar(registro);
                cargarTabla();
                limpiar();
                UIUtil.info(this, "Familiar eliminado.");
            }
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (GrupoFamiliar f : dao.listar()) {
                String td =
                        f.getTipoDocumentoNombre() == null
                                ? "Sin documento"
                                : f.getTipoDocumentoSigla() + " — " + f.getTipoDocumentoNombre();
                modelo.addRow(
                        new Object[] {
                            f.getIdFamiliar(),
                            f.getFuncionarioNombre() + " — " + f.getNumeroDocumentoFuncionario(),
                            td,
                            f.getNumeroIdentificacion(),
                            f.getNombres(),
                            f.getApellidos(),
                            f.getRol(),
                            f.getFechaNacimiento(),
                            f.getTelefono(),
                            f.getIdFuncionario(),
                            f.getIdTipoDocumento()
                        });
            }
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void seleccionarFila() {
        int r = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        id.setText(txt(r, 0));
        seleccionarFuncionario(Long.parseLong(txt(r, 9)));
        Long tipoId = txt(r, 10).isBlank() ? null : Long.parseLong(txt(r, 10));
        if (tipoId == null) tipo.setSelectedIndex(0);
        else tipo.setSelectedItem(buscarTipo(tipoId));
        numero.setText(txt(r, 3));
        nombres.setText(txt(r, 4));
        apellidos.setText(txt(r, 5));
        rol.setText(txt(r, 6));
        fecha.setText(txt(r, 7));
        telefono.setText(txt(r, 8));
    }

    private TipoDocumento buscarTipo(long idTipo) {
        for (int i = 1; i < tipo.getItemCount(); i++)
            if (tipo.getItemAt(i) instanceof TipoDocumento t && t.getIdTipoDocumento() == idTipo)
                return t;
        return null;
    }

    private void seleccionarFuncionario(long value) {
        for (int i = 0; i < funcionario.getItemCount(); i++)
            if (funcionario.getItemAt(i).id() == value) {
                funcionario.setSelectedIndex(i);
                return;
            }
    }

    private String txt(int r, int c) {
        Object v = modelo.getValueAt(r, c);
        return v == null ? "" : v.toString();
    }

    private void limpiar() {
        tabla.clearSelection();
        id.setText("");
        numero.setText("");
        nombres.setText("");
        apellidos.setText("");
        rol.setText("");
        fecha.setText("");
        telefono.setText("");
        if (tipo.getItemCount() > 0) tipo.setSelectedIndex(0);
    }

    private record FuncionarioOpcion(long id, String descripcion) {
        @Override
        public String toString() {
            return id + " — " + descripcion;
        }
    }
}
