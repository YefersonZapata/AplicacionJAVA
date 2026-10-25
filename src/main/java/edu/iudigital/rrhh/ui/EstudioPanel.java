package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.dao.EstudioDAO;
import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.dao.impl.EstudioDAOImpl;
import edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.Estudio;
import edu.iudigital.rrhh.model.Funcionario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class EstudioPanel extends JPanel {
    private final EstudioDAO dao = new EstudioDAOImpl();
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private final JTextField id = new JTextField();
    private final JTextField universidad = new JTextField();
    private final JTextField titulo = new JTextField();
    private final JComboBox<String> nivel =
            new JComboBox<>(
                    new String[] {
                        "Técnico", "Tecnólogo", "Profesional", "Especialización",
                        "Maestría", "Doctorado", "Curso o certificación", "Otro"
                    });
    private final JComboBox<FuncionarioOpcion> funcionario = new JComboBox<>();
    private final DefaultTableModel modelo =
            UIUtil.modeloTabla(
                    "ID estudio",
                    "ID funcionario",
                    "Universidad",
                    "Nivel de estudio",
                    "Título del estudio");
    private final JTable tabla = new JTable(modelo);

    public EstudioPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        id.setEditable(false);

        JPanel formulario = UIUtil.formulario();
        UIUtil.campo(formulario, "ID estudio (automático)", id);
        UIUtil.campo(formulario, "Funcionario *", funcionario);
        UIUtil.campo(formulario, "Universidad *", universidad);
        UIUtil.campo(formulario, "Nivel de estudio *", nivel);
        UIUtil.campo(formulario, "Título del estudio *", titulo);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton guardar = new JButton("Guardar nuevo");
        JButton actualizar = new JButton("Actualizar seleccionado");
        JButton eliminar = new JButton("Eliminar seleccionado");
        JButton limpiar = new JButton("Limpiar formulario");
        JButton recargar = new JButton("Recargar");
        acciones.add(guardar);
        acciones.add(actualizar);
        acciones.add(eliminar);
        acciones.add(limpiar);
        acciones.add(recargar);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(formulario, BorderLayout.CENTER);
        encabezado.add(acciones, BorderLayout.SOUTH);

        UIUtil.configurarTabla(tabla);
        add(encabezado, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        guardar.addActionListener(e -> guardar());
        actualizar.addActionListener(e -> actualizar());
        eliminar.addActionListener(e -> eliminar());
        limpiar.addActionListener(e -> limpiar());
        recargar.addActionListener(e -> cargar());
        tabla.getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                                seleccionarFila();
                            }
                        });
        cargar();
    }

    private Estudio leerFormulario() throws ValidacionException {
        FuncionarioOpcion opcion = (FuncionarioOpcion) funcionario.getSelectedItem();
        if (opcion == null) {
            throw new ValidacionException("Primero registra o selecciona un funcionario.");
        }

        Estudio estudio = new Estudio();
        estudio.setIdFuncionario(opcion.id());
        estudio.setUniversidad(UIUtil.obligatorio(universidad, "universidad"));
        estudio.setNivelEstudio(UIUtil.seleccionado(nivel, "nivel de estudio"));
        estudio.setTituloEstudio(UIUtil.obligatorio(titulo, "título del estudio"));
        return estudio;
    }

    private void guardar() {
        try {
            Estudio estudio = leerFormulario();
            dao.insertar(estudio);
            cargarTabla();
            limpiar();
            UIUtil.info(
                    this, "Estudio guardado correctamente (ID " + estudio.getIdEstudio() + ").");
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void actualizar() {
        try {
            long registro = UIUtil.id(id, "estudios");
            Estudio estudio = leerFormulario();
            estudio.setIdEstudio(registro);
            dao.actualizar(estudio);
            cargarTabla();
            limpiar();
            UIUtil.info(this, "Estudio actualizado correctamente.");
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void eliminar() {
        try {
            long registro = UIUtil.id(id, "estudios");
            if (UIUtil.confirmar(this, "¿Eliminar el estudio seleccionado?")) {
                dao.eliminar(registro);
                cargarTabla();
                limpiar();
                UIUtil.info(this, "Estudio eliminado.");
            }
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void cargar() {
        cargarFuncionarios();
        cargarTabla();
    }

    private void cargarFuncionarios() {
        try {
            Object elegido = funcionario.getSelectedItem();
            Long idElegido = elegido instanceof FuncionarioOpcion opcion ? opcion.id() : null;
            funcionario.removeAllItems();
            for (Funcionario registro : funcionarioDAO.listar()) {
                funcionario.addItem(
                        new FuncionarioOpcion(
                                registro.getIdFuncionario(),
                                registro.getNombres()
                                        + " "
                                        + registro.getApellidos()
                                        + " — "
                                        + registro.getNumeroIdentificacion()));
            }
            if (idElegido != null) {
                seleccionarFuncionario(idElegido);
            }
        } catch (RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (Estudio estudio : dao.listar()) {
                modelo.addRow(
                        new Object[] {
                            estudio.getIdEstudio(),
                            estudio.getIdFuncionario(),
                            estudio.getUniversidad(),
                            estudio.getNivelEstudio(),
                            estudio.getTituloEstudio()
                        });
            }
        } catch (RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void seleccionarFila() {
        int fila = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        id.setText(textoCelda(fila, 0));
        seleccionarFuncionario(Long.parseLong(textoCelda(fila, 1)));
        universidad.setText(textoCelda(fila, 2));
        nivel.setSelectedItem(textoCelda(fila, 3));
        titulo.setText(textoCelda(fila, 4));
    }

    private void seleccionarFuncionario(long idFuncionario) {
        for (int indice = 0; indice < funcionario.getItemCount(); indice++) {
            FuncionarioOpcion opcion = funcionario.getItemAt(indice);
            if (opcion.id() == idFuncionario) {
                funcionario.setSelectedIndex(indice);
                return;
            }
        }
    }

    private String textoCelda(int fila, int columna) {
        Object valor = modelo.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void limpiar() {
        tabla.clearSelection();
        id.setText("");
        universidad.setText("");
        titulo.setText("");
        nivel.setSelectedIndex(0);
    }

    private record FuncionarioOpcion(long id, String descripcion) {
        @Override
        public String toString() {
            return id + " — " + descripcion;
        }
    }
}
