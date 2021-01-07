package ru.educationalwork.employessjava.screens.employees;

import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.educationalwork.employessjava.api.ApiFactory;
import ru.educationalwork.employessjava.api.ApiService;
import ru.educationalwork.employessjava.pojo.EmployeeResponse;

public class EmployeeListPresenter {

    private Disposable disposable; // Чтобы принудительно закрыть api service при закрытии приложения. Чтобы не было утечки памяти
    private CompositeDisposable compositeDisposable; // Если disposable объектов будет много, то их проще добавить в такую группу
    private EmployeesListView view; // интерфейс от нашей активити

    public EmployeeListPresenter(EmployeesListView view) {
        this.view = view;
    }

    public void loadData() {
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
                        //adapter.setEmployees(employeeResponse.getResponse());
                        view.showData(employeeResponse.getResponse());
                    }
                }, new Consumer<Throwable>() {
                    // произошла ошибка
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //Toast.makeText(EmployeeListActivity.this, "Ошибка получения данных: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        view.showError(throwable);
                    }
                });

        compositeDisposable.add(disposable);
    }

    public void disposeDisposable() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
