# Clase `Dictionary`

La clase `Dictionary` extiende la clase `HashMap` de Java y proporciona métodos adicionales para trabajar con datos clave-valor.

## Atributos

- `serialVersionUID` (Tipo: `long`): Identificador de versión de la clase.

## Métodos

### `public static Dictionary setFromArray(Object[] arr)`

Crea un `Dictionary` en base a un array.

**Parámetros:**

- `arr` (Tipo: `Object[]`): El arreglo base.

**Devuelve:**

Un `Dictionary` parseado.

**Excepciones:**

- `OddNumberOfElementsException`: Si el arreglo recibido tiene un número impar de elementos.
- `KeyIsNotAStringException`: Si alguno de los elementos pares del arreglo recibido no es una instancia de `String`.

### `public static Dictionary fromArray(Object... arr)`

Crea un `Dictionary` en base a un parámetro varargs y captura las posibles excepciones.

**Parámetros:**

- `arr` (Tipo: `Object...`): Elementos a agregar.

**Devuelve:**

Un `Dictionary` listo.

### `public boolean exists(String key)`

Comprueba si un elemento existe en la lista a partir de su clave.

**Parámetros:**

- `key` (Tipo: `String`): La clave por la cual se busca el elemento en la lista.

**Devuelve:**

`true` si existe, `false` si no existe.

### `public static int countKeys(String str)`

Cuenta los parámetros presentes en una consulta SQL.

**Parámetros:**

- `str` (Tipo: `String`): La consulta SQL a ser analizada.

**Devuelve:**

La cantidad de parámetros presentes en la consulta.

### `public <T> T $(String key)`

Obtiene el valor asociado a una clave.

**Parámetros:**

- `key` (Tipo: `String`): La clave cuyo valor asociado se desea obtener.

**Devuelve:**

El valor asociado a la clave.

### `public Object[] getParameters(String query)`

Analiza una consulta SQL y extrae los parámetros de esa consulta que están en forma de `@key`.

**Parámetros:**

- `query` (Tipo: `String`): La consulta SQL a ser analizada.

**Devuelve:**

Un array con los valores correspondientes a los parámetros, en el orden en el que aparecen en la consulta.

**Excepciones:**

- `ParameterNotExistsException`: Si un parámetro en la consulta no existe en el `Dictionary`.

