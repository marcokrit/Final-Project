package findmygroup.demo.repository;

import findmygroup.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // เอาไว้หาตอน Login
}