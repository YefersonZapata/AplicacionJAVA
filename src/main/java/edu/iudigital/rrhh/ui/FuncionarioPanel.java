package edu.iudigital.rrhh.ui;

import edu.iudigital.rrhh.dao.EstadoCivilDAO;
import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.dao.TipoDocumentoDAO;
import edu.iudigital.rrhh.dao.impl.EstadoCivilDAOImpl;
import edu.iudigital.rrhh.dao.impl.FuncionarioDAOImpl;
import edu.iudigital.rrhh.dao.impl.TipoDocumentoDAOImpl;
import edu.iudigital.rrhh.exception.ValidacionException;
import edu.iudigital.rrhh.model.EstadoCivil;
import edu.iudigital.rrhh.model.Funcionario;
import edu.iudigital.rrhh.model.TipoDocumento;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FuncionarioPanel extends JPanel {
    private final FuncionarioDAO dao = new FuncionarioDAOImpl();
    private final TipoDocumentoDAO tipoDAO = new TipoDocumentoDAOImpl();
    private final EstadoCivilDAO estadoDAO = new EstadoCivilDAOImpl();
    private final JTextField id = new JTextField(),
            numero = new JTextField(),
            nombres = new JTextField(),
            apellidos = new JTextField(),
            direccion = new JTextField(),
            telefono = new JTextField(),
            fecha = new JTextField();
    private final JComboBox<TipoDocumento> tipo = new JComboBox<>();
    private final JComboBox<EstadoCivil> estado = new JComboBox<>();
    private final JComboBox<String> sexo =
            new JComboBox<>(new String[] {"Femenino", "Masculino", "Intersexual", "No especifica"});
    private final DefaultTableModel modelo =
            UIUtil.modeloTabla(
                    "ID",
                    "Tipo documento",
                    "Número",
                    "Nombres",
                    "Apellidos",
                    "Estado civil",
                    "Sexo",
                    "Dirección",
                    "Teléfono",
                    "Nacimiento",
                    "idTipoDoc",
                    "idEstadoCivil");
    private final JTable tabla = new JTable(modelo);

    public FuncionarioPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        id.setEditable(false);
        JPanel form = UIUtil.formulario();
        UIUtil.campo(form, "ID (automático)", id);
        UIUtil.campo(form, "Tipo de documento *", tipo);
        UIUtil.campo(form, "Número de identificación *", numero);
        UIUtil.campo(form, "Nombres *", nombres);
        UIUtil.campo(form, "Apellidos *", apellidos);
        UIUtil.campo(form, "Estado civil *", estado);
        UIUtil.campo(form, "Sexo *", sexo);
        UIUtil.campo(form, "Dirección", direccion);
        UIUtil.campo(form, "Teléfono", telefono);
        UIUtil.campo(form, "Fecha nacimiento * (AAAA-MM-DD)", fecha);
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
        UIUtil.ocultarColumna(tabla, 10);
        UIUtil.ocultarColumna(tabla, 11);
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
        cargarCatalogos();
        cargarTabla();
    }

    private void cargarCatalogos() {
        Long tipoActual =
                tipo.getSelectedItem() == null
                        ? null
                        : ((TipoDocumento) tipo.getSelectedItem()).getIdTipoDocumento();
        Long estadoActual =
                estado.getSelectedItem() == null
                        ? null
                        : ((EstadoCivil) estado.getSelectedItem()).getIdEstadoCivil();
        try {
            tipo.removeAllItems();
            for (TipoDocumento t : tipoDAO.listar()) tipo.addItem(t);
            seleccionarTipo(tipoActual);
            estado.removeAllItems();
            for (EstadoCivil ec : estadoDAO.listar()) estado.addItem(ec);
            seleccionarEstado(estadoActual);
        } catch (RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private Funcionario leerFormulario() throws ValidacionException {
        TipoDocumento td = UIUtil.seleccionado(tipo, "tipo de documento");
        EstadoCivil ec = UIUtil.seleccionado(estado, "estado civil");
        Funcionario f = new Funcionario();
        f.setIdTipoDocumento(td.getIdTipoDocumento());
        f.setIdEstadoCivil(ec.getIdEstadoCivil());
        f.setNumeroIdentificacion(UIUtil.obligatorio(numero, "número de identificación"));
        f.setNombres(UIUtil.obligatorio(nombres, "nombres"));
        f.setApellidos(UIUtil.obligatorio(apellidos, "apellidos"));
        f.setSexo(UIUtil.seleccionado(sexo, "sexo"));
        f.setDireccion(UIUtil.texto(direccion));
        f.setTelefono(UIUtil.texto(telefono));
        f.setFechaNacimiento(UIUtil.fecha(fecha, "fecha de nacimiento", true));
        return f;
    }

    private void guardar() {
        try {
            Funcionario f = leerFormulario();
            dao.insertar(f);
            cargarTabla();
            limpiar();
            UIUtil.info(
                    this, "Funcionario guardado correctamente (ID " + f.getIdFuncionario() + ").");
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void actualizar() {
        try {
            long registro = UIUtil.id(id, "funcionarios");
            Funcionario f = leerFormulario();
            f.setIdFuncionario(registro);
            dao.actualizar(f);
            cargarTabla();
            limpiar();
            UIUtil.info(this, "Funcionario actualizado correctamente.");
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void eliminar() {
        try {
            long registro = UIUtil.id(id, "funcionarios");
            if (UIUtil.confirmar(
                    this,
                    "¿Eliminar el funcionario? También se eliminarán sus familiares y registros"
                            + " académicos asociados.")) {
                dao.eliminar(registro);
                cargarTabla();
                limpiar();
                UIUtil.info(this, "Funcionario eliminado.");
            }
        } catch (ValidacionException | RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void cargarTabla() {
        try {
            modelo.setRowCount(0);
            for (Funcionario f : dao.listar())
                modelo.addRow(
                        new Object[] {
                            f.getIdFuncionario(),
                            f.getTipoDocumentoSigla() + " — " + f.getTipoDocumentoNombre(),
                            f.getNumeroIdentificacion(),
                            f.getNombres(),
                            f.getApellidos(),
                            f.getEstadoCivilNombre(),
                            f.getSexo(),
                            f.getDireccion(),
                            f.getTelefono(),
                            f.getFechaNacimiento(),
                            f.getIdTipoDocumento(),
                            f.getIdEstadoCivil()
                        });
        } catch (RuntimeException ex) {
            UIUtil.error(this, ex);
        }
    }

    private void seleccionarFila() {
        int r = tabla.convertRowIndexToModel(tabla.getSelectedRow());
        id.setText(txt(r, 0));
        numero.setText(txt(r, 2));
        nombres.setText(txt(r, 3));
        apellidos.setText(txt(r, 4));
        estado.setSelectedItem(buscarEstado(Long.parseLong(txt(r, 11))));
        sexo.setSelectedItem(txt(r, 6));
        direccion.setText(txt(r, 7));
        telefono.setText(txt(r, 8));
        fecha.setText(txt(r, 9));
        tipo.setSelectedItem(buscarTipo(Long.parseLong(txt(r, 10))));
    }

    private TipoDocumento buscarTipo(long idTipo) {
        for (int i = 0; i < tipo.getItemCount(); i++)
            if (tipo.getItemAt(i).getIdTipoDocumento() == idTipo) return tipo.getItemAt(i);
        return null;
    }

    private EstadoCivil buscarEstado(long idEstado) {
        for (int i = 0; i < estado.getItemCount(); i++)
            if (estado.getItemAt(i).getIdEstadoCivil() == idEstado) return estado.getItemAt(i);
        return null;
    }

    private void seleccionarTipo(Long idTipo) {
        if (idTipo != null) {
            TipoDocumento t = buscarTipo(idTipo);
            if (t != null) tipo.setSelectedItem(t);
        }
    }

    private void seleccionarEstado(Long idEstado) {
        if (idEstado != null) {
            EstadoCivil e = buscarEstado(idEstado);
            if (e != null) estado.setSelectedItem(e);
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
        direccion.setText("");
        telefono.setText("");
        fecha.setText("");
        if (tipo.getItemCount() > 0) tipo.setSelectedIndex(0);
        if (estado.getItemCount() > 0) estado.setSelectedIndex(0);
        sexo.setSelectedIndex(0);
    }
}
