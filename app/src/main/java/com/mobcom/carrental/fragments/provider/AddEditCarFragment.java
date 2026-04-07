package com.mobcom.carrental.fragments.provider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobcom.carrental.LocationBottomSheet;
import com.mobcom.carrental.R;
import com.mobcom.carrental.adapters.CarImageAdapter;
import com.mobcom.carrental.models.CarFormData;
import com.mobcom.carrental.models.ProviderCar;
import com.mobcom.carrental.utils.CarBrandData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddEditCarFragment extends Fragment {

    // Edit mode
    private ProviderCar existingCar = null;
    private boolean isEditMode = false;

    // Image picker
    private List<Uri> selectedImages = new ArrayList<>();
    private CarImageAdapter imageAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Views — dropdowns
    private AutoCompleteTextView actvBrand, actvModel, actvYear, actvCarType;
    private AutoCompleteTextView actvTransmission, actvFuelType, actvSeats;

    // Views — inputs
    private TextInputLayout tilCustomBrand, tilCustomModel;
    private TextInputEditText etCustomBrand, etCustomModel;
    private TextInputEditText etPrice, etPlate, etLocation;
    private TextInputEditText etOrNumber, etCrNumber;
    private SwitchMaterial switchAlwaysAvailable;
    private MaterialButton btnSubmit;
    private RecyclerView rvImages;
    private LinearLayout layoutAddPhoto;
    private TextView tvImageCount;

    private CarFormData formData = new CarFormData();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_car, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupToolbar(view);
        setupImagePicker();
        setupDropdowns();
        setupBrandModelLogic();
        setupLocationPicker();

        // Check if edit mode
        if (getArguments() != null) {
            existingCar = (ProviderCar) getArguments().getSerializable("car");
            if (existingCar != null) {
                isEditMode = true;
                populateForEdit();
            }
        }

        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) submitForm();
        });
    }

    private void bindViews(View view) {
        actvBrand            = view.findViewById(R.id.actvBrand);
        actvModel            = view.findViewById(R.id.actvModel);
        actvYear             = view.findViewById(R.id.actvYear);
        actvCarType          = view.findViewById(R.id.actvCarType);
        actvTransmission     = view.findViewById(R.id.actvTransmission);
        actvFuelType         = view.findViewById(R.id.actvFuelType);
        actvSeats            = view.findViewById(R.id.actvSeats);
        tilCustomBrand       = view.findViewById(R.id.tilCustomBrand);
        tilCustomModel       = view.findViewById(R.id.tilCustomModel);
        etCustomBrand        = view.findViewById(R.id.etCustomBrand);
        etCustomModel        = view.findViewById(R.id.etCustomModel);
        etPrice              = view.findViewById(R.id.etPrice);
        etPlate              = view.findViewById(R.id.etPlate);
        etLocation           = view.findViewById(R.id.etLocation);
        etOrNumber           = view.findViewById(R.id.etOrNumber);
        etCrNumber           = view.findViewById(R.id.etCrNumber);
        switchAlwaysAvailable= view.findViewById(R.id.switchAlwaysAvailable);
        btnSubmit            = view.findViewById(R.id.btnSubmit);
        rvImages             = view.findViewById(R.id.rvImages);
        layoutAddPhoto       = view.findViewById(R.id.layoutAddPhoto);
        tvImageCount         = view.findViewById(R.id.tvImageCount);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(isEditMode ? "Edit Listing" : "Add Car Listing");
        toolbar.setNavigationOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
    }

    // ── Image Picker ─────────────────────────────────────────────────────────

    private void setupImagePicker() {
        imageAdapter = new CarImageAdapter(requireContext(), selectedImages, position -> {
            selectedImages.remove(position);
            imageAdapter.notifyDataSetChanged();
            updateImageUI();
        });

        rvImages.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(imageAdapter);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            // Multiple images selected
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count && selectedImages.size() < 5; i++) {
                                selectedImages.add(data.getClipData().getItemAt(i).getUri());
                            }
                        } else if (data.getData() != null) {
                            // Single image
                            if (selectedImages.size() < 5) {
                                selectedImages.add(data.getData());
                            }
                        }
                        imageAdapter.notifyDataSetChanged();
                        updateImageUI();
                    }
                });

        layoutAddPhoto.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        if (selectedImages.size() >= 5) return;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Car Photos"));
    }

    private void updateImageUI() {
        int count = selectedImages.size();
        tvImageCount.setText(count + " / 5 photos added");
        if (count > 0) {
            rvImages.setVisibility(View.VISIBLE);
            layoutAddPhoto.setVisibility(count >= 5 ? View.GONE : View.VISIBLE);
        } else {
            rvImages.setVisibility(View.GONE);
            layoutAddPhoto.setVisibility(View.VISIBLE);
        }
    }

    // ── Dropdowns ────────────────────────────────────────────────────────────

    private void setupDropdowns() {
        // Brand
        setDropdown(actvBrand, CarBrandData.getBrands());

        // Year — generate from 2010 to current year
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int y = currentYear; y >= 2010; y--) {
            years.add(String.valueOf(y));
        }
        setDropdown(actvYear, years);

        // Static dropdowns
        setDropdown(actvCarType,      Arrays.asList("Sedan", "SUV", "Van", "Hatchback", "Pickup"));
        setDropdown(actvTransmission, Arrays.asList("Automatic", "Manual"));
        setDropdown(actvFuelType,     Arrays.asList("Gasoline", "Diesel", "Electric", "Hybrid"));
        setDropdown(actvSeats,        Arrays.asList("2", "4", "5", "7", "8"));
    }

    private void setDropdown(AutoCompleteTextView actv, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                items);
        actv.setAdapter(adapter);
    }

    // ── Brand → Model logic ──────────────────────────────────────────────────

    private void setupBrandModelLogic() {
        actvBrand.setOnItemClickListener((parent, view, position, id) -> {
            String selectedBrand = (String) parent.getItemAtPosition(position);
            formData.setBrand(selectedBrand);

            // Show/hide custom brand field
            boolean isOtherBrand = "Others".equals(selectedBrand);
            tilCustomBrand.setVisibility(isOtherBrand ? View.VISIBLE : View.GONE);

            // Update model dropdown
            actvModel.setText("");
            actvModel.setEnabled(!isOtherBrand);
            tilCustomModel.setVisibility(View.GONE);

            if (!isOtherBrand) {
                List<String> models = CarBrandData.getModelsForBrand(selectedBrand);
                setDropdown(actvModel, models);
                actvModel.setEnabled(true);
            }
        });

        actvModel.setOnItemClickListener((parent, view, position, id) -> {
            String selectedModel = (String) parent.getItemAtPosition(position);
            formData.setModel(selectedModel);
            tilCustomModel.setVisibility(
                    "Others".equals(selectedModel) ? View.VISIBLE : View.GONE);
        });
    }

    // ── Location ─────────────────────────────────────────────────────────────

    private void setupLocationPicker() {
        etLocation.setOnClickListener(v -> {
            openLocationBottomSheet();
        });

        TextInputLayout tilLocation = requireView().findViewById(R.id.tilLocation);
        if (tilLocation != null) {
            tilLocation.setEndIconOnClickListener(v -> openLocationBottomSheet());
        }
    }

    private void openLocationBottomSheet() {
        LocationBottomSheet sheet = new LocationBottomSheet();
        sheet.setOnLocationSelectedListener(location -> etLocation.setText(location));
        sheet.show(getParentFragmentManager(), "LocationBottomSheet");
    }

    // ── Edit mode population ─────────────────────────────────────────────────

    private void populateForEdit() {
        if (existingCar == null) return;

        actvBrand.setText(existingCar.getBrand(), false);
        actvModel.setText(existingCar.getModel(), false);
        actvYear.setText(String.valueOf(existingCar.getYear()), false);
        actvCarType.setText(existingCar.getCarType(), false);
        actvTransmission.setText(existingCar.getTransmission(), false);
        actvFuelType.setText(existingCar.getFuelType(), false);
        actvSeats.setText(String.valueOf(existingCar.getSeats()), false);
        etPrice.setText(String.valueOf(existingCar.getPricePerDay()));
        etPlate.setText(existingCar.getPlateNumber());
        etLocation.setText(existingCar.getLocation());
        btnSubmit.setText("Save Changes");
    }

    // ── Validation ───────────────────────────────────────────────────────────

    private boolean validateForm() {
        boolean valid = true;

        if (actvBrand.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) actvBrand.getParent().getParent())
                    .setError("Brand is required");
            valid = false;
        }

        if (actvModel.getText().toString().trim().isEmpty()
                && tilCustomModel.getVisibility() != View.VISIBLE) {
            ((TextInputLayout) actvModel.getParent().getParent())
                    .setError("Model is required");
            valid = false;
        }

        if (actvYear.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) actvYear.getParent().getParent())
                    .setError("Year is required");
            valid = false;
        }

        if (actvCarType.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) actvCarType.getParent().getParent())
                    .setError("Car type is required");
            valid = false;
        }

        if (actvTransmission.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) actvTransmission.getParent().getParent())
                    .setError("Transmission is required");
            valid = false;
        }

        if (actvFuelType.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) actvFuelType.getParent().getParent())
                    .setError("Fuel type is required");
            valid = false;
        }

        if (actvSeats.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) actvSeats.getParent().getParent())
                    .setError("Seats is required");
            valid = false;
        }

        if (etPrice.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) etPrice.getParent().getParent())
                    .setError("Price is required");
            valid = false;
        }

        if (etPlate.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) etPlate.getParent().getParent())
                    .setError("Plate number is required");
            valid = false;
        }

        if (etOrNumber.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) etOrNumber.getParent().getParent())
                    .setError("OR number is required");
            valid = false;
        }

        if (etCrNumber.getText().toString().trim().isEmpty()) {
            ((TextInputLayout) etCrNumber.getParent().getParent())
                    .setError("CR number is required");
            valid = false;
        }

        if (selectedImages.isEmpty()) {
            tvImageCount.setTextColor(
                    requireContext().getColor(android.R.color.holo_red_light));
            tvImageCount.setText("At least 1 photo is required");
            valid = false;
        }

        return valid;
    }

    // ── Submit ───────────────────────────────────────────────────────────────

    private void submitForm() {
        // Collect form data
        formData.setBrand(actvBrand.getText().toString());
        formData.setModel(actvModel.getText().toString());
        formData.setCustomModel(etCustomModel.getText().toString());
        formData.setCustomBrand(etCustomBrand.getText().toString());
        formData.setYear(Integer.parseInt(actvYear.getText().toString()));
        formData.setCarType(actvCarType.getText().toString());
        formData.setTransmission(actvTransmission.getText().toString());
        formData.setFuelType(actvFuelType.getText().toString());
        formData.setSeats(Integer.parseInt(actvSeats.getText().toString()));
        formData.setPricePerDay(Double.parseDouble(etPrice.getText().toString()));
        formData.setPlateNumber(etPlate.getText().toString());
        formData.setLocation(etLocation.getText().toString());
        formData.setOrNumber(etOrNumber.getText().toString());
        formData.setCrNumber(etCrNumber.getText().toString());
        formData.setAlwaysAvailable(switchAlwaysAvailable.isChecked());

        // TODO: send formData to API/backend

        // Show success and go back
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(isEditMode ? "Listing Updated!" : "Listing Submitted!")
                .setMessage(isEditMode
                        ? "Your car listing has been updated successfully."
                        : "Your car listing has been submitted for review. It will be visible to customers once approved.")
                .setPositiveButton("OK", (dialog, which) ->
                        requireActivity().getOnBackPressedDispatcher().onBackPressed())
                .show();
    }
}