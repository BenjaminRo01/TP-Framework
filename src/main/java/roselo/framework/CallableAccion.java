package roselo.framework;

import java.util.concurrent.Callable;

public class CallableAccion implements Callable<Object> {
    private Accion accion;

    public CallableAccion(Accion accion) {
        this.accion = accion;
    }

    @Override
    public Object call() throws Exception {
        this.accion.ejecutar();
        return this.accion.nombreItemMenu() + " ha sido ejecutado.";
    }


//    @Override
//    public String call() throws Exception {
//        long startTime = System.currentTimeMillis();
//        System.out.println(accion.nombreItemMenu() + " comenzo a las " + startTime);
//
//        try {
//            accion.ejecutar();
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        long endTime = System.currentTimeMillis();
//        System.out.println(accion.nombreItemMenu() + " termino a las " + endTime);
//        return null;
//    }
}
