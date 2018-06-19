/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manejadores;

import clases.Parametro;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class ManejadorParametros implements Serializable {

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
     * Actualiza el valor del parámetro en la base de datos, no se puede
     * actualizar ni el ID, ni el nombre, solo se puede modificar el campo valor
     *
     * @param p Parametro parametro
     * @throws ErrorAplication
     */
    public static void Actualizar(Parametro p) throws ErrorAplication {
        EntityManager em = null;
        Parametro pr = null;
        if (p.idParametro <= 0) {
            throw new ErrorAplication("ManejadorParametros.Actuaalizar() $ El parametro que se intenta actualizar no es valido.");
        } else {
            try {
                pr = Obtener(p.idParametro);
            } catch (Exception e) {
                throw new ErrorAplication("ManejadorParametros.Actualizar() $ " + e.getMessage());
            }
            if (pr == null) {
                throw new ErrorAplication("ManejadorParametros.Actuaalizar() $ El parametro que se intenta actualizar no es valido.");
            } else if (p.valor == null) {
                throw new ErrorAplication("ManejadorParametros.Actualizar() $ El valor del parametro no puede ser nulo.");
            } else {
                try {
                    utx.begin();
                    em = getEntityManager();
                    p.nombre = pr.nombre;
                    em.merge(p);
                    utx.commit();
                } catch (Exception ex) {
                    throw new ErrorAplication("ManejadorParametros.Actualizar() $ " + ex.getMessage());
                } finally {
                    if (em != null) {
                        em.close();
                    }
                }
            }
        }
    }

    /**
     * Va a la base de datos y obtiene todos los parametros que están en dicha
     * tabla.
     *
     * @return List[Parametro] ParametroList
     * @throws ErrorAplication
     */
    public static List<Parametro> Obtener() throws ErrorAplication {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<Parametro> q = em.createQuery("SELECT t FROM Parametro t", Parametro.class);
            return q.getResultList();
        } catch (Exception ex) {
            throw new ErrorAplication("ControladorParametros.Obtener() $ " + ex.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Toma el idParametro y busca en la base de datos una tupla que coincida
     * con dicho id.
     *
     * @param idParametro int idParametro
     * @return Parametro parametro
     * @throws ErrorAplication
     */
    public static Parametro Obtener(Integer idParametro) throws ErrorAplication {
        EntityManager em = null;
        if (idParametro <= 0) {
            throw new ErrorAplication("ManejadorParametros.Obtener(idParametro) $ El id del ingresado no es valido.");
        } else {
            try {
                em = getEntityManager();
                return em.find(Parametro.class, idParametro);
            } catch (Exception ex) {
                throw new ErrorAplication("ManejadorParametros.Obtener(idParametro) $ " + ex.getMessage());
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
    }
}
