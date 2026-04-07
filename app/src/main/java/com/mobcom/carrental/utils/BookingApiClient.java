package com.mobcom.carrental.utils;

import androidx.annotation.NonNull;
import com.mobcom.carrental.BuildConfig;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class BookingApiClient {

    public interface CancelBookingCallback {
        void onSuccess();
        void onError(@NonNull String message);
    }

    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private BookingApiClient() {
    }

    public static void cancelBooking(@NonNull String bookingId,
                                     @NonNull CancelBookingCallback callback) {
        String baseUrl = BuildConfig.BOOKINGS_API_BASE_URL == null
                ? ""
                : BuildConfig.BOOKINGS_API_BASE_URL.trim();

        // Keep demo flows usable when backend URL isn't configured yet.
        if (baseUrl.isEmpty()) {
            callback.onSuccess();
            return;
        }

        String normalizedBase = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;

        String url = normalizedBase + "/bookings/" + bookingId + "/cancel";
        String payload = "{\"status\":\"CANCELLED\"}";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(payload, JSON))
                .build();

        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError("Unable to cancel booking. Please try again.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Cancellation failed (" + response.code() + ").");
                    }
                }
            }
        });
    }
}
