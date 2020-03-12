package hipravin.samples.loader;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class EmployeeDao {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String INSERT_EMPLOYEE_QUERY =
            "INSERT INTO employee (email, is_active) VALUES (:email, :active)";

    public static final String CLEAR_AUTHORITIES_QUERY =
            "DELETE FROM EMPLOYEE_AUTHORITY WHERE EMAIL=:mail ";

    public static final String DROP_EMPLOYEE =
            "DELETE FROM EMPLOYEE WHERE EMAIL=:mail ";

    public static final String INSERT_EMPLOYEE_AUTHORITIES_QUERY =
            "INSERT INTO EMPLOYEE_AUTHORITY (EMAIL, AUTHORITY) VALUES (:email, :authority)";

    public static final String SELECT_EMPLOYEE_AUTH_QUERY =
            "SELECT E.EMAIL, E.IS_ACTIVE, A.AUTHORITY FROM EMPLOYEE E " +
                    " JOIN EMPLOYEE_AUTHORITY A ON E.EMAIL=A.EMAIL";

    public static final String FIND_EMPLOYEE_AUTH_QUERY =
            "SELECT E.EMAIL, E.IS_ACTIVE, A.AUTHORITY FROM EMPLOYEE E " +
                    " JOIN EMPLOYEE_AUTHORITY A ON E.EMAIL=A.EMAIL WHERE E.EMAIL=:mail";

    public EmployeeDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void save(Employee employee) {
        namedParameterJdbcTemplate.update(CLEAR_AUTHORITIES_QUERY, Collections.singletonMap("mail", employee.getEmail()));
        namedParameterJdbcTemplate.update(DROP_EMPLOYEE, Collections.singletonMap("mail", employee.getEmail()));

        namedParameterJdbcTemplate.update(INSERT_EMPLOYEE_QUERY,
                new MapSqlParameterSource()
                        .addValue("email", employee.getEmail())
                        .addValue("active", employee.isActive() ? 1 : 0));

        List<MapSqlParameterSource> batchAuthorities = employee.getAuthorities().stream()
                .map(a -> new MapSqlParameterSource()
                        .addValue("email", employee.getEmail())
                        .addValue("authority", a))
                .collect(Collectors.toList());

        namedParameterJdbcTemplate.batchUpdate(INSERT_EMPLOYEE_AUTHORITIES_QUERY,
                batchAuthorities.toArray(new MapSqlParameterSource[]{}));
    }

    public List<Employee> findAll() {
        Map<String, Employee> employees = new HashMap<>();
        namedParameterJdbcTemplate.query(SELECT_EMPLOYEE_AUTH_QUERY, rs -> {
            String email = rs.getString("EMAIL");
            long isActive = rs.getLong("IS_ACTIVE");
            String authority = rs.getString("AUTHORITY");

            employees.putIfAbsent(email, new Employee(email, isActive == 1, new ArrayList<>()));
            employees.get(email).getAuthorities().add(authority);
        });

        return new ArrayList<>(employees.values());
    }

    public Employee find(String employeeEmail) {
        Map<String, Employee> employees = new HashMap<>();

        namedParameterJdbcTemplate.query(FIND_EMPLOYEE_AUTH_QUERY, Collections.singletonMap("mail", employeeEmail), rs -> {
            String email = rs.getString("EMAIL");
            long isActive = rs.getLong("IS_ACTIVE");
            String authority = rs.getString("AUTHORITY");

            employees.putIfAbsent(email, new Employee(email, isActive == 1, new ArrayList<>()));
            employees.get(email).getAuthorities().add(authority);
        });

        if(!employees.isEmpty()) {
            return employees.values().iterator().next();
        } else {
            return null;
        }
    }
}
