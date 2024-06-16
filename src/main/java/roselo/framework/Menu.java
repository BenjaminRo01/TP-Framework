package roselo.framework;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Menu {
    private List<Accion> acciones;
    private static final String CONFIG_PROPERTIES_DEFAULT = "/config.properties";
    private static final String CLASS_NAME_PROPERTY = "clases";
    public Menu(){
        this(CONFIG_PROPERTIES_DEFAULT);
    }
    public Menu(List<Accion> acciones) {
        this.acciones = acciones;
    }
    public Menu (String path){
        this.acciones = new ArrayList<>(); //Por si llega a estar vacio
        Properties prop = new Properties();
        try (InputStream configFile = getClass().getResourceAsStream(path);) {
            prop.load(configFile);
            String[] clases = prop.getProperty("clases").split(",");
            for (String c : clases) {
                Class clazz = Class.forName(c);
                if (Accion.class.isAssignableFrom(clazz)) {
                    Accion accion = (Accion) clazz.getDeclaredConstructor().newInstance();
                    this.acciones.add(accion);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se puede crear las instancias de 'Accion'. ", e);
        }
    }
    private void mostrarMenu(){
        int num = 1;
        System.out.println("----Menú de acciones----");
        for(Accion a : this.acciones){
            System.out.println(num + ". " + a.nombreItemMenu() + "(" + a.descripcionItemMenu() + ")");
            num++;
        }
        System.out.println("0. Salir");
        System.out.println("Ingrese su opcion: ");
    }
    public void procesarSolicitud(){
        Scanner scanner = new Scanner(System.in);
        int opcion = 1;
        int tamanioList = this.acciones.size();

        this.mostrarMenu();
        opcion = scanner.nextInt();
        while(opcion != 0){
            if((opcion > 0) && (opcion <= tamanioList)) {
                this.acciones.get(opcion - 1).ejecutar();
            }
            else{
                System.out.println("Error: selecciona una de las opciones mostradas!");
            }
            this.mostrarMenu();
            opcion = scanner.nextInt();
        }

        //Ver como implementar la notificación de éxito o fracaso de la ejecución.
        System.out.println("Fin de la ejecución.");
    }
}
