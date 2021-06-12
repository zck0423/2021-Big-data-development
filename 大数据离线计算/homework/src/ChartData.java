import scala.Char;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChartData {
    public ArrayList<String> colSchema;
    public List<List<Object>> rows;

    public ChartData(ArrayList<String> schema, ResultSet result) throws SQLException {
        colSchema = schema;
        int index = 1;
        while (result.next()) {
            List<Object> row1 = (List<Object>) result.getArray(index);
            rows.add(row1);
        }
    }
    public ChartData getData(){
        return this;
    }

}

