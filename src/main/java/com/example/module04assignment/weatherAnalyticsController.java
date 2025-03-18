package com.example.module04assignment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/// This is the class for the controller of the fxml of the weather application.
///
/// author Maxim Nikiforov
///
/// ## Class overview
/// This class is used once the user selects the file from the slashscreen, and it meant to analyze the .cvs
/// files and displays the information in the form of average temperature per month, what the temperature is
/// considered (hot cold, etc.) and the number of rain days per month. The user can also see what
/// the weather is like for other months using buttons.
///
/// ## Details about implementation
///
/// It uses the buffered reader to read the file line by line per month and stores them a list with
/// the information of temperature, humidity and precipitation per day in a record. It then uses that
/// to find the average temperature, the temperature status and the number of rainy days in the whole list.
/// It reads data for the whole month and stores that information into another record which is used to
/// display the information on screen. The user also has two buttons they can use to look at the data of the
/// previous or next month respectively.
public class weatherAnalyticsController {
    @FXML
    private Label currentMonthLabel;
    @FXML
    private Label numberOfRainyDaysLabel;
    @FXML
    private Label averageTemperatureLabel;
    @FXML
    private Label tempStatusLabel;

    @FXML
    private Button nextMonthButton;
    @FXML
    private Button previousMonthButton;

    @FXML
    private ImageView tempStatusImage;
/// enum for the temperature status
/// to be used in the weatherRepresentation record

    enum temperatureStatus {
        warm,
        cold,
        hot,
    }
    /// record for storing all of the individual weather information about each day during the month
    private record weatherData(int temperature, int humidity, int rained) {}
    /// record for storing the summarization of all the weather for a given month
    private record weatherRepresentation(double avgTemperature, int daysRained, temperatureStatus temperatureStatus) {}
    //the current month that the program is displaying data about
    private slashScreenController.Months currentMonth = slashScreenController.Months.January;
    // the file that we are loading from the other controller
    private File selectedFile;
    /// The method that loads the cvs file,
    /// this is called by the slashScreenController when the user selects a file to load.
    public void loadFile(File file) throws IOException {
        this.selectedFile = file;
        configureGUI(loadMonth());
    }
    /// This method creates a list full of the individual parts of weather data for each day for the current month.
    ///</a>
    /// It uses the buffered reader class to do this and uses .filter to filter out all the data for the other
    /// months and whitespace. It also calls a method filterDayByDay() to store all the humidity/temperature/rain
    /// in the weatherRepresentation record
    ///
    private List<weatherData> loadWeatherData() throws IOException {
        BufferedReader reader = Files.newBufferedReader(selectedFile.toPath());
            return reader.lines().filter(line -> !line.isBlank())// filters out white space
                    .filter(line -> {
                        String[] parts = line.split("-");
                        int lineMonth = Integer.parseInt(parts[1]);
                        return lineMonth == currentMonth.ordinal() + 1;
                    })
                    .map(this::filterDayByDay)
                    .collect(Collectors.toList());
    }
    /// this method filters out the humidity/temperature/rain data for each day and stores it in a weatherData
    /// record.
    private weatherData filterDayByDay(String line) {
        String[] data = line.split(",");
        int temperature = Integer.parseInt(data[1]);
        int humidity = Integer.parseInt(data[2]);
        int rained = Integer.parseInt(data[3]);
        return new weatherData(temperature, humidity, rained);
    }
    /// This method analyzes the data in the list of weatherData and
    private weatherRepresentation loadMonth() throws IOException {
        int monthNum = currentMonth.ordinal() + 1;
        List<weatherData> selectedMonth = new ArrayList<>(loadWeatherData());
        int numRained = 0;
        double totalTemp = 0;
        for (weatherAnalyticsController.weatherData weatherData : selectedMonth) {
            if (weatherData.rained == 1) {
                numRained++;
            }
            totalTemp += weatherData.temperature;
        }
        System.out.println("the total temp is " + totalTemp);
        double avgTemp = totalTemp / selectedMonth.size();
        System.out.println("the average temp is " + avgTemp);
        temperatureStatus status = temperatureStatus.hot;
        if(avgTemp < 50)
            status = temperatureStatus.cold;
        else if(avgTemp < 70) {
            status = temperatureStatus.warm;
        }
        System.out.println("Month: " + monthNum);
        System.out.println("Amount of days: " + selectedMonth.size());
        return new weatherRepresentation(avgTemp, numRained, status);
    }
    /// Method that handles displaying the data in the weatherRepresentation record for
    /// the current month into GUI elements on the screen Like average temperature and
    /// number of rainy days and what status the temperature is considered to have.
    /// </a>
    /// It also displays different images depending on that status, as well as changing button
    /// text and the label that displays the current month to have the right text.
    private void configureGUI(weatherRepresentation data) {
        String monthName = currentMonth.name();
        currentMonthLabel.setText("Here is the data for: " + monthName);
        //int monthNum = currentMonth.ordinal() + 1;
        switch (data.temperatureStatus) {
            case hot -> {
                tempStatusImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/hot.png")).toExternalForm()));
                tempStatusLabel.setText("The temperature is considered: " + temperatureStatus.hot);
            }
            case cold -> {
                tempStatusImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/cold.jpg")).toExternalForm()));
                tempStatusLabel.setText("The temperature is considered: " + temperatureStatus.cold);
            }
            case warm -> {
                tempStatusImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/warm.png")).toExternalForm()));
                tempStatusLabel.setText("The temperature is considered: " + temperatureStatus.warm);
            }
        }
        nextMonthButton.setText("Next Month: " + currentMonth.next());
        previousMonthButton.setText("Previous Month: " + currentMonth.previous());
        averageTemperatureLabel.setText("Average temperature is: " + String.format("%.3f", data.avgTemperature));
        numberOfRainyDaysLabel.setText("The number of rainy days is: " + data.daysRained);
    }
    /// Method to switch the current month to the next on button click,
    /// also loads the GUI for the new month.
    @FXML
    void goNextMonth(ActionEvent event) throws IOException {
        currentMonth = currentMonth.next();
        configureGUI(loadMonth());
    }
    /// Method to switch the current month to the previous on button click,
    /// also loads the GUI for the new month.
    @FXML
    void goPreviousMonth(ActionEvent event) throws IOException {
        currentMonth = currentMonth.previous();
        configureGUI(loadMonth());
    }

}

