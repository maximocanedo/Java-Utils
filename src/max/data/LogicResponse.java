package max.data;

import java.util.List;

public class LogicResponse<T> {
	public String message = "";
	public boolean status;
	public String errorMessage = "";
	public Exception exception = null;
	public T objectReturned = null;
	public T[] arrayReturned = null;
	public List<T> listReturned = null;
	public LogicResponse() {}
	public LogicResponse(boolean status, String message) {
		die(status, message);
	}
	public LogicResponse(Exception err) {
		err(err);
	}
	public LogicResponse(T obj) {
		fill(obj);
	}
	public LogicResponse(T[] arr) {
		fill(arr);
	}
	public LogicResponse(List<T> list) {
		fill(list);
	}
	public void die(boolean status, String message) {
		this.status = status;
		this.message = message;
	}
	public void err(Exception err) {
		this.status = false;
		this.exception = err;
		this.errorMessage = err.getMessage();
		
	}
	public void fill(T object) {
		if(object != null) {
			this.objectReturned = object;
			this.status = true;
		}
	}
	public void fill(T[] arr) {
		if(arr != null) {
			this.arrayReturned = arr;
			this.status = arr.length >= 0;
		}
	}
	public void fill(List<T> list) {
		if(list != null) {
			this.listReturned = list;
			this.status = list.size() >= 0;
		}
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public T getObjectReturned() {
		return objectReturned;
	}
	public void setObjectReturned(T objectReturned) {
		this.objectReturned = objectReturned;
	}
	public T[] getArrayReturned() {
		return arrayReturned;
	}
	public void setArrayReturned(T[] arrayReturned) {
		this.arrayReturned = arrayReturned;
	}
	public List<T> getListReturned() {
		return listReturned;
	}
	public void setListReturned(List<T> listReturned) {
		this.listReturned = listReturned;
	}
}
