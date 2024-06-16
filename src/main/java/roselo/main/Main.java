package roselo.main;

import roselo.framework.Start;
import roselo.utilizacion.AccionDos;
import roselo.utilizacion.AccionUno;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var start = new Start(List.of(new AccionUno(), new AccionDos()));
        start.init();
    }
}
