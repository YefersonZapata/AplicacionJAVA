package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.exception.ValidacionException;

import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

final class UIUtil {
    private UIUtil() {}

    static void campo(JPanel panel, String etiqueta, JComponent control) {
        panel.add(new JLabel(etiqueta));
        panel.add(control);
    }

    static JPanel formulario() {
        JPanel p = new JPanel(new java.awt.GridLayout(0, 2, 8, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return p;
    }

    static DefaultTableModel modeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    static void configurarTabla(JTable tabla) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowHeight(23);
        tabla.getTableHeader().setFont(tabla.getTableHeader().getFont().deriveFont(Font.BOLD));
    }

    static void ocultarColumna(JTable tabla, int indice) {
        javax.swing.table.TableColumn columna = tabla.getColumnModel().getColumn(indice);
        columna.setMinWidth(0);
        columna.setMaxWidth(0);
        columna.setPreferredWidth(0);
    }

    static String obligatorio(JTextField campo, String nombre) throws ValidacionException {
        String v = campo.getText() == null ? "" : campo.getText().trim();
        if (v.isEmpty()) throw new ValidacionException("El campo «" + nombre + "» es obligatorio.");
        return v;
    }

    static String texto(JTextField campo) {
        String v = campo.getText() == null ? "" : campo.getText().trim();
        return v.isEmpty() ? null : v;
    }

    static LocalDate fecha(JTextField campo, String nombre, boolean obligatoria)
            throws ValidacionException {
        String t = campo.getText() == null ? "" : campo.getText().trim();
        if (t.isEmpty()) {
            if (obligatoria)
                throw new ValidacionException(
                        "El campo «" + nombre + "» es obligatorio. Usa AAAA-MM-DD.");
            return null;
        }
        try {
            LocalDate f = LocalDate.parse(t);
            if (f.isAfter(LocalDate.now()))
                throw new ValidacionException(
                        "La fecha de «" + nombre + "» no puede estar en el futuro.");
            return f;
        } catch (DateTimeParseException e) {
            throw new ValidacionException(
                    "La fecha de «"
                            + nombre
                            + "» no es válida. Usa AAAA-MM-DD (por ejemplo, 2001-08-25).");
        }
    }

    static <T> T seleccionado(JComboBox<T> combo, String nombre) throws ValidacionException {
        if (combo.getSelectedItem() == null || combo.getSelectedIndex() < 0)
            throw new ValidacionException(
                    "No hay valores disponibles para «"
                            + nombre
                            + "». Añade al menos un registro en la pestaña Catálogos.");
        return combo.getItemAt(combo.getSelectedIndex());
    }

    static void error(Component padre, Exception e) {
        JOptionPane.showMessageDialog(
                padre,
                e.getMessage() == null ? e.toString() : e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(
                padre, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirmar(Component padre, String mensaje) {
        return JOptionPane.showConfirmDialog(
                        padre,
                        mensaje,
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    static long id(JTextField campo, String entidad) throws ValidacionException {
        String t = obligatorio(campo, "registro seleccionado");
        try {
            return Long.parseLong(t);
        } catch (NumberFormatException e) {
            throw new ValidacionException(
                    "Selecciona un registro válido de " + entidad + " en la tabla.");
        }
    }
}
