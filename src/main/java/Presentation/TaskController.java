package Presentation;

import Service.TaskService;
import com.example.todoapp.JsonUtils;
import com.example.todoapp.Task;
import com.sun.net.httpserver.HttpExchange;
import dao.TaskDao;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;


public class    TaskController {

    private static final Pattern ID_PATH = Pattern.compile("^/tasks/([0-9]+)$");
    private static final TaskService service = new TaskService();

    public static void handleTasks(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        //region Manage POST /tasks
        if ("POST".equals(method) && "/tasks".equals(path)) {
            Task input = JsonUtils.deserialize(new String(exchange.getRequestBody().readAllBytes(), UTF_8), Task.class);
            Task createdTask = dao.save(input);

            exchange.getResponseHeaders().add("Location", "/tasks/" + createdTask.id());
            sendResponse(exchange, 201, JsonUtils.serialize(createdTask));
            return;
        }
        //endregion

        //region Manage GET /tasks/{id}
        Matcher m = ID_PATH.matcher(path);

        if ("GET".equals(method) && m.matches()) {
            int id = Integer.parseInt(m.group(1));
            Optional<Task> task = dao.findById(id);

            if (task.isPresent()) {
                sendResponse(exchange, 200, JsonUtils.serialize(task.get()));
            } else {
                sendResponse(exchange, 404, null);
            }
            return;
        }
        //endregion

        if ("GET".equals(method) && "/tasks".equals(path)) {
            List<Task> task = dao.findall();

            if (!task.isEmpty()) {
                sendResponse(exchange, 200, JsonUtils.serialize(task));
            } else {
                sendResponse(exchange, 404, null);
            }
            return;
        }
        if ("DELETE".equals(method) && m.matches()) {
            int id = Integer.parseInt(m.group(1));
            int task = dao.remove(id);

            if (task == 1) {
                sendResponse(exchange, 204, null);
            } else {
                sendResponse(exchange, 404, null);
            }
            return;
        }
        if ("PUT".equals(method) && m.matches()) {
            int id = Integer.parseInt(m.group(1));
            String body = new String(exchange.getRequestBody().readAllBytes());
            Task t = JsonUtils.deserialize(body, Task.class);
            Optional<Task> task = dao.modif(id, t);
            if (task.isPresent()) {
                sendResponse(exchange, 204, null);
            } else {
                sendResponse(exchange, 404, null);
            }
            return;
        }
        if ("DELETE".equals(method) && "/tasks".equals(path)) {
            List<Task> task = dao.remove_all();

            if (task.isEmpty()) {
                sendResponse(exchange, 204, null);
            } else {
                sendResponse(exchange, 404, null);
            }
            return;
        }
        if ("GET".equals(method) && "/tasks/count".equals(path)) {
            int task = dao.count();

            if (task != 0 ) {
                sendResponse(exchange, 200,JsonUtils.serialize(task) );
            } else {
                sendResponse(exchange, 404, JsonUtils.serialize(0));
            }
            return;
        }






        // Otherwise → 404
        sendResponse(exchange, 404, null);
    }

    private static void sendResponse(HttpExchange exchange, int status, String json) throws IOException {
        if(nonNull(json)) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            byte[] bytes = json.getBytes(UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } else {
            exchange.sendResponseHeaders(status, 0);
            exchange.close();
        }
    }
}
