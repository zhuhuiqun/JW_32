package domain;

import java.io.Serializable;


public final class User implements Comparable<User>, Serializable {
    private Integer id;
    private String username;
    private String password;
    private Teacher teacher;

    public User(String username, String password, Teacher teacher) {
        this.username = username;
        this.password = password;
        this.teacher = teacher;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = this.teacher;
    }

    @Override
    public int compareTo(User o) {
        return 0;
    }
}
