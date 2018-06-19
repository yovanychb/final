/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import manejadores.ErrorAplication;

/**
 *
 * @author yovany
 */
@Entity
@Table(name = "Orden", catalog = "resbar", schema = "")
@XmlRootElement
public class Orden implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "idOrden", nullable = false)
    public Integer idOrden;
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @NotNull
    @Column(name = "mesero", nullable = false, length = 100)
    public String mesero;
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @NotNull
    @Column(name = "mesa", nullable = false, length = 100)
    public String mesa;
    @Basic(optional = false)
    @Size(min = 1, max = 100)
    @NotNull
    @Column(name = "cliente", nullable = false, length = 100)
    public String cliente;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date fecha;
    @Size(max = 350)
    @Column(name = "comentario", length = 350)
    public String comentario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total", nullable = false, precision = 10, scale = 4)
    public Double total;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activa", nullable = false)
    public boolean activa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orden")
    public List<DetalleOrden> detalleOrdenList;

    public Orden() {
    }

    public Orden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public Orden(Integer idOrden, String mesero, String mesa, String cliente, Date fecha, Double total, boolean activa) {
        this.idOrden = idOrden;
        this.mesero = mesero;
        this.mesa = mesa;
        this.cliente = cliente;
        this.fecha = fecha;
        this.total = total;
        this.activa = activa;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public String getMesero() {
        return mesero;
    }

    public void setMesero(String mesero) {
        this.mesero = mesero;
    }

    public String getMesa() {
        return mesa;
    }

    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean getActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @XmlTransient
    public List<DetalleOrden> getDetalleOrdenList() {
        return detalleOrdenList;
    }

    public void setDetalleOrdenList(List<DetalleOrden> detalleOrdenList) {
        this.detalleOrdenList = detalleOrdenList;
    }
    
    public void CalcularTotal() throws ErrorAplication {
        Double aux = 0.0;
        total = 0.0;
        if (idOrden == null || idOrden <= 0) {
            throw new ErrorAplication("Orden.CalcularTotal() $ La orden no es valida.  ");
        } else {
            if (detalleOrdenList == null || detalleOrdenList.isEmpty()) {
                throw new ErrorAplication("Orden.CalcularTotal() $ No existen productos en la orden ");
            } else {
                try {
                    for (DetalleOrden det : detalleOrdenList) {
                        Double sub = det.cantidad * det.producto.precio;
                        aux += sub;
                    }
                    total = aux;
                } catch (Exception ex) {
                    throw new ErrorAplication("Orden.CalcularTotal() $ " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Permite agregar más productos a la orden, toma el objeto producto y la
     * cantidad para construir un objeto DetalleOrden, y luego ver si ese
     * producto ya está agregado a la orden, si ya está agregado a la
     * ordenentonces solo se suma la cantidad, sino se agrega a la colección
     * detalleOrdenList de la orden y se invoca calcular total.
     *
     * @param producto Producto producto
     * @param cantidad double cantidad
     * @throws ErrorAplication
     */
    public void AgregarProducto(Producto producto, Double cantidad) throws ErrorAplication {
        List<DetalleOrden> list = new ArrayList<>();
        boolean existe = false;

        if (idOrden == null) {
            throw new ErrorAplication("Orden.AgregarProducto() $ La orden no es valida. ");
        } else {
            if (producto != null) {
                if (cantidad > 0.0) {
                    DetalleOrden dto = new DetalleOrden();
                    DetalleOrdenPK id = new DetalleOrdenPK();
                    try {
                        id.idOrden = idOrden;
                        id.idProducto = producto.idProducto;
                        dto.detalleOrdenPK = id;
                        dto.cantidad = cantidad;
                        dto.producto = producto;
                        if (detalleOrdenList != null) {
                            for (DetalleOrden det : detalleOrdenList) {
                                if (det.producto.idProducto.equals(producto.idProducto)) {
                                    det.cantidad = det.cantidad + cantidad;
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe) {
                                detalleOrdenList.add(dto);
                            }
                        } else {
                            detalleOrdenList = list;
                            detalleOrdenList.add(dto);
                        }
                        CalcularTotal();
                    } catch (Exception ex) {
                        throw new ErrorAplication("Orden.AgregarProducto() $ " + ex.getMessage());
                    }
                } else {
                    throw new ErrorAplication("Orden.AgregarProducto() $ La cantidad a agregar debe ser mayor a 0.0");
                }
            } else {
                throw new ErrorAplication("Orden.AgregarProducto() $ El producto no es valido");
            }
        }
    }

    /**
     * Permite eliminar productos de una orden y actualiza el total de la orden.
     *
     * @param producto Producto producto
     * @param cantidad Double cantidad
     * @throws ErrorAplication
     */
    public void EliminarProducto(Producto producto, Double cantidad) throws ErrorAplication {
        if (idOrden == null) {
            throw new ErrorAplication("Orden.EliminarProducto() $ La orden no es valida. ");
        } else {
            if (producto != null) {
                if (cantidad > 0.0) {
                    DetalleOrden dto = new DetalleOrden();
                    DetalleOrdenPK id = new DetalleOrdenPK();
                    try {
                        id.idOrden = idOrden;
                        id.idProducto = producto.idProducto;
                        dto.producto = producto;
                        if (detalleOrdenList != null) {
                            for (DetalleOrden det : detalleOrdenList) {
                                if (det.producto.idProducto.equals(producto.idProducto)) {
                                    dto = det;
                                    if (det.cantidad > cantidad) {
                                        det.cantidad = det.cantidad - cantidad;
                                    } else if (det.cantidad <= cantidad) {
                                        dto.cantidad = det.cantidad;
                                        detalleOrdenList.remove(dto);
                                    }
                                    break;
                                } else {
                                    //throw new ErrorAplication("Orden.EliminarProducto() $ No se encontro el producto.");
                                }
                            }
                            CalcularTotal();
                        }
                    } catch (Exception ex) {
                        throw new ErrorAplication("Orden.EliminarProducto() $ " + ex.getMessage());
                    }
                } else {
                    throw new ErrorAplication("Orden.EliminarProducto() $ La cantidad a eliminar debe ser mayor que 0.0.");
                }
            } else {
                throw new ErrorAplication("Orden.EliminarProducto() $ El producto no es valido.");
            }
        }
    }
    
}
