package com.ninjaTurtles.agrisense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ninjaTurtles.agrisense.models.AccountBootstrapRequest;
import com.ninjaTurtles.agrisense.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {

    private ImageView btnBack, imgToggleRegPassword, imgToggleConfirmPassword;
    private EditText etFullName, etRegEmail, etMobileNumber, etFarmName, etRegPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private MaterialButton btnRegister;
    private TextView tvLoginLink, tvTermsText;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnBack = findViewById(R.id.btnBack);
        etFullName = findViewById(R.id.etFullName);
        etRegEmail = findViewById(R.id.etRegEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etFarmName = findViewById(R.id.etFarmName);
        etRegPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        imgToggleRegPassword = findViewById(R.id.imgToggleRegPassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        tvTermsText = findViewById(R.id.tvTermsText);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        mAuth = FirebaseAuth.getInstance();
        setupLabelAsterisks();

        // Back button navigation
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            });
        }

        // Toggle Reg Password visibility
        if (imgToggleRegPassword != null) {
            imgToggleRegPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPasswordVisible = !isPasswordVisible;
                    if (isPasswordVisible) {
                        etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    etRegPassword.setSelection(etRegPassword.getText().length());
                }
            });
        }

        // Toggle Confirm Password visibility
        if (imgToggleConfirmPassword != null) {
            imgToggleConfirmPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isConfirmPasswordVisible = !isConfirmPasswordVisible;
                    if (isConfirmPasswordVisible) {
                        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    etConfirmPassword.setSelection(etConfirmPassword.getText().length());
                }
            });
        }

        // Create Account Button Click
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationHelper.animateButtonPress(RegisterActivity.this, btnRegister);

                String fullName = etFullName != null ? etFullName.getText().toString().trim() : "";
                String email = etRegEmail != null ? etRegEmail.getText().toString().trim() : "";
                String mobile = etMobileNumber != null ? etMobileNumber.getText().toString().trim() : "";
                String farmName = etFarmName != null ? etFarmName.getText().toString().trim() : "";
                String password = etRegPassword != null ? etRegPassword.getText().toString().trim() : "";
                String confirmPassword = etConfirmPassword != null ? etConfirmPassword.getText().toString().trim() : "";

                if (TextUtils.isEmpty(fullName)) {
                    etFullName.setError("Full name is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etRegEmail.setError("Email is required");
                    return;
                }
                
                if (TextUtils.isEmpty(mobile)) {
                    etMobileNumber.setError("Mobile number is required");
                    return;
                }

                if (TextUtils.isEmpty(farmName)) {
                    etFarmName.setError("Farm name is required");
                    return;
                }
                
                if (TextUtils.isEmpty(password)) {
                    etRegPassword.setError("Password is required");
                    return;
                }
                
                if (password.length() < 6) {
                    etRegPassword.setError("Password must be >= 6 characters");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    etConfirmPassword.setError("Passwords do not match");
                    return;
                }

                if (cbTerms != null && !cbTerms.isChecked()) {
                    Toast.makeText(RegisterActivity.this, "Please agree to the Terms of Service & Privacy Policy", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnRegister.setEnabled(false);
                if (mAuth.getCurrentUser() != null) {
                    bootstrapAccount(fullName, farmName, mobile);
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user == null) { btnRegister.setEnabled(true); return; }
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fullName).build();
                                user.updateProfile(profile).addOnCompleteListener(profileTask ->
                                        bootstrapAccount(fullName, farmName, mobile));
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                btnRegister.setEnabled(true);
                            }
                        });
            }
        });

        // Log In link navigation
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void bootstrapAccount(String fullName, String farmName, String mobile) {
        btnRegister.setEnabled(false);
        ApiClient.getApiService().bootstrapAccount(new AccountBootstrapRequest(fullName, farmName, mobile))
                .enqueue(new Callback<com.google.gson.JsonObject>() {
                    @Override public void onResponse(Call<com.google.gson.JsonObject> call, Response<com.google.gson.JsonObject> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account and farm are ready.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            btnRegister.setText("RETRY FARM SETUP");
                            btnRegister.setEnabled(true);
                            Toast.makeText(RegisterActivity.this,
                                    "Account created, but farm setup failed (HTTP " + response.code() + "). Check the API and database, then tap retry.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override public void onFailure(Call<com.google.gson.JsonObject> call, Throwable error) {
                        btnRegister.setText("RETRY FARM SETUP");
                        btnRegister.setEnabled(true);
                        Toast.makeText(RegisterActivity.this,
                                "Account created, but farm setup could not reach the API. Check the connection, then tap retry.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupLabelAsterisks() {
        setRedAsterisk(R.id.lblFullName, "Full Name");
        setRedAsterisk(R.id.lblEmail, "Email Address");
        setRedAsterisk(R.id.lblMobile, "Mobile Number");
        setRedAsterisk(R.id.lblFarm, "Farm / Estate Name");
        setRedAsterisk(R.id.lblPassword, "Password");
        setRedAsterisk(R.id.lblConfirmPassword, "Confirm Password");

        if (tvTermsText != null) {
            tvTermsText.setText(Html.fromHtml("I agree to the <font color='#2E7D32'><b>Terms of Service</b></font> &amp; <font color='#2E7D32'><b>Privacy Policy</b></font>"));
        }
    }

    private void setRedAsterisk(int viewId, String label) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setText(Html.fromHtml(label + " <font color='#C62828'>*</font>"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
