package dto;

/**
 * DTO d'erreur retourné en cas de paramètre invalide (400)
 * ou d'erreur inattendue (500).
 */
public record ErrorDTO(
        String field,
        String message
) {}
