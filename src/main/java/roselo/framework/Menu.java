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

import static org.fusesource.jansi.Ansi.ansi;

public class Menu {
    private List<Accion> acciones;
    private static final String CONFIG_PROPERTIES_DEFAULT = "/config.properties";
    private static final String CLASS_NAME_PROPERTY = "clases";
    private static final String CLASS_NAME_JSON = "acciones";
    private ListPromptBuilder listPromptBuilder;
    private ConsolePrompt prompt;
    private PromptBuilder promptBuilder;

    public Menu(){
        this(CONFIG_PROPERTIES_DEFAULT);
    }
    public Menu(List<Accion> acciones) {
        this.acciones = acciones;
    }
    public Menu (String path){
        this.acciones = new ArrayList<>(); //Por si llega a estar vacio
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
        listPromptBuilder.name("opcion").message("Selecciona una opcion");

        int opcion = 0;
        int num = 1;
        for(Accion a : this.acciones){
            listPromptBuilder.newItem(String.valueOf(num))
                    .text(num + ". " + a.nombreItemMenu() + "(" + a.descripcionItemMenu() + ")").add();
            num++;
        }
        listPromptBuilder.newItem("0").text("Salir").add();
        listPromptBuilder.addPrompt();
    }
    public void procesarSolicitud(){
        this.mostrarMenu();
        try {
            HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
            ListResult resultado = (ListResult) result.get("opcion");
            int opcion = Integer.parseInt(resultado.getSelectedId());
            while(opcion != 0){
                this.acciones.get(opcion - 1).ejecutar();
                result = prompt.prompt(promptBuilder.build());
                resultado = (ListResult) result.get("opcion");
                opcion = Integer.parseInt(resultado.getSelectedId());
            }
            System.out.println("Finalizando ejecucion.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Ver como implementar la notificación de éxito o fracaso de la ejecución.
    }
}
