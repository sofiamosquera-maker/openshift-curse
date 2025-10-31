import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // Crear el servidor en el puerto 3000
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);

        // Contexto principal (proxy)
        server.createContext("/", new ProxyHandler());

        // Endpoints de salud
        server.createContext("/health", new SimpleResponseHandler("OK"));
        server.createContext("/startup", new SimpleResponseHandler("OK"));
        server.createContext("/readiness", new SimpleResponseHandler("OK"));

        server.setExecutor(null); // Usa el executor por defecto
        server.start();

        System.out.println("Servidor iniciado en el puerto 3000");
    }

    // Handler para el proxy
    static class ProxyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String targetUrl = "mi-servicio-microservico-2.sofia-mosquera-dev.svc.cluster.local:4000/";
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            StringBuilder response = new StringBuilder();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
            }

            String result = "Respuesta del servicio A: " + response;
            exchange.sendResponseHeaders(status, result.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(result.getBytes());
            }
        }
    }

    // Handler genérico para responder "OK"
    static class SimpleResponseHandler implements HttpHandler {
        private final String message;

        public SimpleResponseHandler(String message) {
            this.message = message;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = message.getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}

