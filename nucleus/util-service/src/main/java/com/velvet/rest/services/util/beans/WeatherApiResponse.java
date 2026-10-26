package com.velvet.rest.services.util.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WeatherApiResponse {

	private Location location;
	private Current current;
	private Forecast forecast;

	@Data
	public static class Location {
		private String name;
		private String region;
		private String country;
		private Double lat;
		private Double lon;

		@JsonProperty("tz_id")
		private String tzId;

		@JsonProperty("localtime_epoch")
		private Long localtimeEpoch;

		private String localtime;
	}

	@Data
	public static class Current {
		@JsonProperty("last_updated_epoch")
		private Long lastUpdatedEpoch;

		@JsonProperty("last_updated")
		private String lastUpdated;

		@JsonProperty("temp_c")
		private Double tempC;

		@JsonProperty("temp_f")
		private Double tempF;

		@JsonProperty("is_day")
		private Integer isDay;

		private Condition condition;

		@JsonProperty("wind_mph")
		private Double windMph;

		@JsonProperty("wind_kph")
		private Double windKph;

		@JsonProperty("wind_degree")
		private Integer windDegree;

		@JsonProperty("wind_dir")
		private String windDir;

		@JsonProperty("pressure_mb")
		private Double pressureMb;

		@JsonProperty("pressure_in")
		private Double pressureIn;

		@JsonProperty("precip_mm")
		private Double precipMm;

		@JsonProperty("precip_in")
		private Double precipIn;

		private Integer humidity;
		private Integer cloud;

		@JsonProperty("feelslike_c")
		private Double feelslikeC;

		@JsonProperty("feelslike_f")
		private Double feelslikeF;

		@JsonProperty("vis_km")
		private Double visKm;

		@JsonProperty("vis_miles")
		private Double visMiles;

		private Double uv;

		@JsonProperty("gust_mph")
		private Double gustMph;

		@JsonProperty("gust_kph")
		private Double gustKph;
	}

	@Data
	public static class Condition {
		private String text;
		private String icon;
		private Integer code;
	}

	@Data
	public static class Forecast {
		private List<ForecastDay> forecastday;
	}

	@Data
	public static class ForecastDay {
		private String date;

		@JsonProperty("date_epoch")
		private Long dateEpoch;

		private Day day;
		private Astro astro;
		private List<Hour> hour;
	}

	@Data
	public static class Day {
		@JsonProperty("maxtemp_c")
		private Double maxtempC;

		@JsonProperty("maxtemp_f")
		private Double maxtempF;

		@JsonProperty("mintemp_c")
		private Double mintempC;

		@JsonProperty("mintemp_f")
		private Double mintempF;

		@JsonProperty("avgtemp_c")
		private Double avgtempC;

		@JsonProperty("avgtemp_f")
		private Double avgtempF;

		@JsonProperty("maxwind_mph")
		private Double maxwindMph;

		@JsonProperty("maxwind_kph")
		private Double maxwindKph;

		@JsonProperty("totalprecip_mm")
		private Double totalprecipMm;

		@JsonProperty("totalprecip_in")
		private Double totalprecipIn;

		@JsonProperty("avgvis_km")
		private Double avgvisKm;

		@JsonProperty("avgvis_miles")
		private Double avgvisMiles;

		@JsonProperty("avghumidity")
		private Double avghumidity;

		@JsonProperty("daily_will_it_rain")
		private Integer dailyWillItRain;

		@JsonProperty("daily_chance_of_rain")
		private Integer dailyChanceOfRain;

		@JsonProperty("daily_will_it_snow")
		private Integer dailyWillItSnow;

		@JsonProperty("daily_chance_of_snow")
		private Integer dailyChanceOfSnow;

		private Condition condition;
		private Double uv;
	}

	@Data
	public static class Astro {
		private String sunrise;
		private String sunset;
		private String moonrise;
		private String moonset;

		@JsonProperty("moon_phase")
		private String moonPhase;

		@JsonProperty("moon_illumination")
		private String moonIllumination;
	}

	@Data
	public static class Hour {
		@JsonProperty("time_epoch")
		private Long timeEpoch;

		private String time;

		@JsonProperty("temp_c")
		private Double tempC;

		@JsonProperty("temp_f")
		private Double tempF;

		@JsonProperty("is_day")
		private Integer isDay;

		private Condition condition;

		@JsonProperty("wind_mph")
		private Double windMph;

		@JsonProperty("wind_kph")
		private Double windKph;

		@JsonProperty("wind_degree")
		private Integer windDegree;

		@JsonProperty("wind_dir")
		private String windDir;

		@JsonProperty("pressure_mb")
		private Double pressureMb;

		@JsonProperty("pressure_in")
		private Double pressureIn;

		@JsonProperty("precip_mm")
		private Double precipMm;

		@JsonProperty("precip_in")
		private Double precipIn;

		private Integer humidity;
		private Integer cloud;

		@JsonProperty("feelslike_c")
		private Double feelslikeC;

		@JsonProperty("feelslike_f")
		private Double feelslikeF;

		@JsonProperty("windchill_c")
		private Double windchillC;

		@JsonProperty("windchill_f")
		private Double windchillF;

		@JsonProperty("heatindex_c")
		private Double heatindexC;

		@JsonProperty("heatindex_f")
		private Double heatindexF;

		@JsonProperty("dewpoint_c")
		private Double dewpointC;

		@JsonProperty("dewpoint_f")
		private Double dewpointF;

		@JsonProperty("will_it_rain")
		private Integer willItRain;

		@JsonProperty("chance_of_rain")
		private Integer chanceOfRain;

		@JsonProperty("will_it_snow")
		private Integer willItSnow;

		@JsonProperty("chance_of_snow")
		private Integer chanceOfSnow;

		@JsonProperty("vis_km")
		private Double visKm;

		@JsonProperty("vis_miles")
		private Double visMiles;

		@JsonProperty("gust_mph")
		private Double gustMph;

		@JsonProperty("gust_kph")
		private Double gustKph;

		private Double uv;
	}
}
