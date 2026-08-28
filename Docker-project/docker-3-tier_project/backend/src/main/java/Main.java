import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.sun.net.httpserver.HttpServer;

public class Main {

    private static final String DB_URL =
            "jdbc:mysql://mysql:3306/myapp";

    private static final String DB_USERNAME = "root";

    public static void main(String[] args) throws Exception {

        // Read MySQL password from Docker Secret
        String dbPassword = Files.readString(
                Path.of("/run/secrets/mysql_root_password")
        ).trim();

        // Test MySQL connection before starting the server
        testDatabaseConnection(dbPassword);

        // Start HTTP server
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        // =========================
        // API Endpoint
        // =========================
        server.createContext("/api", exchange -> {

            String response;

            try {

                Connection connection =
                        DriverManager.getConnection(
                                DB_URL,
                                DB_USERNAME,
                                dbPassword
                        );

                Statement statement =
                        connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery("SELECT 1");

                int result = 0;

                if (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

                resultSet.close();
                statement.close();
                connection.close();

                response =
                        "Hello from Docker Backend!\n" +
                        "MySQL Status: Connected\n" +
                        "MySQL Response: " + result;

            } catch (Exception e) {

                response =
                        "MySQL Status: Connection Failed\n" +
                        e.getMessage();
            }

            exchange.getResponseHeaders()
                    .set("Content-Type", "text/plain");

            exchange.sendResponseHeaders(
                    200,
                    response.getBytes().length
            );

            OutputStream outputStream =
                    exchange.getResponseBody();

            outputStream.write(response.getBytes());

            outputStream.close();
        });


        // =========================
        // Health Endpoint
        // =========================
        server.createContext("/health", exchange -> {

            String response = "OK";

            exchange.getResponseHeaders()
                    .set("Content-Type", "text/plain");

            exchange.sendResponseHeaders(
                    200,
                    response.getBytes().length
            );

            OutputStream outputStream =
                    exchange.getResponseBody();

            outputStream.write(response.getBytes());

            outputStream.close();
        });


        server.start();

        System.out.println(
                "Backend server started on port 8080"
        );
    }


    // =========================
    // Database Connection Test
    // =========================
    private static void testDatabaseConnection(
            String password
    ) {

        try {

            Connection connection =
                    DriverManager.getConnection(
                            DB_URL,
                            DB_USERNAME,
                            password
                    );

            System.out.println(
                    "Connected to MySQL successfully!"
            );

            connection.close();

        } catch (Exception e) {

            System.out.println(
                    "Failed to connect to MySQL"
            );

            e.printStackTrace();

            System.exit(1);
        }
    }
}
