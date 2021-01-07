package ru.educationalwork.employessjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.educationalwork.employessjava.adapters.EmployeeAdapter;
import ru.educationalwork.employessjava.api.ApiFactory;
import ru.educationalwork.employessjava.api.ApiService;
import ru.educationalwork.employessjava.pojo.Employee;
import ru.educationalwork.employessjava.pojo.EmployeeResponse;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEmployees;
    private EmployeeAdapter adapter;
    private Disposable disposable; // Чтобы принудительно закрыть api service при закрытии приложения. Чтобы не было утечки памяти
    private CompositeDisposable compositeDisposable; // Если disposable объектов будет много, то их проще добавить в такую группу

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewEmployees = findViewById(R.id.recyclerViewEmployees);
        adapter = new EmployeeAdapter();
        adapter.setEmployees(new ArrayList<>());
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmployees.setAdapter(adapter);

        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        compositeDisposable = new CompositeDisposable();
        disposable = apiService.getEmployees() // возващет нам объект типа Observable
                .subscribeOn(Schedulers.io())    // метод показывает в каком потоке выполнять действия. Все обращения к БД или загрузка данных из сети делается в потоке Schedulers.io
                .observeOn(AndroidSchedulers.mainThread()) // в каком потоке принимаем данные. Обычно в главном
                .subscribe(new Consumer<EmployeeResponse>() { // Что делать, когда получим данные
                    // успешная загрузка
                    @Override
                    public void accept(EmployeeResponse employeeResponse) throws Exception {
                        adapter.setEmployees(employeeResponse.getResponse());
                    }
                }, new Consumer<Throwable>() {
                    // произошла ошибка
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "Ошибка получения данных: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        super.onDestroy();

    }
}