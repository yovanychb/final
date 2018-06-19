package printer;

import clases.DetalleOrden;
import clases.Orden;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author yovany
 */
public class Ticket {

    SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat hora = new SimpleDateFormat("hh:mm:ss a");

    public void cocina(Orden o, List<DetalleOrden> dt) {
        PrinterOptions p = new PrinterOptions();
        System.out.println("Cocina");
        int cont = 0;
        p.resetAll();
        p.initialize();
        p.feedBack((byte) 2);
        p.chooseFont(1);
        p.alignCenter();
        p.setText("COCINA");
        p.newLine();
        p.newLine();
        p.alignLeft();
        p.setText("Cliente: " + o.cliente + "\t\tFecha: " + fecha.format(o.fecha));
        p.newLine();
        p.setText("No. Ticket: 001");
        p.newLine();
        p.setText("Mesa: " + o.mesa + "\t\t\tHora: " + hora.format(o.fecha));
        p.newLine();
        p.addLineSeperator();
        p.newLine();
        p.setText("Concepto \t\t\t   Cantidad");
        p.newLine();
        p.addLineSeperator();
        p.alignRight();
        for (DetalleOrden d : dt) {
            if (d.producto.area == 'C') {
                cont++;
                p.newLine();
                char linea[] = new char[48];
                for (int i = 0; i < linea.length; i++) {
                    linea[i] = ' ';
                }
                char nombre[] = d.producto.nombre.toCharArray();
                char cant[] = d.cantidad.toString().toCharArray();
                for (int i = 0; i < nombre.length; i++) {
                    linea[i] = nombre[i];
                }
                if (d.cantidad > 9.99) {
                    for (int i = 0; i < cant.length; i++) {
                        linea[i + 39] = cant[i];
                    }
                } else {
                     for (int i = 0; i < cant.length; i++) {
                        linea[i + 40] = cant[i];
                    }
                }
                p.setText(String.valueOf(linea));
                System.out.println(String.valueOf(linea));
            }
        }
        p.newLine();
        p.alignLeft();
        p.addLineSeperator();
        p.newLine();
        p.newLine();
        p.alignLeft();
        p.setText("Comentario:");
        p.newLine();
        p.setText(o.comentario);
        p.feed((byte) 3);
        p.finit();
        if (cont >= 1) {
            PrinterOptions.feedPrinter(p.finalCommandSet().getBytes());
        }
    }

    public void bebida(Orden o, List<DetalleOrden> dt) {
        PrinterOptions p = new PrinterOptions();
        System.out.println("Bebidas");
        int cont = 0;
        p.resetAll();
        p.initialize();
        p.feedBack((byte) 2);
        p.chooseFont(1);
        p.alignCenter();
        p.setText("BEBIDA");
        p.newLine();
        p.newLine();
        p.alignLeft();
        p.setText("Cliente: " + o.cliente + "\t\tFecha: " + fecha.format(fecha()));
        p.newLine();
        p.setText("No. Ticket: 001");
        p.newLine();
        p.setText("Mesa: " + o.mesa + "\t\t\tHora: " + hora.format(fecha()));
        p.newLine();
        p.addLineSeperator();
        p.newLine();
        p.setText("Concepto \t\t\t   Cantidad");
        p.newLine();
        p.addLineSeperator();
        p.alignRight();
        for (DetalleOrden d : dt) {
            if (d.producto.area == 'B') {
                cont++;
                p.newLine();
                char linea[] = new char[48];
                for (int i = 0; i < linea.length; i++) {
                    linea[i] = ' ';
                }
                char nombre[] = d.producto.nombre.toCharArray();
                char cant[] = d.cantidad.toString().toCharArray();
                for (int i = 0; i < nombre.length; i++) {
                    linea[i] = nombre[i];
                }
                if (d.cantidad > 9.99) {
                    for (int i = 0; i < cant.length; i++) {
                        linea[i + 39] = cant[i];
                    }
                } else {
                     for (int i = 0; i < cant.length; i++) {
                        linea[i + 40] = cant[i];
                    }
                }
                p.setText(String.valueOf(linea));
                System.out.println(String.valueOf(linea));
            }
        }
        p.newLine();
        p.alignLeft();
        p.addLineSeperator();
        p.newLine();
        p.newLine();
        p.alignLeft();
        p.setText("Comentario:");
        p.newLine();
        p.setText(o.comentario);
        p.feed((byte) 3);
        p.finit();
        if (cont >= 1) {
            PrinterOptions.feedPrinter(p.finalCommandSet().getBytes());
        }
    }

    public void detalle(Orden o) {
        PrinterOptions p = new PrinterOptions();
        System.out.println("Cuenta");
        int cont = 0;
        p.resetAll();
        p.initialize();
        p.feedBack((byte) 2);
        p.chooseFont(1);
        p.alignCenter();
        p.setText("CUENTA");
        p.newLine();
        p.newLine();
        p.alignLeft();
        p.setText("No. Orden: \t" + o.idOrden);
        p.newLine();
        p.setText("Cliente: " + o.cliente + "\t\tFecha: " + fecha.format(fecha()));
        p.newLine();
        p.setText("Mesa: " + o.mesa + "\t\t\tHora: " + hora.format(fecha()));
        p.newLine();
        p.addLineSeperator();
        p.newLine();
        p.setText("Concepto              Cantidad  P/unit. Subtotal");
        p.newLine();
        p.addLineSeperator();
        p.alignRight();
        for (DetalleOrden d : o.detalleOrdenList) {
            cont++;
            p.newLine();
            char linea[] = new char[48];
            for (int i = 0; i < linea.length; i++) {
                linea[i] = ' ';
            }
            char cantidad[] = d.cantidad.toString().toCharArray();
            char pu[] = d.producto.precio.toString().toCharArray();
            char producto[] = d.producto.nombre.toCharArray();
            char subt[] = String.valueOf(d.producto.precio * d.cantidad).toCharArray();
            for (int i = 0; i < producto.length; i++) {
                linea[i] = producto[i];
            }
            if (d.cantidad > 9.99) {
                for (int i = 0; i < cantidad.length; i++) {
                    linea[i + 25] = cantidad[i];
                }
            } else {
                for (int i = 0; i < cantidad.length; i++) {
                    linea[i + 26] = cantidad[i];
                }
            }
            linea[33] = '$';
            if (d.producto.precio > 9.99) {
                for (int i = 0; i < pu.length; i++) {
                    linea[i + 34] = pu[i];
                }
            } else {
                for (int i = 0; i < pu.length; i++) {
                    linea[i + 35] = pu[i];
                }
            }
            linea[41] = '$';
            if ((d.cantidad * d.producto.precio) > 99.99) {
                for (int i = 0; i < subt.length; i++) {
                    linea[i + 42] = subt[i];
                }
            } else if ((d.cantidad * d.producto.precio) > 9.99) {
                for (int i = 0; i < subt.length; i++) {
                    linea[i + 43] = subt[i];
                }
            } else {
                for (int i = 0; i < subt.length; i++) {
                    linea[i + 44] = subt[i];
                }
            }
            p.setText(String.valueOf(linea));
            System.out.println(String.valueOf(linea));
        }
        p.newLine();
        p.addLineSeperator();
        p.newLine();
        p.setText("TOTAL: \t\t$ " + o.total);
        p.feed((byte) 3);
        p.finit();
        if (cont >= 1) {
            PrinterOptions.feedPrinter(p.finalCommandSet().getBytes());
        }
    }

    public Date fecha() {
        Date now = new Date();
        return now;
    }

}
