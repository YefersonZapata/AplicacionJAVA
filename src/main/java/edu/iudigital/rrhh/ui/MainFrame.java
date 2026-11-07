package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.auth.Rol;
import edu.iudigital.rrhh.auth.SesionUsuario;
import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.AutenticacionDAO;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.*;

public class MainFrame extends JFrame {
    private MainFrame(SesionUsuario sesion, AutenticacionDAO autenticacionDAO) {
        super("Sistema de Recursos Humanos — IU Digital de Antioquia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setMinimumSize(new java.awt.Dimension(980, 650));
        setLocationRelativeTo(null);

        JLabel encabezado = new JLabel("Gestión de Recursos Humanos", SwingConstants.CENTER);
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 21f));
        encabezado.setBorder(BorderFactory.createEmptyBorder(15, 8, 15, 8));
        JLabel identidad =
                new JLabel(
                        sesion.usuario()
                                + " — "
                                + sesion.rol().getEtiqueta()
                                + "     ");
        JButton cerrarSesion = new JButton("Cerrar sesión");
        JPanel barraSesion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        barraSesion.add(identidad);
        barraSesion.add(cerrarSesion);
        cerrarSesion.addActionListener(
                e -> {
                    dispose();
                    iniciarFlujo(autenticacionDAO);
                });
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.add(encabezado, BorderLayout.CENTER);
        cabecera.add(barraSesion, BorderLayout.SOUTH);

        FuncionarioPanel funcionarios = new FuncionarioPanel();
        GrupoFamiliarPanel familiares = new GrupoFamiliarPanel();
        FormacionAcademicaPanel formacion = new FormacionAcademicaPanel();
        CatalogosPanel catalogos = new CatalogosPanel();
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Funcionarios", funcionarios);
        pestanas.addTab("Grupo familiar", familiares);
        pestanas.addTab("Formación académica", formacion);
        pestanas.addTab("Catálogos", catalogos);
        if (sesion.rol() == Rol.ADMIN_RRHH) {
            pestanas.addTab("Cuentas de funcionarios", new CuentasFuncionarioPanel());
        } else {
            UIUtil.aplicarSoloLectura(funcionarios);
            UIUtil.aplicarSoloLectura(familiares);
            UIUtil.aplicarSoloLectura(formacion);
            UIUtil.aplicarSoloLectura(catalogos);
        }
        pestanas.addChangeListener(
                e -> {
                    int indice = pestanas.getSelectedIndex();
                    if (indice == 0) funcionarios.recargar();
                    else if (indice == 1) familiares.recargar();
                    else if (indice == 2) formacion.recargar();
                });

        setLayout(new BorderLayout());
        add(cabecera, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(
                () -> {
                    try (Connection ignored = ConexionBD.obtenerConexion()) {
                        AutenticacionDAO autenticacionDAO = new AutenticacionDAO();
                        autenticacionDAO.inicializarEsquema();
                        iniciarFlujo(autenticacionDAO);
                    } catch (RuntimeException ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                ex.getMessage()
                                        + "\n\n"
                                        + "Verifica que MySQL Server esté iniciado, configura"
                                        + " src/main/resources/db.properties y ejecuta"
                                        + " sql/01_crear_base_datos.sql.",
                                "No se pudo iniciar",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (java.sql.SQLException ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                "No se pudo verificar la conexión a MySQL: " + ex.getMessage(),
                                "No se pudo iniciar",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
    }

    private static void iniciarFlujo(AutenticacionDAO autenticacionDAO) {
        if (!autenticacionDAO.existeAdministrador()
                && !configurarAdministradorInicial(autenticacionDAO)) {
            return;
        }

        SesionUsuario sesion = mostrarInicioSesion(autenticacionDAO);
        if (sesion == null) return;
        JOptionPane.showMessageDialog(
                null,
                "Inicio de sesión exitoso como " + sesion.rol().getEtiqueta() + ".",
                "Sesión iniciada",
                JOptionPane.INFORMATION_MESSAGE);
        new MainFrame(sesion, autenticacionDAO).setVisible(true);
    }

    private static boolean configurarAdministradorInicial(AutenticacionDAO autenticacionDAO) {
        JTextField usuario = new JTextField(20);
        JPasswordField contrasena = new JPasswordField(20);
        JPasswordField confirmar = new JPasswordField(20);
        JPanel formulario = UIUtil.formulario();
        UIUtil.campo(formulario, "Nombre de usuario", usuario);
        UIUtil.campo(formulario, "Contraseña (mínimo 8 caracteres)", contrasena);
        UIUtil.campo(formulario, "Confirmar contraseña", confirmar);
        JOptionPane.showMessageDialog(
                null,
                "No existe una cuenta Admin RRHH. Crea la primera cuenta para continuar.",
                "Configuración inicial",
                JOptionPane.INFORMATION_MESSAGE);

        while (true) {
            int opcion =
                    JOptionPane.showConfirmDialog(
                            null,
                            formulario,
                            "Crear primera cuenta Admin RRHH",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) return false;
            char[] clave = contrasena.getPassword();
            char[] confirmacion = confirmar.getPassword();
            try {
                if (!Arrays.equals(clave, confirmacion)) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Las contraseñas no coinciden.",
                            "Revisa los datos",
                            JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                autenticacionDAO.crearAdministradorInicial(usuario.getText(), clave);
                return true;
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                        null, ex.getMessage(), "No se pudo crear la cuenta", JOptionPane.ERROR_MESSAGE);
            } finally {
                Arrays.fill(clave, '\0');
                Arrays.fill(confirmacion, '\0');
                contrasena.setText("");
                confirmar.setText("");
            }
        }
    }

    private static SesionUsuario mostrarInicioSesion(AutenticacionDAO autenticacionDAO) {
        JTextField usuario = new JTextField(20);
        JPasswordField contrasena = new JPasswordField(20);
        char caracterOculto = contrasena.getEchoChar();
        JCheckBox mostrarContrasena = new JCheckBox("Mostrar contraseña");
        mostrarContrasena.addActionListener(
                e ->
                        contrasena.setEchoChar(
                                mostrarContrasena.isSelected() ? (char) 0 : caracterOculto));
        JPanel formulario = UIUtil.formulario();
        UIUtil.campo(formulario, "Usuario", usuario);
        UIUtil.campo(formulario, "Contraseña", contrasena);
        UIUtil.campo(formulario, "", mostrarContrasena);

        while (true) {
            int opcion =
                    JOptionPane.showConfirmDialog(
                            null,
                            formulario,
                            "Inicio de sesión",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) return null;
            char[] clave = contrasena.getPassword();
            try {
                Optional<SesionUsuario> sesion =
                        autenticacionDAO.autenticar(usuario.getText(), clave);
                if (sesion.isPresent()) return sesion.get();
                JOptionPane.showMessageDialog(
                        null,
                        "El usuario o la contraseña no son correctos, si tiene problemas para iniciar sesión contacte al administrador del sistema.",
                        "Acceso denegado",
                        JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(
                        null, ex.getMessage(), "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
                return null;
            } finally {
                Arrays.fill(clave, '\0');
                contrasena.setText("");
            }
        }
    }
}
