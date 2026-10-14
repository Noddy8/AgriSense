package com.ninjaTurtles.agrisense.network;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    // Override at build time with -PAGRISENSE_API_BASE_URL=https://your-api-host/api/.
    private static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static Retrofit retrofit;
    private ApiClient() { }

    public static AgriApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        okhttp3.Request.Builder request = chain.request().newBuilder()
                                .header("Accept", "application/json");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            try {
                                String token = Tasks.await(user.getIdToken(false), 15, TimeUnit.SECONDS).getToken();
                                if (token == null || token.isEmpty()) throw new IOException("Firebase returned an empty ID token.");
                                request.header("Authorization", "Bearer " + token);
                            } catch (Exception e) {
                                throw new IOException("Unable to obtain the Firebase sign-in token.", e);
                            }
                        }
                        return chain.proceed(request.build());
                    })
                    .authenticator((route, response) -> {
                        // A Firebase ID token lasts about an hour. If the API rejects a cached token,
                        // force one refresh and retry once before asking the user to sign in again.
                        if (responseCount(response) >= 2) return null;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String previousAuthorization = response.request().header("Authorization");
                        if (user == null || previousAuthorization == null) return null;
                        try {
                            String refreshedToken = Tasks.await(user.getIdToken(true), 15, TimeUnit.SECONDS).getToken();
                            String refreshedAuthorization = refreshedToken == null ? null : "Bearer " + refreshedToken;
                            if (refreshedAuthorization == null || refreshedAuthorization.equals(previousAuthorization)) return null;
                            return response.request().newBuilder()
                                    .header("Authorization", refreshedAuthorization)
                                    .build();
                        } catch (Exception ignored) {
                            return null;
                        }
                    })
                    .addInterceptor(interceptor)
                    .build();
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(AgriApiService.class);
    }

    private static int responseCount(okhttp3.Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) count++;
        return count;
    }
}
