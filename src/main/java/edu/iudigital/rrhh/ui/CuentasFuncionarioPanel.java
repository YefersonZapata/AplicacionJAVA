package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.auth.Rol;
import edu.iudigital.rrhh.dao.AutenticacionDAO;
import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.Funcionario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CuentasFuncionarioPanel extends JPanel {
    private final AutenticacionDAO autenticacionDAO = new AutenticacionDAO();
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAOImpl();
    private final JComboBox<FuncionarioOpcion> funcionario = new JComboBox<>();
    private final JComboBox<Rol> rol = new JComboBox<>(Rol.values());
    private final JTextField usuario = new JTextField();
    private final JPasswordField contrasena = new JPasswordField();
    private final DefaultTableModel modeloFuncionarios =
            UIUtil.modeloTabla("ID cuenta", "Funcionario", "Usuario");
    private final JTable tablaFuncionarios = new JTable(modeloFuncionarios);
    private final DefaultTableModel modeloAdministradores =
            UIUtil.modeloTabla("ID cuenta", "Administrador", "Usuario");
    private final JTable tablaAdministradores = new JTable(modeloAdministradores);

    public CuentasFuncionarioPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel formulario = UIUtil.formulario();
        UIUtil.campo(formulario, "Funcionario *", funcionario);
        UIUtil.campo(formulario, "Rol de la cuenta *", rol);
        UIUtil.campo(formulario, "Nombre de usuario *", usuario);
        UIUtil.campo(formulario, "Contraseña * (mínimo 8 caracteres)", contrasena);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton crear = new JButton("Crear cuenta"),
                otorgarAdmin = new JButton("Otorgar Admin RRHH a cuenta seleccionada"),
                cambiar = new JButton("Cambiar contraseña seleccionada"),
                eliminar = new JButton("Eliminar cuenta seleccionada"),
                recargar = new JButton("Recargar");
        acciones.add(crear);
        acciones.add(otorgarAdmin);
        acciones.add(cambiar);
        acciones.add(eliminar);
        acciones.add(recargar);
        JPanel arriba = new JPanel(new BorderLayout());
        arriba.add(formulario, BorderLayout.CENTER);
        arriba.add(acciones, BorderLayout.SOUTH);

        configurarTablaCuentas(tablaFuncionarios);
        configurarTablaCuentas(tablaAdministradores);
        JSplitPane tablas =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        crearTablaPanel("Cuentas de funcionarios", tablaFuncionarios),
                        crearTablaPanel("Cuentas Admin RRHH", tablaAdministradores));
        tablas.setResizeWeight(0.5);
        tablas.setContinuousLayout(true);
        tablas.setOneTouchExpandable(true);
        tablas.setPreferredSize(new java.awt.Dimension(600, 400));
        add(arriba, BorderLayout.NORTH);
        add(tablas, BorderLayout.CENTER);

        crear.addActionListener(e -> crearCuenta());
        otorgarAdmin.addActionListener(e -> otorgarAdmin());
        cambiar.addActionListener(e -> cambiarContrasena());
        eliminar.addActionListener(e -> eliminarCuenta());
        recargar.addActionListener(e -> recargar());
        SwingUtilities.invokeLater(() -> tablas.setDividerLocation(0.5));
        recargar();
    }

    private void recargar() {
        try {
            Long seleccionado =
                    funcionario.getSelectedItem() instanceof FuncionarioOpcion opcion
                            ? opcion.id()
                            : null;
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
            if (seleccionado != null) {
                for (int indice = 0; indice < funcionario.getItemCount(); indice++) {
                    if (funcionario.getItemAt(indice).id() == seleccionado) {
                        funcionario.setSelectedIndex(indice);
                        break;
                    }
                }
            }
            modeloFuncionarios.setRowCount(0);
            modeloAdministradores.setRowCount(0);
            for (AutenticacionDAO.CuentaUsuario cuenta : autenticacionDAO.listarCuentas()) {
                DefaultTableModel modelo =
                        cuenta.rol() == Rol.ADMIN_RRHH
                                ? modeloAdministradores
                                : modeloFuncionarios;
                modelo.addRow(
                        new Object[] {
                            cuenta.id(), cuenta.nombreFuncionario(), cuenta.usuario()
                        });
            }
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void crearCuenta() {
        char[] clave = contrasena.getPassword();
        try {
            FuncionarioOpcion opcion = UIUtil.seleccionado(funcionario, "funcionario");
            Rol rolSeleccionado = UIUtil.seleccionado(rol, "rol");
            autenticacionDAO.crearCuentaFuncionario(
                    opcion.id(),
                    UIUtil.obligatorio(usuario, "nombre de usuario"),
                    clave,
                    rolSeleccionado);
            limpiar();
            recargar();
            UIUtil.info(
                    this,
                    "Cuenta creada correctamente con el rol "
                            + rolSeleccionado.getEtiqueta()
                            + ".");
        } catch (ValidacionException | RuntimeException e) {
            UIUtil.error(this, e);
        } finally {
            java.util.Arrays.fill(clave, '\0');
        }
    }

    private void otorgarAdmin() {
        try {
            long idCuenta = idCuentaSeleccionada();
            if (UIUtil.confirmar(
                    this,
                    "¿Otorgar el rol Admin RRHH a la cuenta seleccionada? "
                            + "El usuario tendrá permisos para administrar la aplicación.")) {
                autenticacionDAO.otorgarRolAdmin(idCuenta);
                recargar();
                UIUtil.info(
                        this,
                        "Rol Admin RRHH otorgado correctamente. La cuenta ya puede iniciar sesión"
                                + " con sus nuevas facultades.");
            }
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private void cambiarContrasena() {
        char[] clave = contrasena.getPassword();
        try {
            autenticacionDAO.cambiarContrasenaFuncionario(idCuentaSeleccionada(), clave);
            limpiar();
            UIUtil.info(this, "Contraseña actualizada correctamente.");
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        } finally {
            java.util.Arrays.fill(clave, '\0');
        }
    }

    private void eliminarCuenta() {
        try {
            if (UIUtil.confirmar(this, "¿Eliminar la cuenta de acceso seleccionada?")) {
                autenticacionDAO.eliminarCuentaFuncionario(idCuentaSeleccionada());
                recargar();
                limpiar();
                UIUtil.info(this, "Cuenta eliminada. El registro del funcionario se conserva.");
            }
        } catch (RuntimeException e) {
            UIUtil.error(this, e);
        }
    }

    private long idCuentaSeleccionada() {
        int fila = tablaFuncionarios.getSelectedRow();
        if (fila < 0) throw new IllegalArgumentException("Selecciona una cuenta en la tabla.");
        return ((Number)
                        modeloFuncionarios.getValueAt(
                                tablaFuncionarios.convertRowIndexToModel(fila), 0))
                .longValue();
    }

    private static void configurarTablaCuentas(JTable tabla) {
        UIUtil.configurarTabla(tabla);
        UIUtil.ocultarColumna(tabla, 0);
    }

    private static JPanel crearTablaPanel(String titulo, JTable tabla) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void limpiar() {
        usuario.setText("");
        contrasena.setText("");
        rol.setSelectedItem(Rol.FUNCIONARIO);
    }

    private record FuncionarioOpcion(long id, String etiqueta) {
        @Override
        public String toString() {
            return etiqueta;
        }
    }
}
