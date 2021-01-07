package ru.educationalwork.employessjava.screens.employees;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.educationalwork.employessjava.R;
import ru.educationalwork.employessjava.adapters.EmployeeAdapter;
import ru.educationalwork.employessjava.pojo.Employee;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEmployees;
    private EmployeeAdapter adapter;
    private EmployeeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewEmployees = findViewById(R.id.recyclerViewEmployees);
        adapter = new EmployeeAdapter();
        adapter.setEmployees(new ArrayList<>());
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmployees.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(EmployeeViewModel.class);
        // успешная загрузка. Подпишемся на сотрудников
        viewModel.getEmployees().observe(this, new Observer<List<Employee>>() {
            // Каждый раз при изменении данных в БД вызывается onChanged
            @Override
            public void onChanged(List<Employee> employees) {
                adapter.setEmployees(employees);
            }
        });
        // неудача. Подпишемся на ошибки
        viewModel.getErrors().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                if (throwable != null) { // иначе будет бесконечно обновляться, и приложение зависнет
                    Toast.makeText(EmployeeListActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    viewModel.clearErrors();
                }
            }
        });

        viewModel.loadData();
    }

}