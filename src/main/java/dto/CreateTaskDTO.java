package dto;

/**
 * DTO d'entrée pour la création d'une tâche (POST /tasks).
 * L'id est autogénéré par SQLite, done est initialisé à false.
 */
public record CreateTaskDTO(
        String title,
        String description
) {}
