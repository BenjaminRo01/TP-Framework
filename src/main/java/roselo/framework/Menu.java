package roselo.framework;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.fusesource.jansi.Ansi.ansi;

public class Menu {
    private List<Accion> acciones;
    private static final String CONFIG_PROPERTIES_DEFAULT = "/config.properties";
    private static final String CLASS_NAME_PROPERTY = "clases";
    private static final String CLASS_NAME_JSON = "acciones";
    private static final String NAME_MAX_THREADS_JSON = "max_threads";
    private ListPromptBuilder listPromptBuilder;
    private ConsolePrompt prompt;
    private PromptBuilder promptBuilder;
    private int maxThreads;
    private int keyFinSeleccion;

    public Menu(){
        this(CONFIG_PROPERTIES_DEFAULT);
    }
    public Menu(List<Accion> acciones) {
        this.acciones = acciones;
    }
    public Menu (String path){
        this.acciones = new ArrayList<>(); //Por si llega a estar vacio
        this.maxThreads = 1; //Por si no se llega a especificar
        if(path.endsWith(".properties")){
            this.cargarAccionesProperties(path);
        } else if (path.endsWith(".json")) {
            this.cargarAccionesJson(path);
        }
        else{
            throw new RuntimeException("Formato desconocido.");
        }
    }
    private void cargarAccionesProperties(String path) {
        Properties prop = new Properties();
        try (InputStream configFile = getClass().getResourceAsStream(path);) {
            prop.load(configFile);
            String[] clases = prop.getProperty(CLASS_NAME_PROPERTY).split(",");
            for (String c : clases) {
                var clazz = Class.forName(c);
                Accion accion = (Accion) clazz.getDeclaredConstructor().newInstance();
                this.acciones.add(accion);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se puede crear las instancias de 'Accion'. ", e);
        }
    }
    private void cargarAccionesJson(String path) {
        try(InputStream configFile = getClass().getResourceAsStream(path);) {
            JSONObject jsonObject = new JSONObject(new String(configFile.readAllBytes(), StandardCharsets.UTF_8));
            JSONArray jsonArray = jsonObject.getJSONArray(CLASS_NAME_JSON);
            for (int i = 0; i < jsonArray.length(); i++) {
                var clazz = Class.forName(jsonArray.getString(i));
                Accion accion = (Accion) clazz.getDeclaredConstructor().newInstance();
                this.acciones.add(accion);
            }
            if(jsonObject.has(NAME_MAX_THREADS_JSON)){
                this.maxThreads = jsonObject.getInt(NAME_MAX_THREADS_JSON);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se puede crear las instancias de 'Accion' desde JSON. ", e);
        }
    }
    private void mostrarMenu(){
        AnsiConsole.systemInstall();
        prompt = new ConsolePrompt();
        promptBuilder = prompt.getPromptBuilder();

        System.out.println(ansi().eraseScreen().render("@|green ----Menu de acciones---- |@"));
        listPromptBuilder = promptBuilder.createListPrompt();
        listPromptBuilder.name("opcion").message("Selecciona una o varias opciones");

        int num = 1;
        for(Accion a : this.acciones){
            listPromptBuilder.newItem(String.valueOf(num))
                    .text(num + ". " + a.nombreItemMenu() + "(" + a.descripcionItemMenu() + ")").add();
            num++;
        }
        this.keyFinSeleccion = num;
        listPromptBuilder.newItem(String.valueOf(num)).text(num + ". Finalizar y ejecutar seleccion").add();
        listPromptBuilder.newItem("0").text("0. Salir").add();
        listPromptBuilder.addPrompt();
    }
    public void procesarSolicitud(){
        List<Accion> accionesSeleccionadas = new ArrayList<Accion>();
        this.mostrarMenu();
        while(true){
            try {
                HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
                ListResult resultado = (ListResult) result.get("opcion");
                int opcion = Integer.parseInt(resultado.getSelectedId());
                if(opcion == 0){
                    System.out.println("Finalizando ejecucion.");
                    break;
                }
                if(opcion != this.keyFinSeleccion){
                    accionesSeleccionadas.add(this.acciones.get(opcion - 1));
                    System.out.println("Se agrego correctamente");
                }
                else{
                    ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
                    for (Accion accion : accionesSeleccionadas) {
                        executor.submit(accion::ejecutar);
                    }
                    executor.shutdown();
                    while (!executor.isTerminated()) {
                        // Espera a que todas las tareas terminen (PREGUNTAR)
                    }
                    System.out.println("Todas las acciones han finalizado.");
                    accionesSeleccionadas.clear();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //Ver como implementar la notificación de éxito o fracaso de la ejecución.
        }
    }
}
