// Ruta: src/main/java/com/agroalert/agroalertbackend/dto/external/OpenMeteoResponseDto.java
package com.agroalert.agroalertbackend.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponseDto {

    private Double latitude;
    private Double longitude;

    @JsonProperty("current_weather")
    private CurrentWeather currentWeather;

    @JsonProperty("current")
    private Current current;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentWeather {
        private String time;
        private Double temperature;
        private Double windspeed;
        private Double winddirection;
        private Integer weathercode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Current {
        private String time;

        @JsonProperty("temperature_2m")
        private Double temperature2m;

        @JsonProperty("relative_humidity_2m")
        private Double relativeHumidity2m;

        private Double precipitation;

        @JsonProperty("wind_speed_10m")
        private Double windSpeed10m;
    }

    public Double resolverTemperatura() {
        if (current != null && current.getTemperature2m() != null) {
            return current.getTemperature2m();
        }
        if (currentWeather != null && currentWeather.getTemperature() != null) {
            return currentWeather.getTemperature();
        }
        return 0.0;
    }

    public Double resolverPrecipitacion() {
        if (current != null && current.getPrecipitation() != null) {
            return current.getPrecipitation();
        }
        return 0.0;
    }

    public Double resolverHumedadRelativa() {
        if (current != null && current.getRelativeHumidity2m() != null) {
            return current.getRelativeHumidity2m();
        }
        return 60.0; // Valor típico si no está reportado en current_weather básico
    }

    public Double resolverVelocidadViento() {
        if (current != null && current.getWindSpeed10m() != null) {
            return current.getWindSpeed10m();
        }
        if (currentWeather != null && currentWeather.getWindspeed() != null) {
            return currentWeather.getWindspeed();
        }
        return 0.0;
    }
}
