package com.mobcom.carrental;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationBottomSheet extends BottomSheetDialogFragment {

    public interface OnLocationSelectedListener {
        void onLocationSelected(String location);
    }

    private OnLocationSelectedListener listener;
    private List<LocationModels.Province> provinceList = new ArrayList<>();
    private List<LocationModels.Municipality> municipalityList = new ArrayList<>();
    private Spinner spinnerProvince, spinnerMunicipality, spinnerBarangay;
    private FusedLocationProviderClient fusedLocationClient;

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_location, container, false);

        spinnerProvince = view.findViewById(R.id.spinnerProvince);
        spinnerMunicipality = view.findViewById(R.id.spinnerMunicipality);
        spinnerBarangay = view.findViewById(R.id.spinnerBarangay);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        loadPsgcData();
        setupSpinners();

        view.findViewById(R.id.btnUseGps).setOnClickListener(v -> requestGps());
        view.findViewById(R.id.btnConfirmLocation).setOnClickListener(v -> confirmLocation());

        return view;
    }

    private void loadPsgcData() {
        try {
            InputStream is = requireContext().getAssets().open("psgc.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, LocationModels.Region>>() {}.getType();
            Map<String, LocationModels.Region> regionMap = gson.fromJson(json, type);

            // Flatten all provinces from all regions into one list
            provinceList = new ArrayList<>();
            for (LocationModels.Region region : regionMap.values()) {
                if (region.province_list != null) {
                    for (Map.Entry<String, LocationModels.Province> entry : region.province_list.entrySet()) {
                        LocationModels.Province province = entry.getValue();
                        province.province_name = entry.getKey();
                        provinceList.add(province);
                    }
                }
            }

        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to load location data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinners() {
        // Province spinner
        List<String> provinceNames = new ArrayList<>();
        provinceNames.add("Select Province");
        for (LocationModels.Province p : provinceList) {
            provinceNames.add(p.province_name);
        }

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, provinceNames);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                loadMunicipalities(position - 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMunicipality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                loadBarangays(position - 1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadMunicipalities(int provinceIndex) {
        LocationModels.Province province = provinceList.get(provinceIndex);
        List<String> municipalityNames = new ArrayList<>();
        municipalityNames.add("Select City/Municipality");

        municipalityList = new ArrayList<>();
        for (Map.Entry<String, LocationModels.Municipality> entry : province.municipality_list.entrySet()) {
            LocationModels.Municipality municipality = entry.getValue();
            municipality.municipality_name = entry.getKey();
            municipalityList.add(municipality);
            municipalityNames.add(municipality.municipality_name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, municipalityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipality.setAdapter(adapter);
        spinnerMunicipality.setEnabled(true);

        // Reset barangay
        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerBarangay.setAdapter(emptyAdapter);
        spinnerBarangay.setEnabled(false);
    }

    private void loadBarangays(int municipalityIndex) {
        LocationModels.Municipality municipality = municipalityList.get(municipalityIndex);
        List<String> barangayNames = new ArrayList<>();
        barangayNames.add("Select Barangay (optional)");
        barangayNames.addAll(municipality.barangay_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, barangayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBarangay.setAdapter(adapter);
        spinnerBarangay.setEnabled(true);
    }

    private void confirmLocation() {
        int provincePos = spinnerProvince.getSelectedItemPosition();
        int municipalityPos = spinnerMunicipality.getSelectedItemPosition();

        if (provincePos == 0) {
            Toast.makeText(getContext(), "Please select a province.", Toast.LENGTH_SHORT).show();
            return;
        }

        String province = spinnerProvince.getSelectedItem().toString();
        String municipality = municipalityPos > 0 ? spinnerMunicipality.getSelectedItem().toString() : "";
        int barangayPos = spinnerBarangay.getSelectedItemPosition();
        String barangay = (barangayPos > 0 && spinnerBarangay.isEnabled()) ? spinnerBarangay.getSelectedItem().toString() : "";

        String location = "";
        if (!barangay.isEmpty()) location = barangay + ", ";
        if (!municipality.isEmpty()) location += municipality + ", ";
        location += province;

        if (listener != null) listener.onLocationSelected(location);
        dismiss();
    }

    private void requestGps() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }
        getGpsLocation();
    }

    private void getGpsLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                resolveLocationName(location);
            } else {
                Toast.makeText(getContext(), "Could not get location. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resolveLocationName(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality() != null ? address.getLocality() : address.getSubAdminArea();
                String result = city != null ? city : "Current Location";
                if (listener != null) listener.onLocationSelected(result);
                dismiss();
            }
        } catch (IOException e) {
            Toast.makeText(getContext(), "Could not resolve location.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getGpsLocation();
        }
    }
}