package testing;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import max.*;

public class Main {
	
    public static void noMain(String[] args) {
    	Schema persona = Schema.fromArray(
    			"id", new SchemaProperty() {{
    				required = false;
    				type = Integer.class;
    			}},
    			"nombre", new SchemaProperty() {{
    				required = true;
    				type = String.class;
    				maxlength = 50;
    				minlength = 2;
    			}},
    			"edad", new SchemaProperty() {{
    				required = true;
    				type = Integer.class;
    			}}
    		);
    	Schema direccion = Schema.fromArray(
    			"id", new SchemaProperty() {{
    				required = false;
    				type = Integer.class;
    			}},
    			"direccion", new SchemaProperty() {{
    				required = true;
    				type = String.class;
    				maxlength = 100;
    			}},
    			"persona_id", new SchemaProperty() {{
    				required = true;
    				type = Integer.class;
    				ref = new ReferenceInfo("personas", "id", "segurosgroup");
    			}}
    		);
    	
    	SchemaValidationResult v = direccion.validate(Dictionary.fromArray(
    		"id", 33,
    		"direccion", "Avenida Siempreviva 753",
    		"persona_id", 99999999
    	));
    	System.out.println(v.message);
    	
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
