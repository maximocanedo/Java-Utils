package max;

import java.util.HashMap;
import java.util.Map;

public class Schema extends HashMap<String, SchemaProperty> {
	/**
	 * Crea un Dictionary en base a un array.
	 * @param arr El arreglo base.
	 * @return El Schema parseado.
	 * @throws OddNumberOfElementsException Si el arreglo recibido tiene un número impar de elementos.
	 * @throws KeyIsNotAStringException Si alguno de los elementos pares del arreglo recibido no es una instancia de String.
	 * @throws ValueIsNotASchemaPropertyException Si alguno de los elementos impares del arreglo recibido no es una instancia de SchemaProperty.
	 */
	public static Schema setFromArray(Object[] arr) throws OddNumberOfElementsException, KeyIsNotAStringException, ValueIsNotASchemaPropertyException {
		Schema obj = new Schema();
		if(arr.length % 2 == 0) {
			for(int i = 0; i < arr.length; i = i+2) {
				String key = "";
				// Key 0 (Par)
				Object tkey = arr[i];
				if(tkey instanceof String) {
					key = (String) tkey;
				} else {
					throw new KeyIsNotAStringException();
				}
				// Value 1 (Impar)
				Object tvalue = arr[i+1];
				if(!(tvalue instanceof SchemaProperty)) throw new ValueIsNotASchemaPropertyException();
				obj.put(key, (SchemaProperty) tvalue);
			}
		} else {
			throw new OddNumberOfElementsException();
		}
		return obj;
	}
	
	/**
	 * Crea un Schema en base a un parámetro varargs.
	 * Captura las posibles excepciones y las muestra por consola.
	 * @param arr Elementos a agregar.
	 * @return El Schema listo.
	 */
	public static Schema fromArray(Object... arr) {
		Schema d = new Schema();
		try {
			d = Schema.setFromArray(arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}
	
	public SchemaValidationResult validate(Dictionary data) {
	    for (Map.Entry<String, SchemaProperty> entry : entrySet()) {
	        String key = entry.getKey();
	        SchemaProperty sp = entry.getValue();

	        // Ver si existe
	        if (data.$(key) != null) {
	            // El dato existe.
	            SchemaValidationResult _r = sp.validate(data.$(key), key);

	            if (!_r.status) {
	                return _r;
	            }
	        } else if (sp.required) {
	            // El dato no existe y es requerido.
	            return new SchemaValidationResult(key, false, "The required property '" + key + "' is missing.");
	        }
	    }

	    // Si no se encontró ningún resultado en 'false', devolvemos un resultado en 'true'.
	    return new SchemaValidationResult("", true, "All properties passed validation.");
	}


	
	
	
	
}
