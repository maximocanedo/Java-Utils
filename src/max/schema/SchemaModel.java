package max.schema;

import java.util.Collection;

import max.data.Dictionary;

public class SchemaModel extends Dictionary {
	private Schema schema;
	private void setDefaults() {
		clear();
		Collection<SchemaProperty> properties = schema.values();
		for(SchemaProperty property : properties) {
			// Establecemos las propiedades requeridas en sus valores por defecto.
			if(property.required) {
				put(property.name, property.defaultValue);
			}
		}
	}
	public SchemaModel(Schema schema) {
		this.schema = schema;
		setDefaults();
	}
	
	public SchemaValidationResult create(Dictionary data) {
		SchemaValidationResult svr = this.schema.validate(data);
		if(svr.status) {
			for(String key : data.keySet()) {
				// Verificamos que sea una propiedad válida
				if(this.schema.containsKey(key)) {
					// Hacemos las validaciones:
					SchemaProperty p = this.schema.get(key);
					SchemaValidationResult sv = p.prepare(data.$(key));
					this.put(key, sv.transformedValue);
				}
			}
		} 
		return svr;
	}
}
