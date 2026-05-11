package com.campusconnect.campusconnect.repository;

import com.campusconnect.campusconnect.model.Project;
import com.campusconnect.campusconnect.model.Task;
import com.campusconnect.campusconnect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProject(Project project);

    List<Task> findByProjectAndStatus(Project project, Task.Status status);

    List<Task> findByAssignedTo(User user);
}