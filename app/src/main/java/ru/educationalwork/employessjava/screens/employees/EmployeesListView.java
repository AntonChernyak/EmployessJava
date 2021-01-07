package ru.educationalwork.employessjava.screens.employees;

import java.util.List;

import ru.educationalwork.employessjava.pojo.Employee;

public interface EmployeesListView {
    void showData(List<Employee> employees);
    void showError(Throwable throwable);
}


