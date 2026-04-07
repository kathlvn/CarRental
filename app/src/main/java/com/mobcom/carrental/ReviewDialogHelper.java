package com.mobcom.carrental;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.mobcom.carrental.models.RentalReview;

public final class ReviewDialogHelper {

    public interface OnReviewSubmittedListener {
        void onSubmitted(RentalReview review);
    }

    private ReviewDialogHelper() {
    }

    public static void show(Context context,
                            String carName,
                            OnReviewSubmittedListener listener) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_rating_review, null, false);

        RatingBar ratingProvider = dialogView.findViewById(R.id.ratingProvider);
        RatingBar ratingCar = dialogView.findViewById(R.id.ratingCar);
        TextInputEditText etComment = dialogView.findViewById(R.id.etComment);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setTitle("Rate your rental")
                .setMessage(carName)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    int providerRating = Math.round(ratingProvider.getRating());
                    int carRating = Math.round(ratingCar.getRating());
                    String comment = etComment.getText() == null
                            ? ""
                            : etComment.getText().toString().trim();

                    if (providerRating < 1 || carRating < 1) {
                        Toast.makeText(context,
                                "Please rate both provider and car (1-5 stars)",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (comment.isEmpty()) {
                        etComment.setError("Comment is required");
                        return;
                    }

                    if (listener != null) {
                        listener.onSubmitted(new RentalReview(
                                providerRating,
                                carRating,
                                comment,
                                System.currentTimeMillis()
                        ));
                    }
                    dialog.dismiss();
                }));

        dialog.show();
    }
}