package hipravin.samples.loader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeAuthoritiesFeedLoader {
    private final EmployeeDao employeeDao;

    @Value("${loader.feedresource}")
    private Resource feed;

    public EmployeeAuthoritiesFeedLoader(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 5 * 60 * 1000)
    public void loadAll() throws IOException {
        if(feed.exists() && feed.isFile()) {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(feed.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    parseLine(line).ifPresent(employeeDao::save);
                }
            }
        }
    }

    private Optional<Employee> parseLine(String line) {
        String[] parts = line.split(",");
        if(parts.length == 2) {
            return Optional.of(
                    new Employee(parts[0], parseAuthorities(parts[1])));
        } else {
            return Optional.empty();
        }
    }

    List<String> parseAuthorities(String authoritiesString) {
        return Arrays.stream(authoritiesString.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
