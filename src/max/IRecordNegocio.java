package max;

import java.sql.SQLException;

public interface IRecordNegocio<X, Y> {
	public LogicResponse<X> validate(X data);
	public LogicResponse<X> insert(X data) throws SQLException;
	public LogicResponse<X> delete(X data) throws SQLException;
	public LogicResponse<X> modify(X data, Y id) throws SQLException;
	public LogicResponse<X> getAll();
	public LogicResponse<X> select(String query);
	public LogicResponse<X> select(String query, Dictionary params);
	public LogicResponse<X> select(String query, Object[] params);
	public LogicResponse<X> getById(Y id);
	public LogicResponse<X> exists(Y id);
}
