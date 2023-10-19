package data;

public interface IAction<T> {
	void exec(T data);
}