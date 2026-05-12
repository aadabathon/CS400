import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;



public class HelloWebServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000),0);
        HttpContext context = server.createContext("/test", exchange -> HelloWebServer.handle(exchange));
        //context.setHandler(exchange -> {
        //      System.out.println("Server Received HTTP Request");
        //      exchange.sendResponseHeaders(204,-1);
          //  });

        server.start();
        System.out.println("Hello Web Server Running...");
        }
        private static void handle(HttpExchange exchange) throws IOException {
                System.out.println("Request Recieved");
                String querie = exchange.getRequestURI().getQuery();
                String course_value = "";
                String name_value = "";
                Map<String, String> kv = new HashMap<>();
                if (querie != null && !querie.isEmpty()) {
                        for (String s : querie.split("&")) { 
                            String[] queries = s.split("=", 2);
                            kv.put(queries[0], queries[1]);
                        }            
        }
        String output = "Hello " + kv.get("name")  + "! <br/> I hope you are having a great " + java.time.LocalDate.now();
        byte[] bytes = output.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-type","text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);

         try (OutputStream os = exchange.getResponseBody()) {
           os.write(bytes);
        }
	System.out.println(output);
    }
}
