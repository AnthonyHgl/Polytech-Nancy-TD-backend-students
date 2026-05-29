package Service;

import com.example.todoapp.Task;
import dao.TaskDao;

import java.util.List;
import java.util.Optional;

public class TaskService {
    private static final TaskDao dao = new TaskDao();

    public  Optional<Task> Taskconsult(id){
        Optional<Task> task = dao.findById(id);
    }

    Task createdTask = dao.save(input);

    List<Task> task = dao.findall();
    int task = dao.remove(id);
    Optional<Task> task = dao.modif(id, t);
    List<Task> task = dao.remove_all();
    int task = dao.count();




}
