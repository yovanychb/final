/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.SelectEvent;
import clases.Producto;

/**
 *
 * @author yovany
 */
@Named(value = "frmDetalle")
@ViewScoped
public class frmDetalle implements Serializable{

   private Producto producto;
    private List<Producto> productos = new ArrayList<Producto>();
    private List<Producto> lista = new ArrayList<Producto>();
    private List<Producto> detalle = new ArrayList<Producto>();
    private Integer cantidad;

    @PostConstruct
    public void init() {
        producto = new Producto();
        productos = new ArrayList<Producto>();
        cantidad = 0;
        lista = getProductos();
        detalle = detalle;
    }

    public List<Producto> getProductos() {
        productos = new ArrayList<Producto>();

        for (int i = 0; i < 20; i++) {

//            productos.add(new Producto(i, "Producto " + i, 1.05, 0));
        }
        return productos;
    }

    public void onRowSelect(SelectEvent se) {
        if (se.getObject() != null) {
//            try {
//                this.producto = (Producto) se.getObject();
//
//                producto.setCantidad(producto.getCantidad() + 1);
//                lista.set(producto.getCodigo(), producto);
//                detalle.add(producto);
//                System.out.println(detalle.get(0).getNombre());
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//            }
        }
    }

    public void redirect() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("productos.jsf");
    }
    
    public void undirect() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("nuevaOrden.jsf");
    }

    public void spinner() {

    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public List<Producto> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Producto> detalle) {
        this.detalle = detalle;
    }

    public List<Producto> getLista() {
        return lista;
    }

    public void setLista(List<Producto> lista) {
        this.lista = lista;
    }

    
}
