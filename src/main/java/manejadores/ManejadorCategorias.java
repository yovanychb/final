/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manejadores;

import clases.Categoria;
import controladores.Messages;
import java.io.Serializable;
import java.util.ArrayList;
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
public class ManejadorCategorias implements Serializable {
    
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
     * Agrega el objeto “categoria” a la base de datos.
     *
     * @param c Categoria categoria
     * @throws ErrorAplication
     */
    public static void Insertar(Categoria c) throws ErrorAplication {
        EntityManager em = null;
        if (c.nombre == null || c.nombre.isEmpty()) {
            throw new ErrorAplication("ManejadorCategorias.Insertar() $ La Categoria debe poseer un nombre. ");
        } else {
            try {
                utx.begin();
                em = getEntityManager();
                c.idCategoria = ObtenerId();
                em.persist(c);
                utx.commit();
                msg.addMessage(FacesMessage.SEVERITY_INFO, "Categoria creada.", "La categoria ha sido creada con éxito.");
            } catch (Exception ex) {
                
                throw new ErrorAplication("ManejadorCategorias.Insertar() $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }

    /**
     * Modifica el objeto “categoria” actualizando en la base de datos, no se
     * modificara el IDCategoria
     *
     * @param c Categoria categoria
     * @throws ErrorAplication
     */
    public static void Actualizar(Categoria c) throws ErrorAplication {
        EntityManager em = null;
        Categoria cat = null;
        if (c.idCategoria == null || c.idCategoria <= 0) {
            throw new ErrorAplication("ManejadorCategorias.Actualizar() $ La categoria que intenta actualizar no es valida.");
        } else {
            try {
                cat = Obtener(c.idCategoria);
            } catch (Exception e) {
                throw new ErrorAplication("ManejadorCategorias.Actualizar() $ " + e.getMessage());
            }
            if (cat == null) {
                throw new ErrorAplication("ManejadorCategorias.Actualizar() $ La categoria que intenta actualizar no existe.");
            } else if (c.nombre.isEmpty() || c.nombre == null) {
                throw new ErrorAplication("ManejadorCategorias.Actualizar() $ La Categoria debe poseer un nombre. ");
            } else {
                try {
                    utx.begin();
                    em = getEntityManager();
                    em.merge(c);
                    utx.commit();
                    msg.addMessage(FacesMessage.SEVERITY_INFO, "Categoria modificada.", "La categoria ha sido modificada con éxito.");
                } catch (Exception ex) {
                    throw new ErrorAplication("ManejadorCategorias.Actualizar() $ " + ex.getMessage());
                } finally {
                    if (em != null) {
                        em.close();
                    }
                }
            }
        }
    }

    /**
     * Elimina el objeto “categoria” de la base de datos.
     *
     * @param c Categoria categoria
     * @throws ErrorAplication
     */
    public static void Eliminar(Categoria c) throws ErrorAplication {
        EntityManager em = null;
        Categoria cat = null;
        if (c == null) {
            throw new ErrorAplication("ManejadorCategorias.Eliminar() $ La categoria que intenta eliminar no existe.");
        } else {
            if (c.idCategoria <= 0) {
                throw new ErrorAplication("ManejadorCategorias.Eliminar() $ La categoria que intenta eliminar no es valida.");
            } else {
                try {
                    cat = Obtener(c.idCategoria);
                } catch (Exception e) {
                    throw new ErrorAplication("ManejadorCategorias.Eliminar() $ " + e.getMessage());
                }
                if (cat == null) {
                    throw new ErrorAplication("ManejadorCategorias.Eliminar() $ La categoria que intenta eliminar no existe.");
                } else if (c.productoList!= null) {
                    throw new ErrorAplication("ManejadorCategorias.Eliminar() $ La categoria: " + c.nombre + " no se puede eliminar debido a que contine productos");
                } else {
                    try {
                        utx.begin();
                        em = getEntityManager();
                        em.remove(em.merge(c));
                        utx.commit();
                        msg.addMessage(FacesMessage.SEVERITY_INFO, "Categoria eliminada.", "La categoria ha sido eliminada con éxito.");
                    } catch (Exception ex) {
                        msg.addMessage(FacesMessage.SEVERITY_WARN, "Acción no permitida.", "La categoria no se puede eliminar debido a que posee productos.");
                        throw new ErrorAplication("ManejadorCategorias.Eliminar() $ " + ex.getMessage());
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
     * Realiza una petición a la base de datos y devuelve una colección
     * dcategorías, si el valor del parametro es TRUE cargara todos los
     * productos que están en esa categoría, si el valor del parametro es FALSE
     * la propiedad “productos” de cada categoría será NULL
     *
     * @param all boolean cargarProductos
     * @return List[Categoria] CategoriaList
     * @throws ErrorAplication
     */
    public static List<Categoria> Obtener(boolean all) throws ErrorAplication {
        EntityManager em = null;
        List<Categoria> lst = new ArrayList<Categoria>();
        try {
            em = getEntityManager();
            TypedQuery<Categoria> q = em.createQuery("SELECT t FROM Categoria t", Categoria.class
            );
            if (all == true) {
                return q.getResultList();
            } else {
                for (Categoria c : q.getResultList()) {
                    c.productoList = null;
                    lst.add(c);
                }
                return lst;
            }
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorCategorias.Obtener() $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Obtiene el identificador de categoria, va la base de datos a obtener el
     * ultimo ID de categoria y le suma uno a dicho valor.
     *
     * @return int idCategoria
     * @throws ErrorAplication
     */
    public static int ObtenerId() throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<Integer> q = em.createQuery("SELECT MAX(t.idCategoria) FROM Categoria t", Integer.class);
            TypedQuery<String> q2 = em.createQuery("SELECT MAX(t.idCategoria) FROM Categoria t", String.class);
            if (q2.getSingleResult() == null) {
                return 1;
            } else {
                return q.getSingleResult() + 1;
            }
        } catch (Exception ex) {
            throw new ErrorAplication("ManejadorCategorias.ObtenerId $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Toma el idCategoria y busca en la base de datos una tupla que coincida
     * con dicho id.
     *
     * @param id int idCategoria
     * @return Categoria categoria
     */
    public static Categoria Obtener(Integer id) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            return em.find(Categoria.class, id);
        } catch (Exception ex) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
