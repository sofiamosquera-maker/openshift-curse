import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;

public class ClienteProxy {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
        server.createContext("/", new ProxyHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Cliente iniciado en el puerto 4000");
    }

    static class ProxyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String targetUrl = "http://mi-servicio-second.sofia-mosquera-dev.svc.cluster.local:4000/";
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
            OutputStream os = exchange.getResponseBody();
            os.write(result.getBytes());
            os.close();
        }
    }
}
