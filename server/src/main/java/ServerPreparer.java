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

        String dbType = "",
                dbHost = "",
                dbPort = "",
                dbRootUserName,
                dbName = "",
                dbUserName = "",
                dbPassword = "";
        StringBuilder dbConnectionString = new StringBuilder("jdbc:");      // сюда собираем строку для подключения к базе
        String mysql = "mysql",
                postgresql = "postgresql";
        String noSSL = "?useSSL=false";     // исключаем предупреждение из логов; будет добавлено к строке подключения к БД последним
        String driverClassName = "";        // сюда положим нужный драйвер в зависимости от типа БД

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
            Connection connection;  // соединение с базой

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
                        dbConnectionString.append("/").append((dbType.equals(postgresql) ? "postgres" : "")).append(noSSL).toString(),
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
                dbPassword = consoleReader.readLine();

                connection = DriverManager.getConnection(
                        dbConnectionString.append("/").append(dbName).append(noSSL).toString(),
                        dbUserName,
                        dbPassword);
            }
            System.out.println("Success!");

            // Adding tables to database
            Statement statement = connection.createStatement();

            // Creating users table
            String sqlQuery = "" +
                    "CREATE TABLE IF NOT EXISTS `" + dbName + "`.`users` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT, " +
                    "  `username` VARCHAR(45) NOT NULL, " +
                    "  `password` VARCHAR(45) NOT NULL, " +
                    "  `adresses` INT NOT NULL, " +
                    "  `birthday` DATE NULL, " +
                    "  PRIMARY KEY (`id`), " +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)) " +
                    "ENGINE = InnoDB " +
                    "DEFAULT CHARACTER SET = utf8;";
            System.out.println("Table 'users' created. Edited stings: " + statement.executeUpdate(sqlQuery));

            // Creating table water
            sqlQuery = "" +
                    "CREATE TABLE IF NOT EXISTS `" + dbName + "`.`water` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT, " +
                    "  `date` DATE NULL COMMENT 'дата снятия показания счетчика', " +
                    "  `value` INT NULL COMMENT 'значение счетчика на момент снятия', " +
                    "  `user` INT NOT NULL COMMENT 'id пользователя', " +
                    "  `user_adress` INT NOT NULL COMMENT 'id адреса пользователя', " +
                    "  `created_at` DATETIME NOT NULL COMMENT 'время внесения записи в базу', " +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)) " +
                    "ENGINE = InnoDB " +
                    "DEFAULT CHARACTER SET = utf8;";
            System.out.println("Table 'water' created. Edited stings: " + statement.executeUpdate(sqlQuery));

            // Creating table electricity
            sqlQuery = "" +
                    "CREATE TABLE IF NOT EXISTS `" + dbName + "`.`electricity` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT, " +
                    "  `date` DATE NULL COMMENT 'дата снятия показания счетчика', " +
                    "  `value` INT NULL COMMENT 'значение счетчика на момент снятия', " +
                    "  `date_plan` DATE NULL COMMENT 'дата прогнозного значения, выставленного в квитанции', " +
                    "  `value_plan` INT NULL COMMENT 'прогнозное значение счетчика указанное в квитанции', " +
                    "  `user` INT NOT NULL COMMENT 'id пользователя', " +
                    "  `user_adress` INT NOT NULL COMMENT 'id адреса пользователя', " +
                    "  `created_at` DATETIME NOT NULL COMMENT 'время внесения записи в базу', " +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)) " +
                    "ENGINE = InnoDB " +
                    "DEFAULT CHARACTER SET = utf8;";
            System.out.println("Table 'electricity' created. Edited stings: " + statement.executeUpdate(sqlQuery));

            // Creating table gas
            sqlQuery = "" +
                    "CREATE TABLE IF NOT EXISTS `" + dbName + "`.`gas` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT, " +
                    "  `date` DATE NULL COMMENT 'дата снятия показания счетчика', " +
                    "  `value` INT NULL COMMENT 'значение счетчика на момент снятия', " +
                    "  `user` INT NOT NULL COMMENT 'id пользователя', " +
                    "  `user_adress` INT NOT NULL COMMENT 'id адреса пользователя', " +
                    "  `created_at` DATETIME NOT NULL COMMENT 'время внесения записи в базу', " +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `id_UNIQUE` (`id` ASC)) " +
                    "ENGINE = InnoDB " +
                    "DEFAULT CHARACTER SET = utf8;";
            System.out.println("Table 'gas' created. Edited stings: " + statement.executeUpdate(sqlQuery));

            statement.close();
            connection.close();

        } catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            // e.printStackTrace();
            System.err.println("Sorry, can't connect");
            System.err.println(e.getMessage());
            System.exit(1);     // нет смысла продолжать. выходим.
        }

        // Preparing properties
        Properties properties = new Properties();
        properties.setProperty("dbType", dbType);
        properties.setProperty("dbHost", dbHost);
        properties.setProperty("dbPort", dbPort);
        properties.setProperty("dbName", dbName);
        properties.setProperty("dbUser", dbUserName);
        properties.setProperty("dbPass", dbPassword);
        properties.setProperty("JDBCDriver", driverClassName);
        // Storing properties
        try (FileOutputStream outputStream = new FileOutputStream(settingsPath);) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
