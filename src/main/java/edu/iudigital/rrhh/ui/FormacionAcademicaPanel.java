package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.dao.FormacionAcademicaDAO;
import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.dao.impl.FormacionAcademicaDAOImpl;
import edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.FormacionAcademica;
import edu.iudigital.rrhh.model.Funcionario;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FormacionAcademicaPanel extends JPanel {
    private final FormacionAcademicaDAO dao = new FormacionAcademicaDAOImpl();
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private final JTextField id = new JTextField(),
            universidad = new JTextField(),
            titulo = new JTextField();
    private final JComboBox<String> nivel =
            new JComboBox<>(
                    new String[] {
                        "Técnico",
                        "Tecnólogo",
                        "Profesional",
                        "Especialización",
                        "Maestría",
                        "Doctorado",
                        "Curso o certificación",
                        "Otro"
                    });
    private final JComboBox<FuncionarioOpcion> funcionario = new JComboBox<>();
    private final DefaultTableModel modelo =
            UIUtil.modeloTabla(
                    "ID formación",
                    "Funcionario",
                    "Universidad",
                    "Nivel de estudio",
                    "Título obtenido",
                    "idFuncionario");
    private final JTable tabla = new JTable(modelo);

    public FormacionAcademicaPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        id.setEditable(false);
        JPanel form = UIUtil.formulario();
        UIUtil.campo(form, "ID formación (automático)", id);
        UIUtil.campo(form, "Funcionario *", funcionario);
        UIUtil.campo(form, "Universidad *", universidad);
        UIUtil.campo(form, "Nivel de estudio *", nivel);
        UIUtil.campo(form, "Título obtenido *", titulo);
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
        UIUtil.ocultarColumna(tabla, 5);
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

    private FormacionAcademica leerFormulario() throws ValidacionException {
        FuncionarioOpcion f = UIUtil.seleccionado(funcionario, "funcionario");
        FormacionAcademica a = new FormacionAcademica();
        a.setIdFuncionario(f.id());
        a.setUniversidad(UIUtil.obligatorio(universidad, "universidad"));
        a.setNivelEstudio(UIUtil.seleccionado(nivel, "nivel de estudio"));
        a.setTituloEstudio(UIUtil.obligatorio(titulo, "título obtenido"));
        return a;
    }

    private void guardar() {
        try {
            FormacionAcademica f = leerFormulario();
            dao.insertar(f);
            cargarTabla();
            limpiar();
            UIUtil.info(
                    this,
                    "Formación académica guardada correctamente (ID " + f.getIdFormacion() + ").");
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void actualizar() {
        try {
            long registro = UIUtil.id(id, "formación académica");
            FormacionAcademica f = leerFormulario();
            f.setIdFormacion(registro);
            dao.actualizar(f);
            cargarTabla();
            limpiar();
            UIUtil.info(this, "Formación académica actualizada correctamente.");
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void eliminar() {
        try {
            long registro = UIUtil.id(id, "formación académica");
            if (UIUtil.confirmar(this, "¿Eliminar el registro académico seleccionado?")) {
                dao.eliminar(registro);
                cargarTabla();
                limpiar();
                UIUtil.info(this, "Registro académico eliminado.");
            }
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (FormacionAcademica a : dao.listar())
                modelo.addRow(
                        new Object[] {
                            a.getIdFormacion(),
                            a.getFuncionarioNombre() + " — " + a.getNumeroDocumentoFuncionario(),
                            a.getUniversidad(),
                            a.getNivelEstudio(),
                            a.getTituloEstudio(),
                            a.getIdFuncionario()
                        });
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void seleccionarFila() {
        int r = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        id.setText(txt(r, 0));
        seleccionarFuncionario(Long.parseLong(txt(r, 5)));
        universidad.setText(txt(r, 2));
        nivel.setSelectedItem(txt(r, 3));
        titulo.setText(txt(r, 4));
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
        universidad.setText("");
        titulo.setText("");
        if (nivel.getItemCount() > 0) nivel.setSelectedIndex(0);
    }

    private record FuncionarioOpcion(long id, String descripcion) {
        @Override
        public String toString() {
            return id + " — " + descripcion;
        }
    }
}
