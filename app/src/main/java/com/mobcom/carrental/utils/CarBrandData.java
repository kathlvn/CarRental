package com.mobcom.carrental.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CarBrandData {

    public static List<String> getBrands() {
        return Arrays.asList(
                "Toyota", "Honda", "Mitsubishi", "Nissan",
                "Ford", "Suzuki", "Hyundai", "Kia",
                "Geely", "MG", "Others"
        );
    }

    public static List<String> getModelsForBrand(String brand) {
        Map<String, List<String>> map = new LinkedHashMap<>();

        map.put("Toyota",    Arrays.asList("Vios", "Innova", "Fortuner", "HiAce", "Rush", "Raize", "Wigo", "Others"));
        map.put("Honda",     Arrays.asList("City", "Civic", "BR-V", "HR-V", "Jazz", "CR-V", "Others"));
        map.put("Mitsubishi",Arrays.asList("Xpander", "Montero Sport", "Strada", "Mirage", "Eclipse Cross", "Others"));
        map.put("Nissan",    Arrays.asList("Almera", "Terra", "Navara", "X-Trail", "Others"));
        map.put("Ford",      Arrays.asList("EcoSport", "Everest", "Ranger", "Territory", "Others"));
        map.put("Suzuki",    Arrays.asList("Ertiga", "Jimny", "Swift", "Dzire", "XL7", "Others"));
        map.put("Hyundai",   Arrays.asList("Accent", "Tucson", "Santa Fe", "Starex", "Others"));
        map.put("Kia",       Arrays.asList("Picanto", "Soluto", "Seltos", "Carnival", "Sportage", "Others"));
        map.put("Geely",     Arrays.asList("Coolray", "Okavango", "Azkarra", "Emgrand", "Others"));
        map.put("MG",        Arrays.asList("ZS", "RX5", "VS", "One", "Others"));
        map.put("Others",    Arrays.asList("Others"));

        List<String> models = map.get(brand);
        return models != null ? models : Arrays.asList("Others");
    }
}