package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.dao.EstadoCivilDAO;
import edu.iudigital.rrhh.dao.TipoDocumentoDAO;
import edu.iudigital.rrhh.dao.impl.EstadoCivilDAOImpl;
import edu.iudigital.rrhh.dao.impl.TipoDocumentoDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.EstadoCivil;
import edu.iudigital.rrhh.model.TipoDocumento;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Permite administrar los dos catálogos que alimentan los formularios de funcionarios. */
public class CatalogosPanel extends JPanel {
    public CatalogosPanel() {
        super(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Estados civiles", new EstadoCivilPanel());
        tabs.addTab("Tipos de documento", new TipoDocumentoPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private static class EstadoCivilPanel extends JPanel {
        private final EstadoCivilDAO dao = new EstadoCivilDAOImpl();
        private final JTextField id = new JTextField(), nombre = new JTextField();
        private final DefaultTableModel modelo = UIUtil.modeloTabla("ID", "Estado civil");
        private final JTable tabla = new JTable(modelo);

        EstadoCivilPanel() {
            super(new BorderLayout(8, 8));
            id.setEditable(false);
            JPanel form = UIUtil.formulario();
            UIUtil.campo(form, "ID (automático)", id);
            UIUtil.campo(form, "Nombre *", nombre);
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton guardar = new JButton("Guardar nuevo"),
                    actualizar = new JButton("Actualizar seleccionado"),
                    eliminar = new JButton("Eliminar seleccionado"),
                    limpiar = new JButton("Limpiar"),
                    recargar = new JButton("Recargar");
            for (JButton b : new JButton[] {guardar, actualizar, eliminar, limpiar, recargar})
                botones.add(b);
            JPanel arriba = new JPanel(new BorderLayout());
            arriba.add(form, BorderLayout.CENTER);
            arriba.add(botones, BorderLayout.SOUTH);
            UIUtil.configurarTabla(tabla);
            add(arriba, BorderLayout.NORTH);
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
                                    int r = tabla.convertRowIndexToModel(tabla.getSelectedRow());
                                    id.setText(modelo.getValueAt(r, 0).toString());
                                    nombre.setText(modelo.getValueAt(r, 1).toString());
                                }
                            });
            cargar();
        }

        private void guardar() {
            try {
                EstadoCivil e = new EstadoCivil();
                e.setNombre(UIUtil.obligatorio(nombre, "nombre del estado civil"));
                dao.insertar(e);
                cargar();
                limpiar();
                UIUtil.info(this, "Estado civil guardado.");
            } catch (ValidacionException | RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void actualizar() {
            try {
                EstadoCivil e = new EstadoCivil();
                e.setIdEstadoCivil(UIUtil.id(id, "estados civiles"));
                e.setNombre(UIUtil.obligatorio(nombre, "nombre del estado civil"));
                dao.actualizar(e);
                cargar();
                limpiar();
                UIUtil.info(this, "Estado civil actualizado.");
            } catch (ValidacionException | RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void eliminar() {
            try {
                long valor = UIUtil.id(id, "estados civiles");
                if (UIUtil.confirmar(
                        this,
                        "¿Eliminar el estado civil seleccionado? Si está asociado a un funcionario,"
                                + " MySQL no permitirá borrarlo.")) {
                    dao.eliminar(valor);
                    cargar();
                    limpiar();
                    UIUtil.info(this, "Estado civil eliminado.");
                }
            } catch (ValidacionException | RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void cargar() {
            try {
                modelo.setRowCount(0);
                for (EstadoCivil e : dao.listar())
                    modelo.addRow(new Object[] {e.getIdEstadoCivil(), e.getNombre()});
            } catch (RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void limpiar() {
            tabla.clearSelection();
            id.setText("");
            nombre.setText("");
        }
    }

    private static class TipoDocumentoPanel extends JPanel {
        private final TipoDocumentoDAO dao = new TipoDocumentoDAOImpl();
        private final JTextField id = new JTextField(),
                nombre = new JTextField(),
                sigla = new JTextField();
        private final DefaultTableModel modelo = UIUtil.modeloTabla("ID", "Nombre", "Sigla");
        private final JTable tabla = new JTable(modelo);

        TipoDocumentoPanel() {
            super(new BorderLayout(8, 8));
            id.setEditable(false);
            JPanel form = UIUtil.formulario();
            UIUtil.campo(form, "ID (automático)", id);
            UIUtil.campo(form, "Nombre *", nombre);
            UIUtil.campo(form, "Sigla *", sigla);
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton guardar = new JButton("Guardar nuevo"),
                    actualizar = new JButton("Actualizar seleccionado"),
                    eliminar = new JButton("Eliminar seleccionado"),
                    limpiar = new JButton("Limpiar"),
                    recargar = new JButton("Recargar");
            for (JButton b : new JButton[] {guardar, actualizar, eliminar, limpiar, recargar})
                botones.add(b);
            JPanel arriba = new JPanel(new BorderLayout());
            arriba.add(form, BorderLayout.CENTER);
            arriba.add(botones, BorderLayout.SOUTH);
            UIUtil.configurarTabla(tabla);
            add(arriba, BorderLayout.NORTH);
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
                                    int r = tabla.convertRowIndexToModel(tabla.getSelectedRow());
                                    id.setText(modelo.getValueAt(r, 0).toString());
                                    nombre.setText(modelo.getValueAt(r, 1).toString());
                                    sigla.setText(modelo.getValueAt(r, 2).toString());
                                }
                            });
            cargar();
        }

        private void guardar() {
            try {
                TipoDocumento t = new TipoDocumento();
                t.setNombre(UIUtil.obligatorio(nombre, "nombre del tipo de documento"));
                t.setSigla(UIUtil.obligatorio(sigla, "sigla"));
                dao.insertar(t);
                cargar();
                limpiar();
                UIUtil.info(this, "Tipo de documento guardado.");
            } catch (ValidacionException | RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void actualizar() {
            try {
                TipoDocumento t = new TipoDocumento();
                t.setIdTipoDocumento(UIUtil.id(id, "tipos de documento"));
                t.setNombre(UIUtil.obligatorio(nombre, "nombre del tipo de documento"));
                t.setSigla(UIUtil.obligatorio(sigla, "sigla"));
                dao.actualizar(t);
                cargar();
                limpiar();
                UIUtil.info(this, "Tipo de documento actualizado.");
            } catch (ValidacionException | RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void eliminar() {
            try {
                long valor = UIUtil.id(id, "tipos de documento");
                if (UIUtil.confirmar(
                        this,
                        "¿Eliminar el tipo de documento seleccionado? Si está asociado a un"
                                + " funcionario o familiar, MySQL no permitirá borrarlo.")) {
                    dao.eliminar(valor);
                    cargar();
                    limpiar();
                    UIUtil.info(this, "Tipo de documento eliminado.");
                }
            } catch (ValidacionException | RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void cargar() {
            try {
                modelo.setRowCount(0);
                for (TipoDocumento t : dao.listar())
                    modelo.addRow(
                            new Object[] {t.getIdTipoDocumento(), t.getNombre(), t.getSigla()});
            } catch (RuntimeException e) {
                UIUtil.error(this, e);
            }
        }

        private void limpiar() {
            tabla.clearSelection();
            id.setText("");
            nombre.setText("");
            sigla.setText("");
        }
    }
}
