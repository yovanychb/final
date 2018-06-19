/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manejadores;

import java.io.Serializable;
import clases.Producto;
import controladores.Messages;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
public class ManejadorProductos implements Serializable {

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
     * Agrega el objeto “producto” a la base de datos.
     *
     * @param p Producto producto //
     * @throws ErrorAplication
     */
    public static void Insertar(Producto p) throws ErrorAplication {
        EntityManager em = null;
        if (p.idCategoria == null) {
            throw new ErrorAplication("ManejadorProductos.Insertar() $ El producto debe pertenecer a una Categoria.");
        } else if (p.nombre == null || p.nombre.isEmpty()) {
            throw new ErrorAplication("ManejadorProductos.Insertar() $ El producto debe poseer un nombre.");
        } else if (p.precio == null || p.precio <= 0.0) {
            throw new ErrorAplication("ManejadorProductos.Insertar() $ El producto debe poseer un precio válido.");
        } else if (p.area != 'C' && p.area != 'B') {
            throw new ErrorAplication("ManejadorProductos.Insertar() $ El producto debe pertenecer a un área valida. Areas validas [C=Cocina; B=Bebida]");
        } else {
            try {
                utx.begin();
                em = getEntityManager();
                p.idProducto = ObtenerId();
                em.persist(p);
                utx.commit();
                msg.addMessage(FacesMessage.SEVERITY_INFO, "Producto creado.", "El producto ha sido creado con éxito.");
            } catch (Exception ex) {
                throw new ErrorAplication("ManejadorProductos.Insertar() $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }

    /**
     * Modifica el objeto “producto” actualizando en la base de datos, no se
     * modifica el ID del producto solo sus otros campos.
     *
     * @param p Producto producto
     * @throws ErrorAplication
     */
    public static void Actualizar(Producto p) throws ErrorAplication {
        EntityManager em = null;
        if (p.idProducto == null || p.idProducto <= 0) {
            throw new ErrorAplication("ManejadorProductos.Actualizar() $ El producto que desea actiualizar no es valido.");
        } else {
            Producto pr = Obtener(p.idProducto);
            if (pr == null) {
                throw new ErrorAplication("ManejadorProductos.Actualizar() $ El producto que desea actiualizar no existe.");
            } else {
                if (p.idCategoria == null) {
                    throw new ErrorAplication("ManejadorProductos.Actualizar() $ El producto debe pertenecer a una Categoria.");
                } else if (p.nombre == null || p.nombre.isEmpty()) {
                    throw new ErrorAplication("ManejadorProductos.Actualizar() $ El producto debe poseer un nombre.");
                } else if (p.precio == null || p.precio <= 0.0) {
                    throw new ErrorAplication("ManejadorProductos.Actualizar() $ El producto debe poseer un precio válido.");
                } else if (p.area != 'C' && p.area != 'B') {
                    throw new ErrorAplication("ManejadorProductos.Actualizar() $ El producto debe pertenecer a un área valida. Areas validas [C=Cocina; B=Bebida]");
                } else {
                    try {
                        utx.begin();
                        em = getEntityManager();
                        em.merge(p);
                        utx.commit();
                        msg.addMessage(FacesMessage.SEVERITY_INFO, "Producto modificado.", "El producto ha sido modificado con éxito.");
                    } catch (Exception ex) {
                        throw new ErrorAplication("ManejadorProductos.Actualizar() $ " + ex.getMessage());
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
     * Elimina el objeto “producto” de la base de datos.
     *
     * @param p Producto producto
     * @throws ErrorAplication
     */
    public static void Eliminar(Producto p) throws ErrorAplication {
        EntityManager em = null;
        if (p == null) {
            throw new ErrorAplication("ManejadorProductos.Eliminar() $ La orden que desea eliminar no existe.");
        } else {
            if (p.idProducto <= 0) {
                throw new ErrorAplication("ManejadorProductos.Eliminar() $ El producto que desea eliminar no es valido.");
            } else {
                Producto pr = Obtener(p.idProducto);
                if (pr == null) {
                    throw new ErrorAplication("ManejadorProductos.Eliminar() $ El producto a eliminar no existe.");
                } else {
                    if (p.area.equals(pr.area) && p.idProducto.equals(pr.idProducto) && p.nombre.equals(pr.nombre) && p.precio.equals(pr.precio) && p.idCategoria.idCategoria.equals(pr.idCategoria.idCategoria)) {
                        try {
                            utx.begin();
                            em = getEntityManager();
                            em.remove(em.merge(p));
                            utx.commit();
                            msg.addMessage(FacesMessage.SEVERITY_INFO, "Producto eliminado.", "El producto ha sido eliminado con éxito.");
                        } catch (Exception ex) {
                            msg.addMessage(FacesMessage.SEVERITY_WARN, "Acción no permitida.", "El producto no se puede eliminar debido a que esta contenido en una orden.");
                            throw new ErrorAplication("ManejadorProductos.Eliminar() $ " + ex.getMessage());
                        } finally {
                            if (em != null) {
                                em.close();
                            }
                        }
                    } else {
                        throw new ErrorAplication("ManejadorProductos.Eliminar() $ El producto a eliminar no es valido.");
                    }
                }
            }
        }
    }

    /**
     * Toma la cadena pasada como parametro como criterio de búsqueda, para ir a
     * la base de datos y buscar todos los productos cuyo Id o nombre coincida
     * con el criterio de búsqueda.
     *
     * @param p String "criterio de busqueda"
     * @return List[Producto] ProductoList
     * @throws ErrorAplication
     */
    public static List<Producto> Buscar(String p) throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            try {
                int aux = Integer.parseInt(p);
                TypedQuery<Producto> q = em.createQuery("SELECT t FROM Producto t WHERE t.idProducto LIKE ?1 ", Producto.class);
                q.setParameter(1, aux);
                return q.getResultList();
            } catch (NumberFormatException e) {
                TypedQuery<Producto> q = em.createQuery("SELECT t FROM Producto t WHERE t.nombre LIKE CONCAT('%',?1,'%') ", Producto.class);
                q.setParameter(1, p);
                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorProductos.Buscar() $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Realiza una petición a la base de datos y devuelve una colección de
     * objetos productos que se corresponden con el Identificador de categoría
     * que se pasó como parámetro.
     *
     * @param idCategoria int idCategoria
     * @return List[Producto] ProductoList
     * @throws ErrorAplication
     */
    public static List<Producto> ObtenerxCategoria(int idCategoria) throws ErrorAplication {
        EntityManager em = null;
        if (idCategoria > 0) {
            try {
                em = getEntityManager();
                TypedQuery<Producto> q = em.createQuery("SELECT t FROM Producto t JOIN t.idCategoria c "
                        + "WHERE c.idCategoria = '" + idCategoria + "'", Producto.class);
                return q.getResultList();
            } catch (Exception ex) {
                throw new ErrorAplication("ManejadorProductos.ObtenerxCategoria() $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        } else {
            throw new ErrorAplication("ManejadorProductos.ObtenerxCategoria() $ El id ingresado no es valido.");
        }
    }

    /**
     * Realiza una petición a la base de datos y devuelve un objeto producto
     * cuyo idProducto coincide con el valor del parámetro
     *
     * @param id int idProducto
     * @return Producto producto
     * @throws ErrorAplication
     */
    public static Producto Obtener(Integer id) throws ErrorAplication {
        EntityManager em = null;
        if (id <= 0) {
            throw new ErrorAplication("ManejadorProductos.Obtener(id) $ El id ingresado no es valido.");
        } else {
            try {
                em = getEntityManager();
                return em.find(Producto.class, id);
            } catch (Exception ex) {
                throw new ErrorAplication("ManejadorProductos.Obtener(id) $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }

    /**
     * Obtiene el identificador de producto, va la base de datos a obtener el
     * ultimo ID de producto y le suma uno a dicho valor.
     *
     * @return int idProducto
     * @throws ErrorAplication
     */
    public static int ObtenerId() throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<Integer> q = em.createQuery("SELECT MAX(t.idProducto) FROM Producto t", Integer.class);
            TypedQuery<String> q2 = em.createQuery("SELECT MAX(t.idProducto) FROM Producto t", String.class);
            if (q2.getSingleResult() == null) {
                return 1;
            } else {
                return q.getSingleResult() + 1;
            }
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorProductos.ObtenerId $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
