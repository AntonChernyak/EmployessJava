package ru.educationalwork.employessjava.screens.employees;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.educationalwork.employessjava.R;
import ru.educationalwork.employessjava.adapters.EmployeeAdapter;
import ru.educationalwork.employessjava.pojo.Employee;

public class EmployeeListActivity extends AppCompatActivity implements EmployeesListView {

    private RecyclerView recyclerViewEmployees;
    private EmployeeAdapter adapter;
    private EmployeeListPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewEmployees = findViewById(R.id.recyclerViewEmployees);
        adapter = new EmployeeAdapter();
        adapter.setEmployees(new ArrayList<>());
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmployees.setAdapter(adapter);

        presenter = new EmployeeListPresenter(this);
        presenter.loadData();
    }

    @Override
    protected void onDestroy() {
        presenter.disposeDisposable();
        super.onDestroy();
    }

    /**
     * Далее два метода, которые пойдут в Presenter, т.к. в нём не может быть View. Чтобы не нарушать инкаплуляцию
     * в presentor передаём не напрямую через ссылку на активити, а через интерфейс.
     */
    @Override
    public void showData(List<Employee> employees){
        adapter.setEmployees(employees);
    }

    @Override
    public void showError(Throwable throwable) {
        Toast.makeText(this, "Ошибка: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}