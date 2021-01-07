package ru.educationalwork.employessjava.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Тут реализация интерфейса ApiService
public class ApiFactory {

    // Паттерн Singleton, чтобы быть уверенными, что мы всегда работаем с одной и той же реализацией
    private static ApiFactory apiFactory;
    private static Retrofit retrofit;

    private static final String BASE_URL = "https://gitlab.65apps.com/65gb/static/raw/master/";

    private ApiFactory() {

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()) // Каким образом преобразуем JSON в объект
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // Слушатель на процесс получения данных (успешно или нет)
                .baseUrl(BASE_URL)
                .build();
    }

    public static ApiFactory getInstance() {
        if (apiFactory == null) {
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }


}
