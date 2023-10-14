package max;

public class SchemaValidationResult {
	public boolean status;
	public String message;
	public String key;
	public SchemaValidationResult() {}
	public SchemaValidationResult(String key, boolean status, String message) {
		this.key = key;
		this.message = message;
		this.status = status;
	}
}