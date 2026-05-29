package dao;

import com.example.todoapp.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for {@link Task} model.
 * Backed by a SQLite database via JDBC.
 * L'id est autogénéré par SQLite (AUTOINCREMENT).
 */
public class TaskDao {

    private static final String DB_URL = "jdbc:sqlite:tasks.db";

    public TaskDao() {
        initTable();
        seedIfEmpty();
    }

    // ─── Initialisation ───────────────────────────────────────────────────────

    private void initTable() {
        // id en AUTOINCREMENT : SQLite génère l'id si on insère avec id=NULL
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    title       TEXT    NOT NULL,
                    description TEXT,
                    done        INTEGER NOT NULL DEFAULT 0
                );
                """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la table", e);
        }
    }

    private void seedIfEmpty() {
        if (count() == 0) {
            save(new Task(0, "Réviser DS de maths",       "Séries numériques et probabilités.", false));
            save(new Task(0, "Valider mon PIVE",           "PIVE Club Poker.",                  true));
            save(new Task(0, "Choisir mon parcours de 4A", "SIR ou SIA ?",                      false));
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private Task mapRow(ResultSet rs) throws SQLException {
        return new Task(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("done") == 1
        );
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Insère une tâche. Si task.id() == 0, SQLite génère l'id automatiquement.
     * Retourne la tâche avec son id généré.
     */
    public Task save(Task task) {
        String sql = "INSERT INTO tasks (title, description, done) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.title());
            ps.setString(2, task.description());
            ps.setInt   (3, task.done() ? 1 : 0);
            ps.executeUpdate();

            // Récupère l'id généré par SQLite
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    return new Task(generatedId, task.title(), task.description(), task.done());
                }
            }
            return task;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde de la tâche", e);
        }
    }

    public Optional<Task> findById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par id", e);
        }
        return Optional.empty();
    }

    public List<Task> findall() {
        String sql = "SELECT * FROM tasks";
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) tasks.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les tâches", e);
        }
        return tasks;
    }

    public int remove(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la tâche", e);
        }
    }

    public Optional<Task> modif(int id, Task task) {
        if (findById(id).isEmpty()) return Optional.empty();
        String sql = "UPDATE tasks SET title = ?, description = ?, done = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, task.title());
            ps.setString(2, task.description());
            ps.setInt   (3, task.done() ? 1 : 0);
            ps.setInt   (4, id);
            ps.executeUpdate();
            return findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification de la tâche", e);
        }
    }

    public List<Task> remove_all() {
        String sql = "DELETE FROM tasks";
        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de toutes les tâches", e);
        }
        return new ArrayList<>();
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Connection conn = getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des tâches", e);
        }
        return 0;
    }
}