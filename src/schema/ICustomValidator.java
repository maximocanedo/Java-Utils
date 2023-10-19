package schema;

public interface ICustomValidator<T> {
	boolean exec(T data);
}
