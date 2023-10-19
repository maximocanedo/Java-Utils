package max.testing;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import max.data.Dictionary;
import max.data.TransactionResponse;
import max.net.Connector;
import max.schema.Schema;
import max.schema.SchemaProperty;

public class Main {
	public static void main(String[] args) {
		noMain();
	}
    public static void noMain() {
    	Schema clientes = new Schema("clientes", "bdejemplo") {{
		    		setProperties(
		    			new SchemaProperty("id") {{
		    				required = false;
		    				type = Types.INTEGER;
		    				autoIncrement = true;
		    				primary = true;
		    			}},
		    			new SchemaProperty("nombre") {{
		    				required = true;
		    				type = Types.VARCHAR;
		    				maxlength = 50;
		    				minlength = 2;
		    			}},
		    			new SchemaProperty("correo") {{
		    				required = true;
		    				unique = true;
		    				type = Types.VARCHAR;
		    				maxlength = 256;
		    				matches = "^[A-Za-z0-9+_.-]+@(.+)$";
		    				trim = true;
		    			}}
		    		);
		    	}};
    	Schema telefonos = new Schema("telefonos", "bdejemplo") {{
    		setProperties(
    				new SchemaProperty("id") {{
    					required = false;
    					type = Types.INTEGER;
    					primary = true;
    					autoIncrement = true;
    				}},
    				new SchemaProperty("telefono") {{
    					required = true;
    					type = Types.VARCHAR;
    					maxlength = 50;
    					trim = true;
    					matches = "^[+]?[0-9\\s-]+$";
    				}},
    				new SchemaProperty("persona_id") {{
    					required = true;
    					type = Types.INTEGER;
    					searchable = true;
    					ref = clientes.ref("id");
    				}}
    			);
    	}};
    	clientes.compile();
    	telefonos.compile();
    	
    	try {
			clientes.create(Dictionary.fromArray(
						"nombre", "Jaime",
						"correo", "a24@gmail.com"
					));
			telefonos.create(Dictionary.fromArray(
						"telefono", "1130154839",
						"persona_id", 1
					));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	
    	try {
			
    		
    		/* TransactionResponse<?> res = 
					direccion.create(
						Dictionary.fromArray(
							"direccion", "Avenida Siempreviva 753",
							"persona_id", 1
						), 
						Dictionary.fromArray(
							"direccion", "Avenida Cazón 654",
							"persona_id", 2
						),
						Dictionary.fromArray(
							"direccion", "Avenida Lacaze 154",
							"persona_id", 1
						), 
						Dictionary.fromArray(
							"direccion", "Avenida Larralde 1001",
							"persona_id", 2
						)
					); */
			
			//System.out.println(res.rowsAffected + " : " + res.status);
    		
			/* TransactionResponse<?> res2 = 
					direccion.modify(
							Dictionary.fromArray(
								"direccion", "HOLA HOLA HOLA",
								"persona_id", 2
							),
							Dictionary.fromArray(
								"id", 2
							));  */
			 
			 // System.out.println(res2.rowsAffected + " : " + res2.status);
			 
			 
			 /*TransactionResponse<?> res3 = 
					 direccion.delete(Dictionary.fromArray("id", 1));
			 
			 System.out.println(res3.rowsAffected + " : " + res3.status); */
			 
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	//System.out.println(v.message);
    	
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
