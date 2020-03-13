package beans;

import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainBean implements Serializable {

    private String x=null;
    private String y=null;
    private String r=null;
    private Connection con=null;
    private ArrayList<Point> points=new ArrayList<Point>();
    public String getR() {
        return r;
    }

    public void setR(String r) {
        System.out.println(r);
        this.r = r;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        System.out.println(y);
        this.y = y.replace(",",".");
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        System.out.println(x);
        this.x = x;
    }

    public ArrayList<Point> getPoints() {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        Connection connection = createTable();
        ArrayList<Point> points = new ArrayList<Point>();
        try {
            preparedStatement = connection.prepareStatement("select * FROM s264469.points");
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while(resultSet.next()) {
                Point point = new Point();
                point.setX(resultSet.getString(1));point.setY(resultSet.getString(2));
                point.setR(resultSet.getString(3));point.setResult(resultSet.getBoolean(4));
                points.add(point);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }
    public ArrayList<Point> getNewPoints(){
        return points;
    }

    public void addPoint(){
        try{
            Connection connection = createTable();
            String sessionId = FacesContext.getCurrentInstance().getExternalContext().getSessionId(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO s264469.points(x, y, r, result) values(?, ?, ?, ?)");

            preparedStatement.setString(1, x);
            preparedStatement.setString(2, y);
            preparedStatement.setString(3, r);
            preparedStatement.setBoolean(4, checkArea(x, y, r));
            preparedStatement.executeUpdate();
            connection.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void addNewPoints(){
        points.clear();
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            Connection connection = createTable();
            preparedStatement = connection.prepareStatement("select * FROM s264469.points");
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while(resultSet.next()) {
                Point point = new Point();
                String x=resultSet.getString(1);
                String y=resultSet.getString(2);
                point.setX(x);point.setY(y);
                point.setR(r);point.setResult(checkArea(x,y,r));
                points.add(point);
                System.out.println(point.getR());
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean checkArea(String x, String y, String r){
        double xx=Double.parseDouble(x);
        double yy=Double.parseDouble(y);
        double rr=Double.parseDouble(r);
        return ((xx >= 0 && yy >= 0 && xx <= rr && yy <= rr) || ((Math.pow(xx,2)+Math.pow(yy,2)<=Math.pow(rr/2,2)) && xx>=0 && yy<=0) || (xx <= 0 && yy >= 0 && yy<=(xx+rr/2)));
    }
    private Connection createTable(){
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://pg:5432/studs";
            String login = "s264469";
            String password = "tka226";
            con = DriverManager.getConnection(url, login, password);
            System.out.println(con.toString());
            Statement stmt = con.createStatement();
            boolean rs = stmt.execute("CREATE TABLE IF NOT EXISTS points(" +
                    "x text," +
                    "y text," +
                    "r text,"+"result boolean)");
            System.out.println("DataBase is ready! Answer:"+rs);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}