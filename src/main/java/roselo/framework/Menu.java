package roselo.framework;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.fusesource.jansi.Ansi.ansi;

public class Menu {
    private static final String MSJ_EXCEPCION_FORMATO_PROPERTIES = "No se puede crear las instancias de 'Accion' desde PROPERTIES. ";
    private static final String MSJ_EXCEPCION_FORMATO_JSON = "No se puede crear las instancias de 'Accion' desde JSON. ";
    private static final String KEY_OPCION = "opcion";
    private static final String MSJ_FORMATO_DESCONOCIDO = "Formato desconocido.";
    private static final String CONFIG_PROPERTIES_DEFAULT = "/config.properties";
    private static final String CLASS_NAME_PROPERTY = "clases";
    private static final String CLASS_NAME_JSON = "acciones";
    private static final String NAME_MAX_THREADS_JSON = "max-threads";
    private static final String TITULO_MENU = "@|green ----Menu de acciones---- |@";
    private static final String MSJ_SELECCION = "Selecciona una o varias opciones";
    public static final String REGEX_CLASES = ",";
    private List<Accion> acciones;
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
        Path pathBase = Paths.get(path);
        if (Files.notExists(pathBase)){
            throw new RuntimeException("No existe path: " + path);
        }

        this.acciones = new ArrayList<>();
        this.maxThreads = 1;
        
        if(path.endsWith(".properties")){
            this.cargarAccionesProperties(path);
        } else if (path.endsWith(".json")) {
            this.cargarAccionesJson(path);
        }
        else{
            throw new RuntimeException(MSJ_FORMATO_DESCONOCIDO);
        }
    }
    private void cargarAccionesProperties(String path) {
        Properties prop = new Properties();
        try (InputStream configFile = getClass().getResourceAsStream(path);) {
            prop.load(configFile);
            String[] clases = prop.getProperty(CLASS_NAME_PROPERTY).split(REGEX_CLASES);
            for (String c : clases) {
                var clazz = Class.forName(c);
                Accion accion = (Accion) clazz.getDeclaredConstructor().newInstance();
                this.acciones.add(accion);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    MSJ_EXCEPCION_FORMATO_PROPERTIES, e);
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
                    MSJ_EXCEPCION_FORMATO_JSON, e);
        }
    }
    private void crearPromptMenu(){
        AnsiConsole.systemInstall();
        prompt = new ConsolePrompt();
        promptBuilder = prompt.getPromptBuilder();

        System.out.println(ansi().eraseScreen().render(TITULO_MENU));
        listPromptBuilder = promptBuilder.createListPrompt();
        listPromptBuilder.name(KEY_OPCION).message(MSJ_SELECCION);

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
        this.crearPromptMenu();
        List<CallableAccion> accionesSeleccionadas = new ArrayList<>();
        while(true){
            try {
                HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
                ListResult resultado = (ListResult) result.get(KEY_OPCION);
                int opcion = Integer.parseInt(resultado.getSelectedId());
                if(opcion == 0){
                    System.out.println("Fin de la ejecucion.");
                    break;
                }
                if(opcion != this.keyFinSeleccion){
                    accionesSeleccionadas.add(new CallableAccion(this.acciones.get(opcion - 1)));
                    System.out.println("Se agrego correctamente");
                }
                else{
                    ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
                    executor.invokeAll(accionesSeleccionadas);
                    executor.shutdown();
                    System.out.println("Todas las acciones han finalizado.");
                    accionesSeleccionadas.clear();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
