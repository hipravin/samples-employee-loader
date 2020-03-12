package hipravin.samples.loader;

import java.util.List;

public class Employee {
    private String email;
    private boolean isActive;
    private List<String> authorities;

    public Employee() {
    }

    public Employee(String email, List<String> authorities) {
        this.email = email;
        this.authorities = authorities;
    }

    public Employee(String email, boolean isActive, List<String> authorities) {
        this.email = email;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
