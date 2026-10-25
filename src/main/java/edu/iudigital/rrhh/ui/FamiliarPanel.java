package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.dao.FamiliarDAO;
import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.dao.impl.FamiliarDAOImpl;
import edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.Familiar;
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

public class FamiliarPanel extends JPanel {
    private final FamiliarDAO dao = new FamiliarDAOImpl();
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private final JTextField id = new JTextField();
    private final JTextField numero = new JTextField();
    private final JTextField nombres = new JTextField();
    private final JTextField apellidos = new JTextField();
    private final JTextField rol = new JTextField();
    private final JTextField fecha = new JTextField();
    private final JTextField telefono = new JTextField();
    private final JComboBox<String> tipo =
            new JComboBox<>(
                    new String[] {
                        "",
                        "Cédula de ciudadanía",
                        "Cédula de extranjería",
                        "Tarjeta de identidad",
                        "Pasaporte",
                        "PPT",
                        "Otro"
                    });
    private final JComboBox<FuncionarioOpcion> funcionario = new JComboBox<>();
    private final DefaultTableModel modelo =
            UIUtil.modeloTabla(
                    "ID familiar",
                    "ID funcionario",
                    "Tipo identificación",
                    "Número",
                    "Nombres",
                    "Apellidos",
                    "Rol familiar",
                    "Nacimiento",
                    "Teléfono");
    private final JTable tabla = new JTable(modelo);

    public FamiliarPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        id.setEditable(false);

        JPanel formulario = UIUtil.formulario();
        UIUtil.campo(formulario, "ID familiar (automático)", id);
        UIUtil.campo(formulario, "Funcionario responsable *", funcionario);
        UIUtil.campo(formulario, "Tipo identificación", tipo);
        UIUtil.campo(formulario, "Número de identificación", numero);
        UIUtil.campo(formulario, "Nombres *", nombres);
        UIUtil.campo(formulario, "Apellidos *", apellidos);
        UIUtil.campo(formulario, "Rol familiar *", rol);
        UIUtil.campo(formulario, "Fecha nacimiento (AAAA-MM-DD)", fecha);
        UIUtil.campo(formulario, "Teléfono", telefono);

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

    private Familiar leerFormulario() throws ValidacionException {
        FuncionarioOpcion opcion = (FuncionarioOpcion) funcionario.getSelectedItem();
        if (opcion == null) {
            throw new ValidacionException("Primero registra o selecciona un funcionario.");
        }

        Familiar familiar = new Familiar();
        familiar.setIdFuncionario(opcion.id());
        String tipoIdentificacion = (String) tipo.getSelectedItem();
        familiar.setTipoIdentificacion(
                tipoIdentificacion == null || tipoIdentificacion.isBlank()
                        ? null
                        : tipoIdentificacion);
        familiar.setNumeroIdentificacion(UIUtil.texto(numero));
        familiar.setNombres(UIUtil.obligatorio(nombres, "nombres del familiar"));
        familiar.setApellidos(UIUtil.obligatorio(apellidos, "apellidos del familiar"));
        familiar.setRol(UIUtil.obligatorio(rol, "rol familiar"));
        familiar.setFechaNacimiento(UIUtil.fecha(fecha, "fecha de nacimiento del familiar", false));
        familiar.setTelefono(UIUtil.texto(telefono));
        return familiar;
    }

    private void guardar() {
        try {
            Familiar familiar = leerFormulario();
            dao.insertar(familiar);
            cargarTabla();
            limpiar();
            UIUtil.info(
                    this, "Familiar guardado correctamente (ID " + familiar.getIdFamiliar() + ").");
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void actualizar() {
        try {
            long registro = UIUtil.id(id, "familiares");
            Familiar familiar = leerFormulario();
            familiar.setIdFamiliar(registro);
            dao.actualizar(familiar);
            cargarTabla();
            limpiar();
            UIUtil.info(this, "Familiar actualizado correctamente.");
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
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
            for (Familiar familiar : dao.listar()) {
                modelo.addRow(
                        new Object[] {
                            familiar.getIdFamiliar(),
                            familiar.getIdFuncionario(),
                            familiar.getTipoIdentificacion(),
                            familiar.getNumeroIdentificacion(),
                            familiar.getNombres(),
                            familiar.getApellidos(),
                            familiar.getRol(),
                            familiar.getFechaNacimiento(),
                            familiar.getTelefono()
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
        tipo.setSelectedItem(textoCelda(fila, 2));
        numero.setText(textoCelda(fila, 3));
        nombres.setText(textoCelda(fila, 4));
        apellidos.setText(textoCelda(fila, 5));
        rol.setText(textoCelda(fila, 6));
        fecha.setText(textoCelda(fila, 7));
        telefono.setText(textoCelda(fila, 8));
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
        numero.setText("");
        nombres.setText("");
        apellidos.setText("");
        rol.setText("");
        fecha.setText("");
        telefono.setText("");
        tipo.setSelectedIndex(0);
    }

    private record FuncionarioOpcion(long id, String descripcion) {
        @Override
        public String toString() {
            return id + " — " + descripcion;
        }
    }
}
