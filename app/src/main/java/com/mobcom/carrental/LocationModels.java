package com.mobcom.carrental;

import java.util.Map;
import java.util.List;

public class LocationModels {

    public static class Region {
        public String region_name;
        public Map<String, Province> province_list;
    }

    public static class Province {
        public String province_name;
        public Map<String, Municipality> municipality_list;
    }

    public static class Municipality {
        public String municipality_name;
        public List<String> barangay_list;
    }
}