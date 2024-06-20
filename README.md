### FRAMEWORK V1.0

Este framework permitira mostrar cada una de las acciones en forma de lista para que pueda seleccionar que accion desea realizar.
Ejemplo:
```
   ----Menú de acciones----
   1. AccionUno (Descripcion de la accion uno)
   2. AccionDos (Descripcion de la accion dos)
   0. Salir
   Ingrese su opcion:_   
```

Usted para usarlo debe:
- Importar el framework.
- Implementar la interfaz **Accion**, con la accion que desee que se muestre y se ejecute.
- Crear una instancia **Start**, puede pasar por constructor:
  * *Nada*, pero para esto debe crear un archivo **config.properties** y colocarlo en la carpeta "resources" de su proyecto.
```java
      Start s = new Start();
```
  * *Path del archivo properties*, respetando su formato.
```java
      Start s = new Start("/config.properties");
```
  * Una lista de las implementaciones de la interfaz **Accion**.
```java
      Start s = new Start(List.of(new AccionUno(), new AccionDos()));
```

Y para finalizar llame al metodo init():
```java
      s.init();
```

Formato del archivo properties: `clase=paquete.clase1,paquete.clase2`
