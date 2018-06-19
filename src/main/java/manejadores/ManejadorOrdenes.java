/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manejadores;

import clases.Orden;
import controladores.Messages;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

/**
 *
 * @author yovany
 */
public class ManejadorOrdenes implements Serializable {

    private static Messages msg = new Messages();
    
    private static UserTransaction utx = getUtx();

    private static UserTransaction getUtx() {
        utx = null;
        try {
            return (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        } catch (NamingException ex) {
            Logger.getLogger(ManejadorOrdenes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return utx;
    }

    private static EntityManager getEntityManager() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ResbarLibPU");
        return emf.createEntityManager();
    }

    /**
     * Va a la base de datos y filtra todas las ordenes cuyo campo estado=true,
     * y devuelve un colección de objetos Orden
     *
     * @return List[Orden] OrdenList
     * @throws ErrorAplication
     */
    public static List<Orden> ObtenerActivas() throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<Orden> q = em.createQuery("SELECT t FROM Orden t WHERE t.activa = true", Orden.class);
            return q.getResultList();
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorProductos.ObtenerActivas() $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Inserta el objeto orden en la base de datos, inserta una tupla en la
     * tabla Orden y una o varias tuplas en la tabla Detalle Orden, además
     * verifica que la orden tenga al menos uno de los campos con valor: mesero,
     * mesa o cliente, no permite insertar ordenes con un total de cero o
     * negativo, o que NO posean ningún producto en su detalle
     *
     * @param o Orden orden
     * @throws ErrorAplication
     */
    public static void Insertar(Orden o) throws ErrorAplication {
        EntityManager em = null;
        if (o.mesa == null && o.mesero == null && o.cliente == null) {
            msg.addMessage(FacesMessage.SEVERITY_ERROR, "Campos invalidos.", "Los campos: Mesa, Mesero y Cliente son requeridos.");
            throw new ErrorAplication("ManejadorOrden.Insertar() $ La orden debe contener los siguientes campos: [Mesa, Mesero y Cliente]");
        } else if (o.detalleOrdenList == null || o.detalleOrdenList.isEmpty()) {
            msg.addMessage(FacesMessage.SEVERITY_ERROR, "Orden vacia.", "La orden debe contener al menos un producto.");
            throw new ErrorAplication("ManejadorOrden.Insertar() $ La orden debe contener al menos un producto");
        } else if (o.total <= 0.0) {
            throw new ErrorAplication("ManejadorOrden.Insertar() $ El total debe ser mayor que $0.0");
        } else {
            try {
                utx.begin();
                em = getEntityManager();
                o.idOrden = ObtenerId();
                java.util.Date fecha = new Date();
                o.fecha = fecha;
                o.activa = true;
                em.persist(o);
                utx.commit();
                msg.addMessage(FacesMessage.SEVERITY_INFO, "Orden creada.", "La orden ha sido creada con éxito.");
            } catch (Exception ex) {
                throw new ErrorAplication("ManejadorOrden.Insertar() $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }

    /**
     * Toma un objeto orden que ya existe en la tabla Orden de la base de datos,
     * luego verifica que el objeto orden tenga productos, que su total sea
     * mayor que cero, en la base de datos hace update de la tabla orden
     *
     * @param o Orden orden
     * @throws ErrorAplication
     */
    public static void Actualizar(Orden o) throws ErrorAplication {
        EntityManager em = null;
        Orden or = null;
        if (o.idOrden == null || o.idOrden <= 0) {
            throw new ErrorAplication("ManejadorOrdenes.Actualizar() $ La orden que desea actualizar no es valida.");
        } else {
            try {
                or = Obtener(o.idOrden);
            } catch (Exception e) {
                throw new ErrorAplication("ManejadorOrdenes.Actualizar() $ " + e.getMessage());
            }
            if (or == null) {
                throw new ErrorAplication("ManejadorOrdenes.Actualizar() $ La orden que desea actualizar no existe.");
            } else {
                if (o.detalleOrdenList == null) {
                    throw new ErrorAplication("ManejadorOrden.Actualizar() $ La orden debe contener al menos un producto");
                } else if (o.mesero == null || o.mesa == null || o.cliente == null) {
                    throw new ErrorAplication("ManejadorOrden.Actualizar() $ La orden debe contener por lo menos uno de los siguientes campos: [Mesa, Mesero o Cliente]");
                } else if (o.total <= 0.0) {
                    throw new ErrorAplication("ManejadorOrden.Actualizar() $ El total debe ser mayor que $0.0");
                } else {
                    try {

                        utx.begin();
                        em = getEntityManager();
                        em.merge(o);
                        utx.commit();
                        msg.addMessage(FacesMessage.SEVERITY_INFO, "Orden modificada.", "La orden ha sido modificada con éxito.");
                    } catch (Exception ex) {
                        throw new ErrorAplication("ManejadorOrden.Actualizar() $ " + ex.getMessage());
                    } finally {
                        if (em != null) {
                            em.close();
                        }
                    }
                }
            }
        }
    }

    /**
     * Elimina dicha orden de la base de datos, eliminando sus detalles también.
     *
     * @param o Orden orden
     * @throws ErrorAplication
     */
    public static void Eliminar(Orden o) throws ErrorAplication {
        EntityManager em = null;
        Orden or = null;
        if (o == null) {
            throw new ErrorAplication("ManejadorOrdenes.Eliminar() $ La orden que desea eliminar no existe.");
        } else {
            if (o.idOrden <= 0) {
                throw new ErrorAplication("ManejadorOrdenes.Eliminar() $ La orden que desea eliminar no es valida.");
            } else {
                try {
                    or = Obtener(o.idOrden);
                } catch (Exception e) {
                    throw new ErrorAplication("ManejadorOrdenes.Eliminar() $" + e.getMessage());
                }
                if (or == null) {
                    throw new ErrorAplication("ManejadorOrdenes.Eliminar() $ La orden que desea eliminar no existe.");
                } else {
                    try {
                        utx.begin();
                        em = getEntityManager();
                        em.remove(em.merge(o));
                        utx.commit();
                        msg.addMessage(FacesMessage.SEVERITY_INFO, "Categoria eliminada.", "La categoria ha sido eliminada con éxito.");
                    } catch (Exception ex) {
                        throw new ErrorAplication("ManejadorOrden.Eliminar() $ " + ex.getMessage());
                    } finally {
                        if (em != null) {
                            em.close();
                        }
                    }
                }
            }
        }
    }

    /**
     * Va a la base de datos y obtiene el ultimo Id de orden y le suma 1
     *
     * @return int idOrden
     * @throws ErrorAplication
     */
    public static int ObtenerId() throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<Integer> q = em.createQuery("SELECT MAX(t.idOrden) FROM Orden t", Integer.class);
            TypedQuery<String> q2 = em.createQuery("SELECT MAX(t.idOrden) FROM Orden t", String.class);
            if (q2.getSingleResult() == null) {
                return 1;
            } else {
                return q.getSingleResult() + 1;
            }
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorOrdenes.ObtenerId $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Toma el string que tiene el criterio de búsqueda y va a la base de datos
     * a buscar todas aquellas ordenes que cumplan con dicho criterio ya sea en
     * el mesero, mesa, cliente o comentario. Devuelve una colección de órdenes
     * que cumplen con dicho criterio.
     *
     * @param p String "criterio de busqueda"
     * @return List[Orden] OrdenList
     * @throws ErrorAplication
     */
    public static List<Orden> BuscarActivas(String p) throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<Orden> q = em.createQuery("SELECT t FROM Orden t WHERE (t.activa = true "
                    + "AND (t.mesero LIKE CONCAT('%',?1,'%') OR t.mesa LIKE CONCAT('%',?1,'%') OR t.cliente LIKE CONCAT('%',?1,'%') "
                    + "OR t.comentario LIKE CONCAT('%',?1,'%'))) ", Orden.class);
            q.setParameter(1, p);
            return q.getResultList();
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorOrden.BuscarActivas() $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene todas las ventas realizadas para una fecha determinada, se filtra
     * solo por día, mes y año, las ordenes devueltas tienen que tener el campo
     * estado en false, pues son ordenes que ya fueron cobradas.
     *
     * @param fecha Date fecha
     * @return List[Orden] OrdenList
     * @throws ErrorAplication
     */
    public static List<Orden> ObtenerVentas(Date fecha) throws ErrorAplication {
        EntityManager em = null;
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        try {
            em = getEntityManager();
            TypedQuery<Orden> q = em.createQuery("SELECT t FROM Orden t WHERE t.activa = false "
                    + "AND t.fecha LIKE '" + form.format(fecha) + "%'", Orden.class);
            return q.getResultList();
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorOrdenes.ObtenerVentas(:Date) $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene las ventas filtrando por un rango de fechas.
     *
     * @param inicio Date fechaInicio
     * @param fin Date fechaFin
     * @return List[Orden] OrdenList
     * @throws ErrorAplication
     */
    public static List<Orden> ObtenerVentas(Date inicio, Date fin) throws ErrorAplication {
        EntityManager em = null;
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Orden> list = new ArrayList<Orden>();
        try {
            em = getEntityManager();
            TypedQuery<Orden> q = em.createQuery("SELECT t FROM Orden t WHERE t.activa = false "
                    + "AND (t.fecha BETWEEN '" + form.format(inicio) + "' AND '" + form.format(fin) + "')", Orden.class);
            for (Orden o : q.getResultList()) {
                o.detalleOrdenList = null;
                list.add(o);
            }
            return list;
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorOrdenes.ObtenerVentas(:Date, :Date) $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Recibe un entero que indica el ID de la orden y luego devuelve el objeto
     * orden completo que corresponde
     *
     * @param idOrden
     * @return
     * @throws ErrorAplication
     */
    public static Orden Obtener(Integer idOrden) throws ErrorAplication {
        EntityManager em = null;
        if (idOrden <= 0) {
            throw new ErrorAplication("ManejadorOrdenes.Obtener() $ El id ingresado no es valido.");
        } else {
            try {
                em = getEntityManager();
                return em.find(Orden.class, idOrden);
            } catch (Exception ex) {
                throw new ErrorAplication("ManejadorOrdenes.Obtener() $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }
}
