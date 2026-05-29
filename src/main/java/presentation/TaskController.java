package presentation;

import service.TaskService;
import com.example.todoapp.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import dto.CreateTaskDTO;
import dto.ErrorDTO;
import dto.TaskResponseDTO;
import dto.UpdateTaskDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

public class TaskController {

    private static final Pattern ID_PATH = Pattern.compile("^/tasks/([0-9]+)$");
    private static final TaskService service = new TaskService();

    public static void handleTasks(HttpExchange exchange) throws IOException {
        try {
            dispatch(exchange);
        } catch (Exception e) {
            // Étape 4 : erreur inattendue → 500
            ErrorDTO error = new ErrorDTO("server", "Une erreur inattendue s'est produite : " + e.getMessage());
            sendResponse(exchange, 500, JsonUtils.serialize(error));
        }
    }

    // ─── Dispatch ─────────────────────────────────────────────────────────────

    private static void dispatch(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();
        Matcher m     = ID_PATH.matcher(path);

        // POST /tasks
        if ("POST".equals(method) && "/tasks".equals(path)) {
            handleCreate(exchange);
            return;
        }

        // GET /tasks/count  — doit être testé AVANT le pattern /{id}
        if ("GET".equals(method) && "/tasks/count".equals(path)) {
            handleCount(exchange);
            return;
        }

        // GET /tasks/{id}
        if ("GET".equals(method) && m.matches()) {
            handleFindById(exchange, Integer.parseInt(m.group(1)));
            return;
        }

        // GET /tasks
        if ("GET".equals(method) && "/tasks".equals(path)) {
            handleFindAll(exchange);
            return;
        }

        // DELETE /tasks/{id}  — réinitialiser le matcher
        m = ID_PATH.matcher(path);
        if ("DELETE".equals(method) && m.matches()) {
            handleDeleteById(exchange, Integer.parseInt(m.group(1)));
            return;
        }

        // PUT /tasks/{id}
        m = ID_PATH.matcher(path);
        if ("PUT".equals(method) && m.matches()) {
            handleUpdate(exchange, Integer.parseInt(m.group(1)));
            return;
        }

        // DELETE /tasks
        if ("DELETE".equals(method) && "/tasks".equals(path)) {
            handleDeleteAll(exchange);
            return;
        }

        sendResponse(exchange, 404, null);
    }

    // ─── Handlers ─────────────────────────────────────────────────────────────

    /** POST /tasks */
    private static void handleCreate(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        CreateTaskDTO input = JsonUtils.deserialize(body, CreateTaskDTO.class);

        // Validation
        List<ErrorDTO> errors = TaskValidator.validate(input);
        if (!errors.isEmpty()) {
            sendResponse(exchange, 400, JsonUtils.serialize(errors));
            return;
        }

        TaskResponseDTO created = service.save(input);
        exchange.getResponseHeaders().add("Location", "/tasks/" + created.id());
        sendResponse(exchange, 201, JsonUtils.serialize(created));
    }

    /** GET /tasks/{id} */
    private static void handleFindById(HttpExchange exchange, int id) throws IOException {
        Optional<TaskResponseDTO> task = service.findByID(id);
        if (task.isPresent()) {
            sendResponse(exchange, 200, JsonUtils.serialize(task.get()));
        } else {
            sendResponse(exchange, 404, null);
        }
    }

    /** GET /tasks */
    private static void handleFindAll(HttpExchange exchange) throws IOException {
        List<TaskResponseDTO> tasks = service.findAll();
        if (!tasks.isEmpty()) {
            sendResponse(exchange, 200, JsonUtils.serialize(tasks));
        } else {
            sendResponse(exchange, 404, null);
        }
    }

    /** DELETE /tasks/{id} */
    private static void handleDeleteById(HttpExchange exchange, int id) throws IOException {
        int deleted = service.remove(id);
        sendResponse(exchange, deleted == 1 ? 204 : 404, null);
    }

    /** PUT /tasks/{id} */
    private static void handleUpdate(HttpExchange exchange, int id) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), UTF_8);
        UpdateTaskDTO input = JsonUtils.deserialize(body, UpdateTaskDTO.class);

        // Validation
        List<ErrorDTO> errors = TaskValidator.validate(input);
        if (!errors.isEmpty()) {
            sendResponse(exchange, 400, JsonUtils.serialize(errors));
            return;
        }

        Optional<TaskResponseDTO> task = service.modif(id, input);
        sendResponse(exchange, task.isPresent() ? 204 : 404, null);
    }

    /** DELETE /tasks */
    private static void handleDeleteAll(HttpExchange exchange) throws IOException {
        service.remove_all();
        sendResponse(exchange, 204, null);
    }

    /** GET /tasks/count */
    private static void handleCount(HttpExchange exchange) throws IOException {
        int count = service.count();
        if (count != 0) {
            sendResponse(exchange, 200, JsonUtils.serialize(count));
        } else {
            sendResponse(exchange, 404, JsonUtils.serialize(0));
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private static void sendResponse(HttpExchange exchange, int status, String json) throws IOException {
        if (nonNull(json)) {
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