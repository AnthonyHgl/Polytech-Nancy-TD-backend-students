package Service;

import com.example.todoapp.Task;
import dao.TaskDao;

import java.util.List;
import java.util.Optional;

public class TaskService {
    private static final TaskDao dao = new TaskDao();

    public  Task save(Task input){
        Task createdTask = dao.save(input);
        return createdTask;
    }

    public  Optional<Task> findByID(int id){
        Optional<Task> task = dao.findById(id);
        return task;
    }

    public  List<Task> findAll(){
        List<Task> task = dao.findall();
        return task;
    }

    public  int remove(int id){
        int task = dao.remove(id);
        return task;
    }

    public  Optional<Task> modif(int id, Task t){
        Optional<Task> task = dao.modif(id, t);
        return task;
    }

    public  List<Task> remove_all(){
        List<Task> task = dao.remove_all();
        return task;
    }

    public  int count(){
        int task = dao.count();
        return task;
    }

}
