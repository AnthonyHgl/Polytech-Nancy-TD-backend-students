package service;

import com.example.todoapp.Task;
import dao.TaskDao;
import dto.CreateTaskDTO;
import dto.TaskResponseDTO;
import dto.UpdateTaskDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskService {

    private static final TaskDao dao = new TaskDao();

    // ─── Mapping helpers ──────────────────────────────────────────────────────

    private TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
                task.id(),
                task.title(),
                task.description(),
                task.done()
        );
    }

    // ─── Endpoints ────────────────────────────────────────────────────────────

    /**
     * Crée une tâche à partir du DTO d'entrée.
     * L'id est autogénéré par SQLite ; done est initialisé à false.
     */
    public TaskResponseDTO save(CreateTaskDTO input) {
        // id=0 → SQLite génère l'id automatiquement (AUTOINCREMENT)
        Task task = new Task(0, input.title(), input.description(), false);
        Task saved = dao.save(task);
        return toDTO(saved);
    }

    public Optional<TaskResponseDTO> findByID(int id) {
        return dao.findById(id).map(this::toDTO);
    }

    public List<TaskResponseDTO> findAll() {
        return dao.findall().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public int remove(int id) {
        return dao.remove(id);
    }

    /**
     * Met à jour une tâche à partir du DTO de modification.
     */
    public Optional<TaskResponseDTO> modif(int id, UpdateTaskDTO input) {
        Task task = new Task(id, input.title(), input.description(), input.done());
        return dao.modif(id, task).map(this::toDTO);
    }

    public List<TaskResponseDTO> remove_all() {
        return dao.remove_all().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public int count() {
        return dao.count();
    }
}