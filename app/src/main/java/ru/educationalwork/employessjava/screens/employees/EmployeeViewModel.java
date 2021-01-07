package ru.educationalwork.employessjava.screens.employees;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.educationalwork.employessjava.api.ApiFactory;
import ru.educationalwork.employessjava.api.ApiService;
import ru.educationalwork.employessjava.data.AppDatabase;
import ru.educationalwork.employessjava.pojo.Employee;
import ru.educationalwork.employessjava.pojo.EmployeeResponse;

/**
 * Суть в том, что в этом примере нет интерактора. Всё в ViewModel.
 * Также тут всё грузится в БД из сети, а только потом из БД во View
 */
public class EmployeeViewModel extends AndroidViewModel {

    private static AppDatabase db;
    private LiveData<List<Employee>> employees;
    private CompositeDisposable compositeDisposable; // Если disposable объектов будет много, то их проще добавить в такую группу
    private MutableLiveData<Throwable> errors;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        employees = db.employeeDao().getAllEmployees();
        errors = new MutableLiveData<>();
    }

    public LiveData<List<Employee>> getEmployees() {
        return employees;
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

    // Вствка. Она должна быть выполнена в отдельном потоке --- применим AsyncTask
    @SuppressWarnings("unchecked")
    private void insertEmployees(List<Employee> employees) {
        new InsertEmployeesTask().execute(employees);
    }

    private static class InsertEmployeesTask extends AsyncTask<List<Employee>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Employee>... lists) {
            if (lists != null && lists.length > 0) {
                db.employeeDao().insertEmployees(lists[0]);
            }
            return null;
        }
    }

    // Удаление
    private void deleteAllEmployees() {
        new DeleteAllEmployeesTask().execute();
    }

    private static class DeleteAllEmployeesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.employeeDao().deleteAllEmployees();
            return null;
        }
    }


    public void loadData() {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();

        compositeDisposable = new CompositeDisposable();

        Disposable disposable = apiService.getEmployees() // возващет нам объект типа Observable
                .subscribeOn(Schedulers.io())    // метод показывает в каком потоке выполнять действия. Все обращения к БД или загрузка данных из сети делается в потоке Schedulers.io
                .observeOn(AndroidSchedulers.mainThread()) // в каком потоке принимаем данные. Обычно в главном
                .subscribe(new Consumer<EmployeeResponse>() { // Что делать, когда получим данные
                    // успешная загрузка
                    @Override
                    public void accept(EmployeeResponse employeeResponse) throws Exception {
                        deleteAllEmployees();
                        insertEmployees(employeeResponse.getEmployees());
                    }
                }, new Consumer<Throwable>() {
                    // произошла ошибка
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });

        compositeDisposable.add(disposable);
    }

    // Метод вызывается при уничтожении ViewModel
    @Override
    protected void onCleared() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        super.onCleared();
    }

    // Чтобы в Toast не показывалось каждый раз последнее сохранённое значение ошибки
    public void clearErrors() {
        errors.setValue(null);
    }
}
