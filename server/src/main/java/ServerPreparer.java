import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Simple class for creating/connecting database, user and tables before using this application.
 * And also storing data in settings file.
 */
public class ServerPreparer {
    public static void main(String[] args) {
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        String settingsPath;
        if (args != null && args.length > 0) {
            settingsPath = args[0];
        } else settingsPath = "./settings";

        String dbType, dbHost, dbPort, dbRootUserName, dbName, dbUserName, dbPassword;
        StringBuilder dbConnectionString = new StringBuilder("jdbc:");
        String mysql = "mysql", postgresql = "postgresql";
        String driverClassName;             // сюда положим нужный драйвер в зависимости от типа БД
        String noSSL = "?useSSL=false";     // исключаем предупреждение из логов; будет добавлено к строке подключения к БД последним

        try {
            System.out.print("Select database type. Empty string for MySQL, not empty string for PostgreSQL: ");
            dbType = consoleReader.readLine();
            // выставляем тип БД и определяем драйвер для работы с ней
            if (dbType.isEmpty()) {
                dbType = mysql;
                driverClassName = "com.mysql.jdbc.Driver";
            }
            else {
                dbType = postgresql;
                driverClassName = "org.postgresql.Driver";
            }
            dbConnectionString.append(dbType).append("://");
            System.out.print("Enter host name (empty string for localhost): ");
            dbHost = consoleReader.readLine();
            if (dbHost.isEmpty()) {
                dbHost = "localhost";
            }
            dbConnectionString.append(dbHost).append(":");
            System.out.print("Enter port (blank string for default port): ");
            dbPort = consoleReader.readLine();
            if (dbPort.isEmpty()) {
                if (dbType.equals(mysql)) dbPort = "3306";
                if (dbType.equals(postgresql)) dbPort = "5432";
            }
            dbConnectionString.append(dbPort);

            // регистрируем драйвер в зависимости от типа базы данных
            DriverManager.registerDriver((Driver) Class.forName(driverClassName).newInstance());
            Connection connection;

            System.out.print("Do you need to create new database and user? [y/n] ");
            if ("y".equalsIgnoreCase(consoleReader.readLine())) {
                System.out.println("Let's test connection first...");
                System.out.print("Enter the name of root user: ");
                dbRootUserName = consoleReader.readLine();
                System.out.print("Enter root password: ");
                /*
                * пытаемся подключиться к базе.
                * строка подключения выглядит примерно так:
                * mysql: jdbc:mysql://localhost:3306?useSSL=false
                * postgresql: jdbc:postgresql://localhost:5432?useSSL=false
                * */
                connection = DriverManager.getConnection(
                        dbConnectionString.append(noSSL).toString(),
                        dbRootUserName,
                        consoleReader.readLine());
                System.out.println("Connected successfully!");
                dbConnectionString.insert(dbConnectionString.lastIndexOf("?"), "/");

                System.out.print("Enter new database name: ");
                dbName = consoleReader.readLine();

                Statement statement = connection.createStatement();
                statement.executeUpdate(String.format("create database %s;", dbName));

                System.out.print("Enter new username: ");
                dbUserName = consoleReader.readLine();
                System.out.print("Enter password: ");
                dbPassword = consoleReader.readLine();

                String createUserTemplate = "", grantPrivilegesTemplate = "";
                // todo: выяснить значение host в запросе и исправить если неправильно
                if (dbType.equals(mysql)) {
                    createUserTemplate = "CREATE USER '%s'@'" + dbHost + "' IDENTIFIED BY '%s';";
                    grantPrivilegesTemplate = "GRANT ALL PRIVILEGES ON %s.* TO '%s'@'" + dbHost + "';";
                }
                if (dbType.equals(postgresql)) {
                    createUserTemplate = "create user %s with password '%s';";
                    grantPrivilegesTemplate = "GRANT ALL privileges ON DATABASE %s TO %s;";
                }

                statement.execute(String.format(createUserTemplate, dbUserName, dbPassword));
                statement.execute(String.format(grantPrivilegesTemplate, dbName, dbUserName));

                statement.close();
                System.out.println("Created successfully.\nTrying to connect with new user...");
                connection.close(); // закрываем root подключение и пытаемся подключиться новым пользователем
                connection = DriverManager.getConnection(
                        dbConnectionString.insert(dbConnectionString.lastIndexOf("?"), dbName).toString(),
                        dbUserName,
                        dbPassword);
            } else {
                System.out.print("Enter database name to use: ");
                dbName = consoleReader.readLine();
                System.out.print("Enter username: ");
                dbUserName = consoleReader.readLine();
                System.out.print("Enter password: ");

                connection = DriverManager.getConnection(
                        dbConnectionString.append("/").append(dbName).append(noSSL).toString(),
                        dbUserName,
                        consoleReader.readLine());
            }
            System.out.println("Success!");
            connection.close();

        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Sorry, can't connect");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Properties properties = new Properties();
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(settingsPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
