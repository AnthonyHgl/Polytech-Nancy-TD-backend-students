package presentation;

import dto.CreateTaskDTO;
import dto.ErrorDTO;
import dto.UpdateTaskDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Valide les DTO d'entrée dans la couche Présentation.
 * Retourne une liste d'ErrorDTO (vide si tout est valide).
 */
public class TaskValidator {

    private static final int MAX_TITLE_LENGTH       = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 255;

    /**
     * Valide le DTO de création.
     * - title  : obligatoire, max 50 caractères
     * - description : optionnelle, max 255 caractères
     */
    public static List<ErrorDTO> validate(CreateTaskDTO dto) {
        List<ErrorDTO> errors = new ArrayList<>();

        if (dto == null) {
            errors.add(new ErrorDTO("body", "Le corps de la requête est manquant."));
            return errors;
        }

        // title
        if (dto.title() == null || dto.title().isBlank()) {
            errors.add(new ErrorDTO("title", "Le titre est obligatoire."));
        } else if (dto.title().length() > MAX_TITLE_LENGTH) {
            errors.add(new ErrorDTO("title",
                    "Le titre ne doit pas dépasser " + MAX_TITLE_LENGTH + " caractères."));
        }

        // description (optionnelle mais bornée)
        if (dto.description() != null && dto.description().length() > MAX_DESCRIPTION_LENGTH) {
            errors.add(new ErrorDTO("description",
                    "La description ne doit pas dépasser " + MAX_DESCRIPTION_LENGTH + " caractères."));
        }

        return errors;
    }

    /**
     * Valide le DTO de modification.
     * - title  : obligatoire, max 50 caractères
     * - description : optionnelle, max 255 caractères
     * - done   : obligatoire
     */
    public static List<ErrorDTO> validate(UpdateTaskDTO dto) {
        List<ErrorDTO> errors = new ArrayList<>();

        if (dto == null) {
            errors.add(new ErrorDTO("body", "Le corps de la requête est manquant."));
            return errors;
        }

        // title
        if (dto.title() == null || dto.title().isBlank()) {
            errors.add(new ErrorDTO("title", "Le titre est obligatoire."));
        } else if (dto.title().length() > MAX_TITLE_LENGTH) {
            errors.add(new ErrorDTO("title",
                    "Le titre ne doit pas dépasser " + MAX_TITLE_LENGTH + " caractères."));
        }

        // description (optionnelle mais bornée)
        if (dto.description() != null && dto.description().length() > MAX_DESCRIPTION_LENGTH) {
            errors.add(new ErrorDTO("description",
                    "La description ne doit pas dépasser " + MAX_DESCRIPTION_LENGTH + " caractères."));
        }

        // done
        if (dto.done() == null) {
            errors.add(new ErrorDTO("done", "Le statut 'done' est obligatoire."));
        }

        return errors;
    }
}
