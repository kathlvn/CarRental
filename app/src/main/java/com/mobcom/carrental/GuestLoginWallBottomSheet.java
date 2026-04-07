package com.mobcom.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class GuestLoginWallBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SOURCE = "source";

    public static GuestLoginWallBottomSheet newInstance(String source) {
        GuestLoginWallBottomSheet sheet = new GuestLoginWallBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SOURCE, source);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_guest_login_wall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnLogin = view.findViewById(R.id.btnGuestWallLogin);
        MaterialButton btnSignUp = view.findViewById(R.id.btnGuestWallSignUp);
        TextView btnLater = view.findViewById(R.id.btnGuestWallLater);

        btnLogin.setOnClickListener(v -> openAuth(LoginActivity.MODE_LOGIN));
        btnSignUp.setOnClickListener(v -> openAuth(LoginActivity.MODE_SIGN_UP));
        btnLater.setOnClickListener(v -> dismiss());
    }

    private void openAuth(String mode) {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_AUTH_MODE, mode);
        intent.putExtra("source", getArguments() != null ? getArguments().getString(ARG_SOURCE, "") : "");
        startActivity(intent);
        dismiss();
    }
}
