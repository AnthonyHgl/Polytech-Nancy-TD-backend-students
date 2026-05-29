package dto;

/**
 * DTO de sortie retourné par tous les endpoints de lecture.
 * Les champs sont identiques au modèle Task.
 */
public record TaskResponseDTO(
        int id,
        String title,
        String description,
        boolean done
) {}
