package max;

public class ValueIsNotASchemaPropertyException extends Exception {
	public ValueIsNotASchemaPropertyException() {
		super("The key provided is not an instance of SchemaProperty. ");
	}
}
