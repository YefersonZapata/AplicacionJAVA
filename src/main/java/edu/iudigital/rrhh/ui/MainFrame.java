package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.config.ConexionBD;

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.Connection;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Sistema de Recursos Humanos — IU Digital de Antioquia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setMinimumSize(new java.awt.Dimension(980, 650));
        setLocationRelativeTo(null);
        JLabel encabezado = new JLabel("Gestión de Recursos Humanos", SwingConstants.CENTER);
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 21f));
        encabezado.setBorder(BorderFactory.createEmptyBorder(15, 8, 15, 8));
        FuncionarioPanel funcionarios = new FuncionarioPanel();
        GrupoFamiliarPanel familiares = new GrupoFamiliarPanel();
        FormacionAcademicaPanel formacion = new FormacionAcademicaPanel();
        CatalogosPanel catalogos = new CatalogosPanel();
        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Funcionarios", funcionarios);
        pestañas.addTab("Grupo familiar", familiares);
        pestañas.addTab("Formación académica", formacion);
        pestañas.addTab("Catálogos", catalogos);
        pestañas.addChangeListener(
                e -> {
                    int i = pestañas.getSelectedIndex();
                    if (i == 0) funcionarios.recargar();
                    else if (i == 1) familiares.recargar();
                    else if (i == 2) formacion.recargar();
                });
        setLayout(new BorderLayout());
        add(encabezado, BorderLayout.NORTH);
        add(pestañas, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(
                () -> {
                    try (Connection ignored = ConexionBD.obtenerConexion()) {
                        new MainFrame().setVisible(true);
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
}
