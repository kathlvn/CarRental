// com/mobcom/carrental/LoginActivity.java
package com.mobcom.carrental;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobcom.carrental.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_AUTH_MODE = "authMode";
    public static final String MODE_LOGIN = "login";
    public static final String MODE_SIGN_UP = "signup";

    // Mode
    private boolean isRegisterMode = false;
    private String selectedRole = SessionManager.ROLE_CUSTOMER;

    // Views
    private TabLayout tabAuthMode;
    private LinearLayout layoutLoginFields, layoutRegisterFields;
    private LinearLayout layoutRoleCustomer, layoutRoleProvider;
    private MaterialButton btnPrimaryAction;

    // Login fields
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;

    // Register fields
    private TextInputLayout tilFullName, tilRegEmail, tilPhone, tilRegPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etRegEmail, etPhone, etRegPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();
        setupTabs();
        setupRoleSelector();

        String authMode = getIntent().getStringExtra(EXTRA_AUTH_MODE);
        if (MODE_SIGN_UP.equalsIgnoreCase(authMode)) {
            setAuthMode(true);
        } else {
            setAuthMode(false);
        }

        btnPrimaryAction.setOnClickListener(v -> {
            if (isRegisterMode) handleRegister();
            else handleLogin();
        });
    }

    private void bindViews() {
        tabAuthMode          = findViewById(R.id.tabAuthMode);
        layoutLoginFields    = findViewById(R.id.layoutLoginFields);
        layoutRegisterFields = findViewById(R.id.layoutRegisterFields);
        layoutRoleCustomer   = findViewById(R.id.layoutRoleCustomer);
        layoutRoleProvider   = findViewById(R.id.layoutRoleProvider);
        btnPrimaryAction     = findViewById(R.id.btnPrimaryAction);

        tilEmail             = findViewById(R.id.tilEmail);
        tilPassword          = findViewById(R.id.tilPassword);
        etEmail              = findViewById(R.id.etEmail);
        etPassword           = findViewById(R.id.etPassword);

        tilFullName          = findViewById(R.id.tilFullName);
        tilRegEmail          = findViewById(R.id.tilRegEmail);
        tilPhone             = findViewById(R.id.tilPhone);
        tilRegPassword       = findViewById(R.id.tilRegPassword);
        tilConfirmPassword   = findViewById(R.id.tilConfirmPassword);
        etFullName           = findViewById(R.id.etFullName);
        etRegEmail           = findViewById(R.id.etRegEmail);
        etPhone              = findViewById(R.id.etPhone);
        etRegPassword        = findViewById(R.id.etRegPassword);
        etConfirmPassword    = findViewById(R.id.etConfirmPassword);
    }

    private void setupTabs() {
        tabAuthMode.addTab(tabAuthMode.newTab().setText("Log In"));
        tabAuthMode.addTab(tabAuthMode.newTab().setText("Sign Up"));

        tabAuthMode.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setAuthMode(tab.getPosition() == 1);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setAuthMode(boolean registerMode) {
        isRegisterMode = registerMode;
        layoutLoginFields.setVisibility(isRegisterMode ? View.GONE : View.VISIBLE);
        layoutRegisterFields.setVisibility(isRegisterMode ? View.VISIBLE : View.GONE);
        btnPrimaryAction.setText(isRegisterMode ? "Create Account" : "Log In");

        int targetIndex = isRegisterMode ? 1 : 0;
        TabLayout.Tab target = tabAuthMode.getTabAt(targetIndex);
        if (target != null && tabAuthMode.getSelectedTabPosition() != targetIndex) {
            target.select();
        }
    }

    private void setupRoleSelector() {
        layoutRoleCustomer.setOnClickListener(v -> selectRole(SessionManager.ROLE_CUSTOMER));
        layoutRoleProvider.setOnClickListener(v -> selectRole(SessionManager.ROLE_PROVIDER));
    }

    private void selectRole(String role) {
        selectedRole = role;
        layoutRoleCustomer.setBackgroundResource(
                role.equals(SessionManager.ROLE_CUSTOMER)
                        ? R.drawable.bg_role_selected
                        : R.drawable.bg_role_unselected);
        layoutRoleProvider.setBackgroundResource(
                role.equals(SessionManager.ROLE_PROVIDER)
                        ? R.drawable.bg_role_selected
                        : R.drawable.bg_role_unselected);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    private void handleLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            return;
        }
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Local demo credentials for testing
        if (email.equals("customer@test.com") && password.equals("1234")) {
            new SessionManager(this).login("C001", "Juan dela Cruz", email, SessionManager.ROLE_CUSTOMER);
            goToCustomer();
        } else if (email.equals("provider@test.com") && password.equals("1234")) {
            new SessionManager(this).login("P001", "Maria Santos", email, SessionManager.ROLE_PROVIDER);
            goToProvider();
        } else if (email.equals("admin@test.com") && password.equals("1234")) {
            new SessionManager(this).login("A001", "Admin User", email, SessionManager.ROLE_ADMIN);
            goToAdmin();
        } else {
            tilPassword.setError("Invalid email or password");
        }
    }

    // ── Register ─────────────────────────────────────────────────────────────

    private void handleRegister() {
        String fullName         = etFullName.getText().toString().trim();
        String email            = etRegEmail.getText().toString().trim();
        String phone            = etPhone.getText().toString().trim();
        String password         = etRegPassword.getText().toString().trim();
        String confirmPassword  = etConfirmPassword.getText().toString().trim();

        // Validation
        boolean valid = true;
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            valid = false;
        } else tilFullName.setError(null);

        if (TextUtils.isEmpty(email)) {
            tilRegEmail.setError("Email is required");
            valid = false;
        } else tilRegEmail.setError(null);

        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("Phone number is required");
            valid = false;
        } else tilPhone.setError(null);

        if (TextUtils.isEmpty(password)) {
            tilRegPassword.setError("Password is required");
            valid = false;
        } else tilRegPassword.setError(null);

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        } else tilConfirmPassword.setError(null);

        if (!valid) return;

        // For now, simulate successful registration
        new SessionManager(this).login("NEW001", fullName, email, selectedRole);

        if (selectedRole.equals(SessionManager.ROLE_PROVIDER)) {
            goToProvider();
        } else {
            goToCustomer();
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    private void goToCustomer() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToProvider() {
        Intent intent = new Intent(this, ProviderMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAdmin() {
        Intent intent = new Intent(this, AdminMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}