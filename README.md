### FRAMEWORK V1.3

Cambios: ahora se pueden seleccionar una o mas acciones. Dichas acciones se ejecutaran de forma concurrente. Ademas se 
añadio una nueva forma de configuración (con .json) en donde puede añadir los números de threads máximos permitidos para ser ejecutados
concurrentemente.

Ejemplo de interfaz:
```
   ----Menú de acciones----
   ? Selecciona una opcion
   > 1. AccionUno (Descripcion de la accion uno)
     2. AccionDos (Descripcion de la accion dos)
     3. Ejecutar seleccion
     0. Salir   
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
* *Una lista de las implementaciones de la interfaz **Accion***.
```java
      Start s = new Start(List.of(new AccionUno(), new AccionDos()));
```

* *Path del archivo json*, respetando su formato.
```java
      Start s = new Start("/config.json");
```

Y para finalizar llame al metodo init():
```java
      s.init();
```

Formato del archivo properties: `clase=paquete.clase1,paquete.clase2`
Formato del archivo json sin especificar maximo de threads:
```json
    {
      "acciones": ["paquete.clase1", "paquete.clase2", "paquete.claseX"]
    }
```
Formato del archivo json especificando maximo de threads:
```json
    {
      "acciones": ["paquete.clase1", "paquete.clase2", "paquete.claseX"],
      "max-threads": 3
    }
```

![Diagrama UML Framework](/src/main/resources/DigramaUMLFramework.png)