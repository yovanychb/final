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
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import clases.Orden;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import manejadores.ManejadorOrdenes;

/**
 *
 * @author yovany
 */
@Named(value = "frmReportes")
@ViewScoped
public class frmReportes implements Serializable {

    private String tipo;
    private Orden orden;
    private List<Orden> ordenes;
    private List<String> meses;
    private boolean diario;
    private boolean mensual;
    private boolean anual;
    private boolean rango;
    private Date dia;
    private String mes;
    private String anio;
    private Date inicio;
    private Date inicioMes;
    private Date fin;
    private Date finMes;
    private Double total;

    @PostConstruct
    public void init() {
        orden = new Orden();
        ordenes = new ArrayList<Orden>();
        negar();
    }

    public void negar() {
        diario = false;
        mensual = false;
        anual = false;
        rango = false;
        ordenes = Collections.EMPTY_LIST;
        total = 0.0;
    }
    
    public void reporteRango(){
        try {
            total = 0.0;
            fin.setHours(23);
            fin.setMinutes(59);
            fin.setSeconds(59);
            System.out.println("Inicio: "+ inicio);
            System.out.println("Fin: "+ fin);
            ordenes = ManejadorOrdenes.ObtenerVentas(inicio,fin);
            for (Orden o : ordenes) {
                total += o.getTotal();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void reporteAnual() {
        try {
            total = 0.0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fecha1 = anio + "-01-01 00:00:00";
            String fecha2 = anio + "-12-31 23:59:59";
            ordenes = ManejadorOrdenes.ObtenerVentas(format.parse(fecha1), format.parse(fecha2));
            for (Orden o : ordenes) {
                total += o.getTotal();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void reporteMensual() {
        try {
            total = 0.0;
            anio = "2018";
            ordenes = ManejadorOrdenes.ObtenerVentas(getInicioMes(), getFinMes());
            for (Orden o : ordenes) {
                total += o.getTotal();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void reporteDiario() {
        try {
            total = 0.0;
            ordenes = ManejadorOrdenes.ObtenerVentas(dia);
            for (Orden o : ordenes) {
                total += o.getTotal();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void elegirTipo() {
        if (null != tipo) {
            switch (tipo) {
                case "Diario":
                    negar();
                    diario = true;
                    break;
                case "Mensual":
                    negar();
                    mensual = true;
                    break;
                case "Anual":
                    negar();
                    anual = true;
                    break;
                case "Rango":
                    negar();
                    inicio = new Date();
                    fin = new Date();
                    rango = true;
                    break;
                default:
                    negar();
                    break;
            }
        }
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes;
    }

    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void undirect() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("../inicio.html");
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public boolean isDiario() {
        return diario;
    }

    public void setDiario(boolean diario) {
        this.diario = diario;
    }

    public boolean isMensual() {
        return mensual;
    }

    public void setMensual(boolean mensual) {
        this.mensual = mensual;
    }

    public boolean isAnual() {
        return anual;
    }

    public void setAnual(boolean anual) {
        this.anual = anual;
    }

    public boolean isRango() {
        return rango;
    }

    public void setRango(boolean rango) {
        this.rango = rango;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getInicioMes() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = anio + "-" + mes + "-01";
            return format.parse(fecha);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return inicioMes;
        }
    }

    public void setInicioMes(Date inicioMes) {
        this.inicioMes = inicioMes;
    }

    public Date getFinMes() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(getInicioMes());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            return cal.getTime();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return finMes;
        }
    }

    public void setFinMes(Date finMes) {
        this.finMes = finMes;
    }

    public List<String> getMeses() {
        meses = new ArrayList<>();
        meses.add("Enero");
        return meses;
    }

    public void setMeses(List<String> meses) {
        this.meses = meses;
    }

}
