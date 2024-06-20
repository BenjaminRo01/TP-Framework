### FRAMEWORK V1.2

Cambios: se ha añadido la opcion de crear un archivo .json para configurar las clases a utilizar.

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

* *Path del archivo json*, respetando su formato.
```java
      Start s = new Start("/config.json");
```

Y para finalizar llame al metodo init():
```java
      s.init();
```

Formato del archivo properties: `clase=paquete.clase1,paquete.clase2`
Formato del archivo json:
```json
    {
      "acciones": ["paquete.clase1", "paquete.clase2", "paquete.claseX"]
    }
```