/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author yovany
 */
@Embeddable
class DetalleOrdenPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "idOrden", nullable = false)
    protected int idOrden;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idProducto", nullable = false)
    protected int idProducto;

    
}
