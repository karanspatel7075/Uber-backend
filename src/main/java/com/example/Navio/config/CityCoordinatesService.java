package com.example.Navio.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class CityCoordinatesService {

    private Map<String, double[]> cityCoordinates;

    @PostConstruct // ✅ Automatically runs after bean creation
    public void loadCityCoordinates() {
        ObjectMapper mapper = new ObjectMapper();

        try(InputStream inputStream = getClass()
                .getResourceAsStream("/cities.json")) {
            cityCoordinates = mapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, double[]>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double[] getCoordinates(String city) {
        return cityCoordinates.getOrDefault(city.toLowerCase(), new double[]{0.0, 0.0});
    }
}
