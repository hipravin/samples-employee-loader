package hipravin.samples.loader;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeService {
    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @GetMapping("/all")
    List<Employee> findAll() {
        return employeeDao.findAll();
    }

    @GetMapping("/search")
    Employee findAll(@RequestParam("mail") String mail) {
        return employeeDao.find(mail);
    }

    @GetMapping("/is-authorized")
    boolean isAuthorized(@RequestParam("mail") String mail, @RequestParam("authority") String authority) {
        Employee e = employeeDao.find(mail);
        return e != null && e.getAuthorities().contains(authority);
    }
}
