package com.stepup.ims.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DataUtils {

    public static List<String> getCountriesList() {
        return Arrays.stream(Locale.getISOCountries())
                .map(countryCode -> new Locale("", countryCode).getDisplayCountry())
                .sorted()
                .toList();
    }
    private DataUtils() {}
}
