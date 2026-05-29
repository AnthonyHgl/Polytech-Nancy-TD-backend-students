package dao;

import com.example.todoapp.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDao {

    // Le fichier SQLite sera créé dans le répertoire courant
    private static final String DB_URL = "jdbc:sqlite:tasks.db";

    public TaskDao() {
        initTable();
        seedIfEmpty();
    }
    private void initTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id      INTEGER PRIMARY KEY,
                    title   TEXT    NOT NULL,
                    description TEXT,
                    done    INTEGER NOT NULL DEFAULT 0
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
            save(new Task(1, "Réviser DS de maths",  "Séries numériques et probabilités.", false));
            save(new Task(2, "Valider mon PIVE",      "PIVE Club Poker.",                  true));
            save(new Task(3, "Choisir mon parcours de 4A", "SIR ou SIA ?",                false));
        }
    }


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

    public Task save(Task task) {
        String sql = "INSERT OR REPLACE INTO tasks (id, title, description, done) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, task.id());
            ps.setString(2, task.title());
            ps.setString(3, task.description());
            ps.setInt   (4, task.done() ? 1 : 0);
            ps.executeUpdate();
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
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
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
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
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
            return ps.executeUpdate(); // renvoie le nombre de lignes affectées
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la tâche", e);
        }
    }

    public Optional<Task> modif(int id, Task task) {
        // Vérifie d'abord que la tâche existe
        if (findById(id).isEmpty()) {
            return Optional.empty();
        }
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
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de toutes les tâches", e);
        }
        return new ArrayList<>();
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM tasks";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des tâches", e);
        }
        return 0;
    }
}