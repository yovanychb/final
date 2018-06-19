/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import clases.Categoria;
import clases.Producto;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import manejadores.ManejadorCategorias;
import manejadores.ManejadorProductos;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author yovany
 */
@Named(value = "frmproductos")
@ViewScoped
public class frmProductos implements Serializable {

    private List<Categoria> categorias;
    private List<Producto> productos;
    private List<Producto> filtroProd;
    private Categoria categoria = new Categoria();
    private Producto producto;
    private String nombre = "";
    private Double precio;
    private Character area;
    private int idCategoria;
    private Messages msg;

    @PostConstruct
    public void init() {
        producto = new Producto();
        categoria = new Categoria();
        msg = new Messages();
    }

    public void nuevaCat() {
        categoria = new Categoria();
        nombre = "";
    }

    public void nuevoProd() {
        producto = new Producto();
        nombre = "";
        precio = null;
        area = null;
    }

    public void editarCategoria() {
        try {
            if (nombre == null || nombre.isEmpty()) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Nombre invalido.", "La categoria debe poseer un nombre valido.");
            } else {
                categoria.nombre = nombre;
                ManejadorCategorias.Actualizar(categoria);
                nuevaCat();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void editarProducto() {
        try {
            producto.idCategoria = ManejadorCategorias.Obtener(idCategoria);
            if (nombre == null || nombre.isEmpty()) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Nombre invalido.", "El producto debe poseer  un nombre.");
            } else if (precio == null || precio <= 0.0) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Precio invalido.", "El producto debe poseer  un precio mayor que 0.00.");
            } else if (area == null) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Area invalida.", "El producto debe pertenecer a un area.");
            } else {
                producto.nombre = nombre;
                producto.precio = precio;
                producto.area = area;
                ManejadorProductos.Actualizar(producto);
                nuevoProd();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void onRowSelectCategoria(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                categoria = (Categoria) se.getObject();
                nombre = categoria.nombre;
                getProductos();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void eliminarCategoria() {
        try {
            ManejadorCategorias.Eliminar(categoria);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void eliminarProducto() {
        try {
            ManejadorProductos.Eliminar(producto);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void crearCategoria() {
        try {
            if (nombre == null || nombre.isEmpty()) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Nombre invalido.", "La categoria debe poseer un nombre valido.");
            } else {
                categoria = new Categoria();
                categoria.setNombre(nombre);
                ManejadorCategorias.Insertar(categoria);
                nuevaCat();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void crearProducto() {
        try {
            producto = new Producto();
            producto.idCategoria = ManejadorCategorias.Obtener(idCategoria);
            if (nombre == null || nombre.isEmpty()) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Nombre invalido.", "El producto debe poseer  un nombre.");
            } else if (precio == null || precio <= 0.0) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Precio invalido.", "El producto debe poseer  un precio mayor que 0.00.");
            } else if (area == null) {
                msg.addMessage(FacesMessage.SEVERITY_ERROR, "Area invalida.", "El producto debe pertenecer a un area.");
            } else {
                producto.nombre = nombre;
                producto.precio = precio;
                producto.area = area;
                ManejadorProductos.Insertar(producto);
                nuevoProd();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void onRowSelect(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                producto = (Producto) se.getObject();
                nombre = producto.nombre;
                area = producto.area;
                idCategoria = producto.idCategoria.idCategoria;
                precio = producto.precio;
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
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

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public List<Producto> getProductos() {
        try {
            productos = ManejadorProductos.ObtenerxCategoria(categoria.idCategoria);
            filtroProd = null;
            return productos;
        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public void home() throws IOException {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("../inicio.html");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Producto> getFiltroProd() {
        return filtroProd;
    }

    public void setFiltroProd(List<Producto> filtroProd) {
        this.filtroProd = filtroProd;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Character getArea() {
        return area;
    }

    public void setArea(Character area) {
        this.area = area;
    }

    public Messages getMsg() {
        return msg;
    }

    public void setMsg(Messages msg) {
        this.msg = msg;
    }
}
