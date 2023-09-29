package utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		Conn connection = new Conn(Conn.DB.bdregistro);
		System.out.println("Probando un INSERT...");
		try {
			boolean res = connection.executeTransaction("DELETE FROM Users WHERE nombre = ?", new Object[] {
				"Marco"
			});
			if(res) {
				System.out.println("�xito!");
			} else {

				System.out.println("Error. ");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} 
		System.out.println("Probando con par�metros con @...");
		
	    // Opci�n 1: Par�metros con @
	    try {
	    	List<Map<String, Object>> result = connection.fetch(
			    	"SELECT * FROM Users WHERE nombre = @miNombre OR apellido = @miApellido",
			    	new HashMap<String, Object>() {{
				        put("miNombre", "Marco");
				        put("miApellido", "Su�rez");
				    }});
	    	
	    	for(Map<String, Object> row : result) {
	    		int id = (int) row.get("id");
	    		String nombre = (String) row.get("nombre");
	    		String apellido = (String) row.get("apellido");
	    		System.out.println("#" + id + ": " + nombre + " " + apellido);
	    	}
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    }

	    // Opci�n 2: Par�metros con ?
		System.out.println("Probando con par�metros con ?...");
	    try {
	    	List<Map<String, Object>> result = connection.fetch(
	    			"SELECT * FROM Users WHERE nombre = ? OR apellido = ?", 
	    			new Object[] { 
	    					"M�ximo", "Calabrese" 
	    			});
	    	for(Map<String, Object> row : result) {
	    		int id = (int) row.get("id");
	    		String nombre = (String) row.get("nombre");
	    		String apellido = (String) row.get("apellido");
	    		System.out.println("#" + id + ": " + nombre + " " + apellido);
	    	}
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    }
			

	}

}
