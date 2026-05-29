package Service;

import com.example.todoapp.Task;
import dao.TaskDao;

import java.util.List;
import java.util.Optional;

public class TaskService {
    private static final TaskDao dao = new TaskDao();

    public  Task save(Task input){
        return dao.save(input);
    }

    public  Optional<Task> findByID(int id){
        return dao.findById(id);
    }

    public  List<Task> findAll(){
        return dao.findall();
    }

    public  int remove(int id){
        return dao.remove(id);
    }

    public  Optional<Task> modif(int id, Task t){
        return dao.modif(id, t);
    }

    public  List<Task> remove_all(){
        return dao.remove_all();
    }

    public  int count(){
        return dao.count();
    }

}
