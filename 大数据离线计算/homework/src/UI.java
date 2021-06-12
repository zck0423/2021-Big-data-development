import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Scene;

import javax.swing.*;

import static java.sql.DriverManager.getConnection;

public class UI extends Application {

    private static Function one = new Function();

    public static void main(String[] args) throws SQLException {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {

        //设置树状结构
        TreeItem<String> rootItem = new TreeItem("Tables");
        rootItem.setExpanded(true);


        //设置文本框
        //设置输入文本框
        VBox vb = new VBox();
        Label label = new Label("  输入SQL语句");
        label.setStyle("-fx-font-size:20;");
        label.setStyle("-fx-padding: 20;");
        TextArea input = new TextArea();
        input.setEditable(true);
        input.setStyle("-fx-font-size:20;");

        //设置输出文本框
        Label label1 = new Label("执行结果");
        label1.setStyle("-fx-font-size:20;");
        label1.setStyle("-fx-padding: 20;");
        TableView tableView = new TableView();
        tableView.setEditable(false);
        vb.getChildren().addAll(label, input, label1, tableView);


        //创建菜单栏
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-padding: 24;");
        menuBar.setStyle("-fx-font-size:24;");

        Menu menu1 = new Menu("菜单    ");
        Menu menu2 = new Menu("帮助    ");

        //设置分割线
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        SeparatorMenuItem separator3 = new SeparatorMenuItem();

        MenuItem menuItem1 = new MenuItem("连接    ");
        MenuItem menuItem2 = new MenuItem("执行    ");
        MenuItem menuItem3 = new MenuItem("刷新    ");
        MenuItem menuItem4 = new MenuItem("退出    ");

        //连接界面和连接按钮的功能逻辑
        menuItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage createStage = new Stage();

                GridPane grid = new GridPane();
                grid.setAlignment(Pos.CENTER);
                grid.setHgap(15);
                grid.setVgap(15);
                grid.setPadding(new Insets(25, 25, 25, 25));

                grid.setStyle("-fx-font-size: 20;");
                
                Label port= new Label("端口号");
                TextField portTextField = new TextField();
                portTextField.setPrefWidth(2);
                grid.add(port,0,3);
                grid.add(portTextField,1,3);

                Label userName= new Label("用户名");
                TextField userNameTextField = new TextField();
                grid.add(userName,0,4);
                grid.add(userNameTextField,1,4);

                Label passWord= new Label("密码");
                PasswordField passWordTextField = new PasswordField();
                grid.add(passWord,0,5);
                grid.add(passWordTextField,1,5);

                HBox hbBtn = new HBox(10);
                Button confirm = new Button("确认");
                Button cancel  = new Button("取消");

                confirm.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String user = userNameTextField.getText();
                        String password = passWordTextField.getText();
                        try {
                            one.connect(user,password);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }

                        //建立表结构
                        List<String> list = new ArrayList<String>();
                        List<String> subList = new ArrayList<>();
                        try {
                            list = one.bulidTree();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        rootItem.getChildren().clear();
                        for(String s : list){
                            TreeItem<String> item = new TreeItem<>(s);
                            try {
                                subList = one.buildSubTree(s);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                            for(String ss : subList){
                                item.getChildren().add(new TreeItem<>(ss));
                            }
                            //item.getChildren().add(new TreeItem<>())
                            rootItem.getChildren().add(item);
                        }
                        createStage.close();
                    }
                });

                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        createStage.close();
                    }
                });

                hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                hbBtn.getChildren().addAll(confirm,cancel);
                grid.add(hbBtn,1,6);

                Scene scene = new Scene(grid, 400, 600);
                createStage.setScene(scene);
                createStage.setTitle("创建连接");
                createStage.show();
            }
        });

        //执行按钮的功能逻辑
        menuItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String sql = input.getText();
                //output.clear();
                try {
                    one.execute(sql,tableView);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        //刷新按钮的功能逻辑（用于更新展示数据库表、字段）
        menuItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<String> list = new ArrayList<String>();
                List<String> subList = new ArrayList<>();
                try {
                    list = one.bulidTree();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                rootItem.getChildren().clear();
                for(String s : list){
                    TreeItem<String> item = new TreeItem<>(s);
                    try {
                        subList = one.buildSubTree(s);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    for(String ss : subList){
                        item.getChildren().add(new TreeItem<>(ss));
                    }
                    //item.getChildren().add(new TreeItem<>())
                    rootItem.getChildren().add(item);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("刷新成功！");
                alert.show();
            }
        });

        //退出功能
        menuItem4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        menu1.getItems().addAll(menuItem1,separator1,menuItem2,separator2,menuItem3,separator3,menuItem4);
        menuBar.getMenus().addAll(menu1,menu2);
        TreeView treeView = new TreeView(rootItem);
        treeView.setStyle("-fx-font-size:18;");
        BorderPane bl = new BorderPane();
        bl.setTop(menuBar);
        bl.setCenter(vb);
        bl.setLeft(treeView);


        Scene mainScene = new Scene(bl,300,400);
        primaryStage.setTitle("查询器");
        primaryStage.setScene(mainScene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }
}
