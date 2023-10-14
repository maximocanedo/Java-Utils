package testing;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import max.*;

public class Main {
	public static void testSchemas() {
		// Usuarios
    	Schema userSchema = Schema.fromArray(
    			"username", new SchemaProperty() {{
    				required = true;
    				type = String.class;
    				matches = "^[a-zA-Z0-9_-]{3,16}$";
    			}},
    			"biography", new SchemaProperty() {{
    				type = String.class;
    			}},
    			"age", new SchemaProperty() {{
    				required = true;
    				type = Number.class;
    				min = 18;
    			}},
    			"birthdate", new SchemaProperty() {{
    				type = Calendar.class;
    				required = true;
    			}}
    		);
    	Calendar fecha1 = Calendar.getInstance();
    	fecha1.set(2003, Calendar.NOVEMBER, 12);
    	Calendar fecha2 = Calendar.getInstance();
    	fecha2.set(2010, Calendar.APRIL, 1);
    	Dictionary datosCorrectosDePrueba = Dictionary.fromArray( // Todo bien
    			"username", "usuarioEjemplo",
    			"biography", "Usuario de ejemplo 123",
    			"age", 24,
    			"birthdate", fecha1
    		);
    	Dictionary datosIncorrectosPrueba = Dictionary.fromArray( // Usuario muy corto
    			"username", 34,
    			"age", 24,
    			"birthdate", fecha1
    		);
    	Dictionary datosIncorrectos2 = Dictionary.fromArray( // Edad menor al mínimo admitido
    			"username", "usuarioEj1234",
    			"age", 10,
    			"birthdate", fecha2
    		);
    	Dictionary datosIncorrectos3 = Dictionary.fromArray( // Le falta el campo "username".
    			"age", 50,
    			"birthdate", fecha2
    		);
    	SchemaValidationResult v1 = userSchema.validate(datosCorrectosDePrueba);
    	System.out.println("V1: (" + v1.status + ") "+ v1.message);
    	SchemaValidationResult v2 = userSchema.validate(datosIncorrectosPrueba);
    	System.out.println("V1: (" + v2.status + ") "+ v2.message);
    	SchemaValidationResult v3 = userSchema.validate(datosIncorrectos2);
    	System.out.println("V1: (" + v3.status + ") "+ v3.message);
    	SchemaValidationResult v4 = userSchema.validate(datosIncorrectos3);
    	System.out.println("V1: (" + v4.status + ") "+ v4.message);
	}
    public static void main(String[] args) {
    	
    	
    }

    public static void testDictionary() {
        try {
            // Prueba de creación de un Dictionary a partir de un array
            Dictionary dict = Dictionary.fromArray(
                "nombre", "%e",
                "apellido", "Pérez"
            );

            // Prueba de exists
            assert dict.exists("nombre") == true;
            assert dict.exists("edad") == false;

            // Prueba de getParameters
            String query = "SELECT * FROM Personas WHERE Nombre LIKE @nombre";
            Object[] params = dict.getParameters(query);
            assert params.length == 2;
            assert params[0].equals("%e");
            assert params[1].equals("Pérez");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testConnector() {
        Connector x = new Connector(Connector.DB.bdPersonas);
        try {
            // Prueba de fetch
            TransactionResponse<Dictionary> t = x.fetch(
                "SELECT * FROM Personas WHERE Nombre LIKE @nombre",
                Dictionary.fromArray(
                    "nombre", "%e"
                )
            );

            List<Dictionary> ppl = t.rowsReturned;
            for (Dictionary p : ppl) {
                String name = (String) p.get("Nombre");
                System.out.println(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
