import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


import static java.sql.DriverManager.getConnection;
import static java.sql.DriverManager.getConnection;
public class Function {
    private static Connection conn;
    private static String userId;

    //连接数据库
    public static void connect(String user,String password) throws SQLException {

        userId = user;
        String url = "jdbc:hive2://bigdata115.depts.bingosoft.net:22115/"+user+"_db";
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        conn = getConnection(url,user,password);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("连接成功！");
        alert.show();

    }

    //执行sql语句
    public static void execute(String sql, TableView output) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery(sql);
        //stmt.execute(sql);
        if(sql.contains("select")||sql.contains("SELECT")){
            //获取表名
            String array[] = sql.split(" ");
            int index = 0;
            for(int i=0;i<array.length;++i){
                if(array[i].equals("from")){
                    index = i;
                    break;
                }
            }
            //获取表中列元素名称
            List<String> temp = buildSubTree(array[index+1]);
            ChartData data = new ChartData((ArrayList<String>) temp,res);
            int colsCount = data.colSchema.size();
            output.getColumns().clear();
            for(int i=0;i<colsCount;++i){
                TableColumn<List<Object>,Object> column = new TableColumn<>(data.colSchema.get(i));
                int columnIndex = i;
                column.setCellValueFactory(cellData ->
                        new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
                output.getColumns().add(column);
            }
            output.getItems().setAll(data.rows);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("操作成功！");
        alert.show();
    }

    //返回库中所有表名，建立树状结构（数据库中表）
    public static List<String> bulidTree() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show tables");
        List<String> temp = new ArrayList<String>();
        while(res.next()){
            temp.add(res.getString(1));

        }
        return temp;
    }

    //返回指定表的列名，用于创建子树
    public static List<String> buildSubTree(String s) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet res = stmt.executeQuery("show columns FROM " + s);
        List<String> list = new ArrayList<>();
        while(res.next()) {
            list.add(res.getString(1));
        }
        return list;
    }
}
