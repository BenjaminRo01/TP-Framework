package roselo.framework;

import java.util.List;

public class Start {
    private Menu menu;

    public Start(){
        this.menu = new Menu();
    }
    public Start(List<Accion> acciones){
        this.menu = new Menu(acciones);
    }
    public Start(String path){
        this.menu = new Menu(path);
    }

    public void init(){
        this.menu.procesarSolicitud();
    }
}
