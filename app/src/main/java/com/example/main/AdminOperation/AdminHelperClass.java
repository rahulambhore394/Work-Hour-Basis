package com.example.main.AdminOperation;

public class AdminHelperClass {

    String name;
    String email;
    String username;
    String password;
    String number;
    public String practical,theory;

    public String getPractical() {
        return practical;
    }

    public void setPractical(String practical) {
        this.practical = practical;
    }

    public String getTheory() {
        return theory;
    }

    public void setTheory(String theory) {
        this.theory = theory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {this.number = number;}

    public AdminHelperClass(String name, String email, String username, String password, String number) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.number=number;

    }
    public AdminHelperClass() {
    }

    public String salary_th,salary_pr;

    public AdminHelperClass(String salary_th, String salary_pr) {
        this.salary_th = salary_th;
        this.salary_pr = salary_pr;
    }

    public String getSalary_th() {
        return salary_th;
    }

    public void setSalary_th(String salary_th) {
        this.salary_th = salary_th;
    }

    public String getSalary_pr() {
        return salary_pr;
    }

    public void setSalary_pr(String salary_pr) {
        this.salary_pr = salary_pr;
    }
}
