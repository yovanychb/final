/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import clases.Categoria;
import clases.Producto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import manejadores.ManejadorCategorias;
import manejadores.ManejadorProductos;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author yovany
 */
@Named(value = "frmCategorias")
@ViewScoped
public class frmCategorias implements Serializable{

    private Categoria categoria;
    private List<Categoria> categorias;
    private int idCategoria;
    private List<Producto> productos;
    private Producto producto;
    
    @PostConstruct
    public void init(){
        productos = new ArrayList<Producto>();
    }

    public void onRowSelect(SelectEvent se) {
        if (se.getObject() != null) {
            try {
                categoria = (Categoria) se.getObject();
                idCategoria = categoria.idCategoria;
                getProductos();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Categoria> getCategorias() {
        try {
            return ManejadorCategorias.Obtener(true);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public List<Producto> getProductos() {
        try {
            productos = ManejadorProductos.ObtenerxCategoria(idCategoria);
            for(Producto p : productos){
                System.out.println(p.nombre);
            }
            return productos;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }

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
}
