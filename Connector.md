# Clase Connector
## Introducción
Connector es una clase que facilita el manejo de consultas SQL. Por el momento sólo soporta conexiones a servidores MySQL.
## Funcionamiento básico
### ¿Cómo ejecutar una consulta?
Para ejecutar una consulta, tenemos que usar los métodos `fetch()` o `transact()`, dependiendo si se espera que la consulta devuelva un conjunto de valores (Tabla de resultados) o no.

#### ¿Cómo hacer una consulta que devuelve datos (SELECT)?
Usamos el método `fetch()`, al que se le pasan por parámetros la consulta en formato `String`, y los parámetros de la misma (Opcional), en formato `Dictionary`.
```java
try {
  Connector myConnector("myDB");
  TransactionResponse<Dictionary> res = myConnector.fetch(
      "SELECT * FROM myTable"
    );
  if(res.nonEmptyResult()) {
    List<Dictionary> tableResult = res.rowsReturned;
  }
} catch(SQLException e) {
  e.printStackTrace();
}
```
A continuación, ejemplo de uso de `fetch()` con parámetros:
```java
try {
  TransactionResponse<Dictionary> res1 = myConnector.fetch(
      "SELECT * FROM myTable WHERE id = @myId OR name LIKE @myName AND surname = @surname",
      Dictionary.fromArray(
        "myId", 7,
        "myName", "%Diego%",
        "surname", "Herrera"
      )
    );
  if(res1.nonEmptyResult()) {
    List<Dictionary> tableResult = res.rowsReturned;
  }
} catch (SQLException e) {
  e.printStackTrace();
}
```
Como se puede observar, para implementar parámetros en una consulta SQL se añade un objeto `Dictionary` como segundo parámetro.
El objeto Dictionary espera una cantidad par de elementos, en dónde cada elemento impar es un `String` que contiene el nombre del parámetro, y cada elemento par es un `Object` que representa el valor que tendrá dicho parámetro.

#### Consultas del tipo INSERT, UPDATE, DELETE
Usamos el método `transact()`, al que se le pasan por parámetros la consulta en formato `String`, y los parámetros de la misma (Opcional), en formato `Dictionary`.
```java
try {
  Connector myConnector("myDB");
  TransactionResponse<?> res = myConnector.transact(
      "INSERT INTO myTable (id, name, surname) SELECT @id, @name, @surname",
      Dictionary.fromArray(
        "id", 8,
        "name", "Esteban",
        "surname", "Quito"
      )
    );
  if(res.rowsAffected > 0 || res.status) {
    // La consulta fue un éxito
  }
} catch(SQLException e) {
  e.printStackTrace();
}
```
Aunque en este caso se usaron parámetros, es posible usar el método `transact()` únicamente pasando el `String` de la consulta.

#### Parámetros con "?"
Los métodos `fetch()` y `transact()` pueden trabajar con parámetros del estilo `@key`, como también con parámetros del estilo `?`. 
Si se escoge esta segunda opción, se debe reemplazar el objeto `Dictionary` por un `Object[]`. 
A continuación, un ejemplo:
```java
try {
  TransactionResponse<Dictionary> res1 = myConnector.fetch(
      "SELECT * FROM myTable WHERE id = ? OR name LIKE ? AND surname = ?",
      new Object[] { 7, "%Diego%", "Herrera" }
    );
  if(res1.nonEmptyResult()) {
    List<Dictionary> tableResult = res.rowsReturned;
  }

  TransactionResponse<?> res = myConnector.transact(
      "INSERT INTO myTable (id, name, surname) SELECT ?, ?, ?",
      new Object[] { 8, "Esteban", "Quito" }
    );
  if(res.rowsAffected > 0 || res.status) {
    // La consulta fue un éxito
  }
} catch (SQLException e) {
  e.printStackTrace();
}
```

## API
### `Connector(ConnectorSettings data, String database)`
Constructor de la clase `Connector` que recibe una instancia de `ConnectorSettings` y el nombre de la base de datos.
#### Sintaxis
```java
public Connector(ConnectorSettings data, String database)
```
#### Parámetros
| Parámetro | Tipo              | Descripción                              |
|-----------|-------------------|------------------------------------------|
| data      | ConnectorSettings | Configuración para conectar al servidor. |
| database  | String            | Nombre de la base de datos.              |


### `Connector(String database)`
Constructor de la clase `Connector` que recibe el nombre de la base de datos.
#### Sintaxis
```java
public Connector(String database)
```
#### Parámetros
| Parámetro | Tipo              | Descripción                              |
|-----------|-------------------|------------------------------------------|
| database  | String            | Nombre de la base de datos.              |


### `Connector(ConnectorSettings data)`
Constructor de la clase `Connector` que recibe una instancia de `ConnectorSettings` y usa la base de datos predeterminada. 
#### Sintaxis
```java
public Connector(ConnectorSettings data)
```
#### Parámetros
| Parámetro | Tipo              | Descripción                              |
|-----------|-------------------|------------------------------------------|
| data      | ConnectorSettings | Configuración para conectar al servidor. |

### `Connector()`
Constructor de la clase `Connector` que usa la configuración predeterminada.
#### Sintaxis
```java
public Connector()
```

### Método `fetch()`
Realiza una consulta que devuelve un conjunto de datos.
#### Sintaxis
```java
public TransactionResponse<Dictionary> fetch(String query [, (Object[] | Dictionary) params]) throws SQLException
```
#### Parámetros
| Parámetro | Tipo                   | Descripción                          |
|-----------|------------------------|--------------------------------------|
| query     | String                 | Consulta a ejecutar                  |
| params    | Dictionary \| Object[] | Parámetros, en caso de requerirlos.  |

#### Ejemplo de uso
```java
try {
  TransactionResponse<Dictionary> res = myConnector.fetch(
    "SELECT * FROM myTable"
  );
} catch (SQLException e) {
  e.printStackTrace();
}
```
#### Valor de retorno
Este método devuelve un objeto del tipo `TransactionResponse<Dictionary>` con el resultado de la operación.

### Método `transact()`
Realiza una consulta que no devuelve un conjunto de datos. Ideal para ejecutar transacciones.
#### Sintaxis
```java
public TransactionResponse<Dictionary> transact(String query [, (Object[] | Dictionary) params]) throws SQLException
```
#### Parámetros
| Parámetro | Tipo                   | Descripción                          |
|-----------|------------------------|--------------------------------------|
| query     | String                 | Consulta a ejecutar                  |
| params    | Dictionary \| Object[] | Parámetros, en caso de requerirlos.  |

#### Ejemplo de uso
```java
try {
  TransactionResponse<Dictionary> res = myConnector.transact(
    "INSERT INTO myTable (id, nombre, apellido) SELECT @id, @nombre, @apellido",
    Dictionary.fromArray(
      "id", 9,
      "nombre", "Mario",
      "apellido", "González"
    )
  );
} catch (SQLException e) {
  e.printStackTrace();
}
```
#### Valor de retorno
Este método devuelve un objeto del tipo `TransactionResponse<Dictionary>` con el resultado de la operación.
