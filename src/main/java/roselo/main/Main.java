package roselo.main;

import roselo.framework.Start;

public class Main {
    public static void main(String[] args) {
        var start = new Start("/acciones.json");
        start.init();
    }
}
