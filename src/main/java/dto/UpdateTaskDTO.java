package dto;

/**
 * DTO d'entrée pour la modification d'une tâche (PUT /tasks/{id}).
 */
public record UpdateTaskDTO(
        String title,
        String description,
        Boolean done
) {}
