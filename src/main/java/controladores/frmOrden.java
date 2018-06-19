/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import clases.Categoria;
import clases.DetalleOrden;
import clases.Orden;
import clases.Producto;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import manejadores.ManejadorCategorias;
import manejadores.ManejadorOrdenes;
import manejadores.ManejadorProductos;
import org.primefaces.event.SelectEvent;
import printer.Ticket;

/**
 *
 * @author yovany
 */
@Named(value = "frmOrden")
@ViewScoped
public class frmOrden implements Serializable {

    private Orden orden;
    private List<Orden> ordenes;
    private List<Orden> filtro;
    private Double efectivo;
    private Double cambio;
    private int idOrden;
    private List<Categoria> categorias;
    private boolean btnCobrar = false;
    private Double cantidad = 0.0;
    private List<DetalleOrden> detalle;
    private List<DetalleOrden> detalle2;
    private Categoria categoria;
    private int idCategoria = 1;
    private List<Producto> productos;
    private Producto producto;
    private DetalleOrden detail;
    private List<Fake> fake;
    private List<Fake> fakeAgregar;
    private List<Fake> fake2;
    private Fake fakeel;
    private String efect;

    @PostConstruct
    public void init() {
        orden = new Orden();
        ordenes = new ArrayList<Orden>();
        detalle = new ArrayList<DetalleOrden>();
        detalle2 = new ArrayList<DetalleOrden>();
        try {
            ordenes = ManejadorOrdenes.ObtenerActivas();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        filtro = new ArrayList<Orden>();
        filtro = ordenes;
        fake = new ArrayList<Fake>();

    }

    public List<Orden> getOrdenes() {
        try {
            ordenes = ManejadorOrdenes.ObtenerActivas();
            filtro = ordenes;
            return ordenes;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public void onRowSelect(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                orden = (Orden) se.getObject();
                idOrden = orden.idOrden;
                detalle2 = new ArrayList<DetalleOrden>();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void quitarProductos(Fake pr) {
        System.out.println("Esta en clic (-)");
        if (pr != null) {
            try {
                if (pr.getCantidad() > 0.0) {
                    orden.EliminarProducto(pr.getProducto(), 1.0);
                }

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void agregarProductosDetalle(DetalleOrden dt) {
        try {
            orden.AgregarProducto(dt.getProducto(), 1.0);
            detalle = orden.getDetalleOrdenList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void quitarProductosDetalle(DetalleOrden dt) {
        try {
            orden.EliminarProducto(dt.getProducto(), 1.0);
            detalle = orden.getDetalleOrdenList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void onRowSelectCategoria(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                categoria = (Categoria) se.getObject();
                idCategoria = categoria.idCategoria;
                getFakeAgregar();
                getFake();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void agregarProductos(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                fakeel = (Fake) se.getObject();
                orden.AgregarProducto(fakeel.getProducto(), 1.0);

            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void quitarCompleto(DetalleOrden dt) {
        try {
            orden.EliminarProducto(dt.getProducto(), dt.cantidad);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void agregarProductosAdd(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                fakeel = (Fake) se.getObject();
                fakeel.setCantidad(fakeel.getCantidad() + 1.0);
                if (detalle2.isEmpty()) {
                    detalle2.add(new DetalleOrden(orden, fakeel.getProducto(), fakeel.getCantidad()));
                } else {
                    boolean encontrado = false;
                    for (int i = 0; i < detalle2.size(); i++) {
                        if (detalle2.get(i).producto.idProducto == fakeel.getProducto().getIdProducto()) {
                            detalle2.get(i).cantidad = fakeel.getCantidad();
                            encontrado = true;
                        }
                    }
                    if (!encontrado) {
                        detalle2.add(new DetalleOrden(orden, fakeel.getProducto(), fakeel.getCantidad()));
                    }
                }
                orden.AgregarProducto(fakeel.getProducto(), 1.0);
                detalle = orden.getDetalleOrdenList();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void quitarProductosAdd(Fake fk) {
        if (fk != null) {
            try {
                fakeel = fk;

                fakeel.setCantidad(fakeel.getCantidad() - 1.0);
                for (int i = 0; i < detalle2.size(); i++) {
                    if (detalle2.get(i).producto.idProducto == fakeel.getProducto().getIdProducto()) {
                        detalle2.get(i).cantidad = fakeel.getCantidad();
                        if (detalle2.get(i).cantidad == 0.0) {
                            detalle2.remove(detalle2.get(i));
                        }
                    }
                }
                orden.EliminarProducto(fakeel.getProducto(), 1.0);
                detalle = orden.getDetalleOrdenList();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void nueva() throws IOException {
        try {
            orden = new Orden();
            orden.comentario = "";
            orden.idOrden = ManejadorOrdenes.ObtenerId();
            FacesContext.getCurrentInstance().getExternalContext().redirect("pages/nuevaOrden.jsf");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public void crear() {
        try {
            orden.idOrden = ManejadorOrdenes.ObtenerId();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public void dashboard() throws IOException {

        FacesContext.getCurrentInstance().getExternalContext().redirect("../index.jsf");
        orden = new Orden();
    }

    public String fecha() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
        return formateador.format(ahora);
    }

    public void cobrar() {
        try {
            if (efectivo >= orden.total) {
                orden.activa = false;
                ManejadorOrdenes.Actualizar(orden);
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.jsf");
            } else {

            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

        }
    }

    public void cambio() {
        boolean esta = false;
        if (efect == null || efect.isEmpty()) {
            cambio = 0.00;
            btnCobrar = false;
        } else {
            if (efect.length() >= 1) {
                if (efect.substring(0, efect.length() - 1).contains(".")) {
                    if (efect.endsWith(".")) {
                        efect = efect.substring(0, efect.length() - 1);
                    }
                }
            }
            if (efect.startsWith(".")) {
                efect = "0" + efect;
                esta = true;
            }
            efectivo = Double.parseDouble(efect);
            if (efectivo >= orden.total) {
                btnCobrar = true;
                cambio = efectivo - orden.total;
            } else {
                btnCobrar = false;
                cambio = 0.0;
            }
            if (esta) {
                efect = efect.substring(1, efect.length());
                esta = false;
            }
        }
    }

    public List<Categoria> getCategorias() {
        try {
            return ManejadorCategorias.Obtener(false);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<Fake> getFake() {
        fake = new ArrayList<Fake>();
        fake2 = null;
        try {
            for (Producto p : getProductos()) {
                fake.add(new Fake(p, 0.0));
                if (orden.getDetalleOrdenList() != null) {
                    for (DetalleOrden dt : orden.getDetalleOrdenList()) {
                        if (p.idProducto == dt.producto.idProducto) {
                            fake.set(fake.size() - 1, new Fake(p, dt.getCantidad()));
                        }
                    }
                }
            }
            return fake;
        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<Fake> getFakeAgregar() {
        fakeAgregar = new ArrayList<Fake>();
        fake2 = null;
        try {
            for (Producto p : getProductos()) {
                fakeAgregar.add(new Fake(p, 0.0));
                if (detalle2 != null) {
                    for (DetalleOrden dt : getDetalle2()) {
                        if (p.idProducto == dt.producto.idProducto) {
                            fakeAgregar.set(fakeAgregar.size() - 1, new Fake(p, dt.getCantidad()));
                        }
                    }
                }
            }
            return fakeAgregar;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public List<Producto> getProductos() {
        try {
            productos = ManejadorProductos.ObtenerxCategoria(categoria.idCategoria);
            return productos;
        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }

    }

    public List<DetalleOrden> getDetalle() {
        detalle = new ArrayList<DetalleOrden>();
        try {
            for (DetalleOrden dt : orden.detalleOrdenList) {
                detalle.add(dt);
            }
            return detalle;
        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }

    }

    public List<DetalleOrden> getDetalle2() {
        return detalle2;
    }

    public String hora() {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("hh:mm a");
        return formateador.format(ahora);
    }

    public void editarOrdenAdd() {
        try {
            Ticket tk = new Ticket();
            ManejadorOrdenes.Actualizar(orden);
            tk.cocina(orden, detalle2);
            tk.bebida(orden, detalle2);
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.jsf");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void editarOrden() {
        try {
            Ticket tk = new Ticket();
            ManejadorOrdenes.Actualizar(orden);
            tk.cocina(orden, orden.detalleOrdenList);
            tk.bebida(orden, orden.detalleOrdenList);
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.jsf");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void eliminarOrden(){
        try {
            ManejadorOrdenes.Eliminar(orden);
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.jsf");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }
    
    public void agregarNueva() {
        try {
            Ticket tk = new Ticket();
            orden.setDetalleOrdenList(detalle);
            ManejadorOrdenes.Insertar(orden);
            tk.cocina(orden, orden.detalleOrdenList);
            tk.bebida(orden, orden.detalleOrdenList);
            FacesContext.getCurrentInstance().getExternalContext().redirect("../index.jsf");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void imprimir() {
        try {
            Ticket tk = new Ticket();
            tk.detalle(orden);
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.jsf");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes;
    }

    public Double getCambio() {
        return cambio;
    }

    public void setCambio(Double cambio) {
        this.cambio = cambio;
    }

    public int getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(int idOrden) {
        this.idOrden = idOrden;
    }

    public Double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(Double efectivo) {
        this.efectivo = efectivo;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Orden> getFiltro() {
        return filtro;
    }

    public void setFiltro(List<Orden> filtro) {
        this.filtro = filtro;
    }

    public boolean isBtnCobrar() {
        return btnCobrar;
    }

    public void setBtnCobrar(boolean btnCobrar) {
        this.btnCobrar = btnCobrar;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public void setDetalle(List<DetalleOrden> detalle) {
        this.detalle = detalle;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public DetalleOrden getDetail() {
        return detail;
    }

    public void setDetail(DetalleOrden detail) {
        this.detail = detail;
    }

    public void setFake(List<Fake> fake) {
        this.fake = fake;
    }

    public Fake getFakeel() {
        return fakeel;
    }

    public void setFakeel(Fake fakeel) {
        this.fakeel = fakeel;
    }

    public void setFakeAgregar(List<Fake> fakeAgregar) {
        this.fakeAgregar = fakeAgregar;
    }

    public void setDetalle2(List<DetalleOrden> detalle2) {
        this.detalle2 = detalle2;
    }

    public List<Fake> getFake2() {
        return fake2;
    }

    public void setFake2(List<Fake> fake2) {
        this.fake2 = fake2;
    }

    public String getEfect() {
        return efect;
    }

    public void setEfect(String efect) {
        this.efect = efect;
    }

}
