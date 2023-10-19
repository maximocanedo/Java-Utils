# Schemas
## Introducción
Un schema es una estructura de datos que permite validar registros de forma más eficiente. Está inspirado en las funcionalidades de Mongoose.
Los Schemas funcionan como tablas en SQL. 
Al definir un Schema, se definen sus propiedades o campos, con sus atributos, como el tipo de dato que se espera manejar, restricciones, referencias a otros Schemas o tablas, etc. 
Luego de definirlos, se pueden usar para validar que los valores de un objeto Dictionary cumplan con las restricciones impuestas en el Schema.

## Funcionamiento
### ¿Cómo crear un Schema?
Para crear un Schema, podemos usar su constructor de la siguiente manera:
```java
Schema personas = new max.Schema("personas", "bdPersonas") {{
   setProperties(
      new SchemaProperty("id") {{
         required = true;
         type = Types.INTEGER;
         primary = true;
         autoIncrement = true;
      }},
      new SchemaProperty("nombre") {{
         required = true;
         type = Types.VARCHAR;
         maxlength = 50;
      }},
      new SchemaProperty("correo") {{
         type = Types.VARCHAR;
         matches = "^[+]?[0-9\\s-]+$";
         trim = true;
         maxlength = 50
      }},
   );
}};
```
Hasta acá, tenemos un Schema que contiene una estructura de datos de personas:
 - Id (Clave primaria, de tipo entero, que se autoincrementa)
 - Nombre (Requerido, de tipo varchar(50))
 - Correo (De tipo varchar(50), con una expresión regular que valida que sea un correo electrónico)

Así cómo está podríamos hacer uso de algunos métodos como `validate`. 

### ¿Cómo hacer validaciones sobre el Schema?
El objeto `Schema` cuenta con un método `validate()` que recibe como primer y único parámetro un objeto de tipo `Dictionary` con los datos a verificar. Devuelve un objeto de tipo `SchemaValidationResult`.
Ejemplo de uso:
```java
Dictionary armandoGonzalez = Dictionary.fromArray(
      "nombre", "Armando",
      "correo", "armando.gonzalez@gmail.com"
   );
SchemaValidationResult validacionArmando = personas.validate(armandoGonzalez);
print(validacionArmando.status ? "Las validaciones fueron un éxito. " : validacionArmando.message);
// Imprime "Las validaciones fueron un éxito".
```
Ejemplo de uso con datos que no respetan la estructura del Schema:
```java
Dictionary diegoHerrera = Dictionary.fromArray(
      "nombre", "Diego",
      "correo", 39 // Ponemos un entero en lugar de una cadena de texto
   );
SchemaValidationResult validacionDiego = personas.validate(diegoHerrera);
print(validacionDiego.status ? "Las validaciones fueron un éxito. " : validacionDiego.message);
// Imprime "A INTEGER object was provided, but this object type is not allowed in the Schema. ".
```
En una próxima versión se implementarán Excepciones para manejar validaciones con resultado negativo.

### ¿Qué es compilar un Schema?
Cuando se tiene un Schema listo, se puede usar el método `compile`, que crea la base de datos y la tabla con todos sus campos, en caso de no existir previamente.
Esto permite además, hacer uso de métodos de agregado, modificación y eliminación de registros.
Ejemplo: 
```java
personas.compile();
```

### Insertar un registro en la tabla vinculada al Schema
Mediante el uso del método `create()`, es posible añadir registros en la tabla vinculada al Schema.
Se recomienda cerciorarse de que el objeto `Schema` en cuestión ya haya ejecutado el método `compile()` previo a la ejecución de `create()`.
Ejemplo de uso:
```java
TransactionResponse<?> resultadoInsercion = personas.create(
      Dictionary.fromArray(
         "nombre", "Antonio",
         "correo", "antonio@gmail.com"
      ),
      Dictionary.fromArray(
         "nombre", "Julia"
      ),
      Dictionary.fromArray( 
         "edad", 32
      )
   );
print("Se añadieron con éxito " + resultadoInsercion.rowsAffected + " columna/s. ");
// Imprime "Se añadieron con éxito 2 columna/s. ".
```
En el ejemplo anterior se envían tres registros `Dictionary`, de los cuales sólo dos terminan subiéndose exitosamente, porque el tercero no cumplía con las restricciones impuestas en el `Schema`.

Ahora, podemos comprobar que se añadieron esos registros si ejecutamos el siguiente código MySQL:
```mysql
SELECT * FROM personas;
```
| id | nombre  | correo            |
|----|---------|-------------------|
| 1  | Antonio | antonio@gmail.com |
| 2  | Julia   | ```NULL```        |

### Modificar un registro en la tabla vinculada al Schema
Mediante el uso del método `modify()` es posible modificar un registro en la tabla vinculada al Schema.
Este método recibe dos parámetros de tipo `Dictionary`: El primero contiene los campos que se van a editar con sus nuevos valores, mientras que el seguro servirá de filtro para seleccionar los registros que se van a modificar.
Ejemplo de uso:
```java
TransactionResponse<?> res = personas.modify(
      Dictionary.fromArray( "correo", "julia.1990@hotmail.com" ),
      Dictionary.fromArray( "id", 2 )
   );
print(res.status ? "El registro se modificó correctamente. " : "No se modificó ningún registro. ");
// Imprime "El registro se modificó correctamente. ".
```
Podemos comprobar que se efectuó la modificación si ejecutamos de nuevo el código SQL anterior y observamos que ahora el registro con ID N.º 2 ya cuenta con correo.
```sql
SELECT * FROM personas
```
| id | nombre  | correo                 |
|----|---------|------------------------|
| 1  | Antonio | antonio@gmail.com      |
| 2  | Julia   | julia.1990@hotmail.com |

### Eliminar un registro de la tabla vinculada al Schema
Para eliminar un registro se usa el método `delete()`, que recibe un sólo parámetro de tipo `Dictionary` que funciona como filtro para seleccionar los registros que se desean eliminar.
Ejemplo de uso:
```java
TransactionResponse<?> res2 = personas.delete(
      Dictionary.fromArray("id", 1)
   );
print(res.status ? "El registro se eliminó correctamente. " : "No se eliminó ningún registro. ");
// Imprime "El registro se eliminó correctamente. ".
```
Podemos comprobar que se efectuó la eliminación si ejecutamos de nuevo el código SQL anterior y observamos que ahora el registro con ID N.º 1 fue eliminado.
```sql
SELECT * FROM personas
```
| id | nombre  | correo                 |
|----|---------|------------------------|
| 2  | Julia   | julia.1990@hotmail.com |

### Claves foráneas y referencias a otros Schemas
Supongamos que ahora necesitamos otro `Schema` que almacene las direcciones de las personas en el `Schema` personas.
```java
Schema direcciones = new max.Schema("direcciones", "bdPersonas") {{
   setProperties(
      new SchemaProperty("id") {{
         required = true;
         type = Types.INTEGER;
         primary = true;
         autoIncrement = true;
      }},
      new SchemaProperty("direccion") {{
         required = true;
         type = Types.VARCHAR;
         maxlength = 100;
      }},
      new SchemaProperty("persona_id") {{
         type = Types.INTEGER;
         ref = personas.ref("id");
         required = true;
      }}
   );
}};
```
Este Schema cuenta con las siguientes propiedades:
- Id: Entero autonumérico, clave primaria.
- Dirección: Requerido, de tipo varchar(100).
- Persona_Id: Entero que hace referencia a la propiedad `id` del Schema personas.

De esta forma, al intentar validar o añadir un registro en la base de datos por medio de este Schema, validará también si `persona_id` hace referencia a un `id` del Schema personas.

Ejemplo de uso:
```java
TransactionResponse<?> resultadoInsercion = direcciones.create(
      Dictionary.fromArray(
         "direccion", "Avenida Lacaze 1150",
         "persona_id", 1
      ),
      Dictionary.fromArray(
         "direccion", "Av. 9 de Julio 1350 3º Piso",
         "persona_id", 2
      ),
      Dictionary.fromArray( 
         "direccion", "Avenida Cazón 750",
         "persona_id", 2
      )
   );
print("Se añadieron con éxito " + resultadoInsercion.rowsAffected + " columna/s. ");
// Imprime "Se añadieron con éxito 2 columna/s. ".
```
En este ejemplo se pretenden añadir tres registros, pero se añaden dos porque el primero de ellos tiene un `persona_id` que no corresponde con ningún registro en el Schema personas.
Podemos verificar que la operación haya salido bien si ejecutamos el siguiente código SQL:
```sql
SELECT * FROM direcciones;
```
| id | direccion                   | persona_id |
|----|-----------------------------|------------|
| 1  | Av. 9 de Julio 1350 3º Piso | 2          |
| 2  | Avenida Cazón 750           | 2          |

## API
### Constructores
Cuenta con un único constructor, que recibe el nombre de la tabla y el nombre de la base de datos.
#### Sintaxis
```java
public Schema(String tableName, String dbName)
```
#### Parámetros
| Parámetro | Tipo   | Descripción                                                   |
|-----------|--------|---------------------------------------------------------------|
| tableName | String | Nombre de la tabla a la cual estará ligado el objeto.         |
| dbName    | String | Nombre de la base de datos a la cual estará ligado el objeto. |
#### Ejemplo de uso
```java
Schema animales = new Schema("animales", "bdAnimales");
```
### Método `ref()`
Devuelve un objeto `ReferenceInfo` que sirve para referenciar una propiedad a una propiedad de otro Schema.
#### Sintaxis
```java
public ReferenceInfo ref(String propertyName)
```
#### Parámetros
| Parámetro    | Tipo   | Descripción                                                   |
|--------------|--------|---------------------------------------------------------------|
| propertyName | String | Nombre de la columna o propiedad del Schema.                  |
#### Ejemplo de uso
```java
ReferenceInfo refId = animales.ref("id");
```
#### Valor de retorno
Este método retorna un objeto de tipo `ReferenceInfo`, que contiene el nombre de la tabla, el nombre de la columna, y el nombre de la base de datos.

### Método `setProperties()`
Establece las propiedades o columnas con las que contará el objeto `Schema`.
#### Sintaxis
```java
public void setProperties(SchemaProperty... properties)
```
#### Parámetros
| Parámetro  | Tipo           | Descripción                    |
|------------|----------------|--------------------------------|
| properties | SchemaProperty | Propiedades a añadir al Schema |
#### Ejemplo de uso
```java
animales.setProperties(
   new SchemaProperty("id") {{
      required = true;
      autoIncrement = true;
      type = Types.INTEGER;
   }},
   new SchemaProperty("nombre") {{
      type = Types.VARCHAR;
      maxlength = 50;
   }}
);
```

