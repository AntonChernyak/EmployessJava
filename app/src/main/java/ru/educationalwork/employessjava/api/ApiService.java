package ru.educationalwork.employessjava.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import ru.educationalwork.employessjava.pojo.EmployeeResponse;


// Запросы на сайт
public interface ApiService {

    // Observable из пакета rx, чтобы следить за данными
    @GET("testTask.json") // testTask.json --- конечная точка
    Observable<EmployeeResponse> getEmployees();
}
