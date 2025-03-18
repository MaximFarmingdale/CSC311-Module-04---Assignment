package com.example.module04assignment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Random;

/// This is the controller class for the FXML of the splash screen.
///
/// author Maxim Nikiforov
///
/// ## Class overview
///
/// Allows the user to select a CSV file to display weather information
/// or to create a new file based on real data. The program also lets the
/// user regenerate the data in the file they create.
///
/// ## Details about implementation
///
/// The class generates data into a CSV file using weather data from New York
/// City from this website:
/// <a href="https://weatherspark.com/m/23912/1/Average-Weather-in-January-in-New-York-City-New-York-United-States#Figures-Temperature">
/// WeatherSpark </a>.
/// to be used in the weatherRepresentation record
/// It uses the low and high average temperatures for each month to generate
/// random temperatures while simulating temperature changes within the same month.
/// It also applies the same logic to humidity values but not to precipitation,
/// since it does not vary much within the same month.
/// </a>
/// The user has the option to regenerate values in the CSV file or select
/// a CSV file to display its data. If the second option is chosen, the class
/// will call a method to change the FXML file being displayed and pass the
/// selected file to another controller for data visualization.
public class slashScreenController {
    @FXML
    private Label welcomeText;
    @FXML
    private Button generateButton;

    private Path path = Path.of("src/main/resources/cvs_file_folders/text.csv");
    private Desktop desktop = Desktop.getDesktop();
    int year = 0;
    private File file;
    /// This method on button press lets the user select a cvs file in the internal folder
    /// of this application
    /// </a>
    /// Once the user selects a file, the method will call another method to change the FXML
    /// file and to pass the selected file to new controller of the FXML
    @FXML
    void activateFiles(ActionEvent event) {
        File folder = new File("src/main/resources/cvs_file_folders");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(folder);
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", "*.csv*"));
        Window window = ((Node) event.getSource()).getScene().getWindow();
        //fileChooser.showOpenDialog(window);
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            Stage stage = (Stage) window;
            changeController(stage, selectedFile);
        }

    }
    /// Method to change FXML files, it also passes the selected file to the new controller
    public void changeController(Stage stage, File selectedFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("weatherAnalytics.fxml"));
            Parent newRoot = loader.load();
            weatherAnalyticsController controller = loader.getController();
            controller.loadFile(selectedFile);
            Scene scene = new Scene(newRoot, 1270, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /// On button press this method calls another method that starts the process of filling a
    /// new .cvs file with weather information. It also disables the button if the user has
    /// already generated an existing csv file.
    @FXML
    void generateFile(ActionEvent event) {
        try {
            file = new File(path.toString());
            if(file.exists()) {
                generateButton.setDisable(true);
                return;
            }
            if(file.createNewFile()) {
                fillMonths();
                System.out.println("File Created");
            }
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return;
        }
    }
    /// On button click regenerate data by emptying file and then calling fill months to fill file again
    @FXML
    void redoFile(ActionEvent event) throws IOException {
        file = new File(path.toString());
        FileWriter myWriter = new FileWriter(file);
        myWriter.write("");
        myWriter.close();
        fillMonths();
        System.out.println("File Redo");

    }
    /// Enum for representing the months, it also has methods for getting the next and
    /// previous month
    public enum Months{
        January,
        February,
        March,
        April,
        May,
        June,
        July,
        August,
        September,
        October,
        November,
        December;
        /// gets the next month of an enum month
        public Months next() {
            return Months.values()[(this.ordinal() + 1) % Months.values().length];
        }
        /// gets the previous month of an enum month
        public Months previous() {
            return Months.values()[(this.ordinal() - 1 + Months.values().length) % Months.values().length];
        }
    }
    /// Method that returns a boolean value indicating whether a given year is a
    /// leap year or not.
    public boolean isLeapYear(int year) {
        if(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
            return true;
        }
        return false;
    }
    /// This method both generates a random year between 2000-2025 and also uses the fillDays method
    /// to fill the file with information of the days of each month.
    private void fillMonths() throws IOException {
        year = 2000 + new Random().nextInt(26);
        for (Months month : Months.values()) {
            switch (month) {
                case January -> fillDays(31, month, year, file);
                case February -> {
                    if(isLeapYear(year))
                        fillDays(29, month, year, file);
                    else
                        fillDays(28, month, year, file);
                }
                case March -> fillDays(31, month, year, file);
                case April -> fillDays(30, month, year, file);
                case May -> fillDays(31, month, year, file);
                case June -> fillDays(30, month, year, file);
                case July -> fillDays(31, month, year, file);
                case August -> fillDays(31, month, year, file);
                case September -> fillDays(30, month, year, file);
                case October -> fillDays(31, month, year, file);
                case November -> fillDays(30, month, year, file);
                case December -> fillDays(31, month, year, file);
                default -> throw new IllegalStateException("Unexpected value: " + month);
            }
        }
    }
    /// This method uses data from:
    /// <a href="https://weatherspark.com/m/23912/1/Average-Weather-in-January-in-New-York-City-New-York-United-States#Figures-Temperature">
    /// WeatherSpark </a>.
    /// to calculate random realistic values for the temperature, humidity and precipitation during each day.
    /// </a>
    /// it does this by using the low and high temperature average and humidity during each month and using the
    /// rate of change through the month to model how trend of temperature and humidity changes between the
    /// beginning and end of each month. However, it does not do this for precipitation as it generally is constant
    /// during the beginning and end of each month and has peaks and lows that are quite near to each other.
    /// Generates average temperature and humidity using high and low averages as bounds
    /// for a random integer. The method also adds a rate of change over the month to both of the bounds.
    /// The formula for the rate of change adjustment is:
    /// <pre>
    /// (rate of change / 3) * (days / 10)
    /// </pre>
    /// Since integer division is used, the rate of change is applied as follows:
    ///0 for the first 9 days, 1 for the next 10 days, 2 for the next 10 days, 3 if there are more than 29 days
    /// This ensures a smooth transition throughout the month for realistic data generation.
    /// ### Example Calculation
    ///</a>
    /// using a lower bound of 10 and an upperbound of 21 and a rate of change of 6
    ///
    /// The random temperature calculation:
    /// <pre>
    /// int temperature = rand.nextInt(averageTempLow + ((tempchange / 3) * (i / 10)), averageTempHigh + ((tempchange / 3) * (i / 10)));
    /// </pre>
    ///
    /// Substituting the values:
    /// <pre>
    /// int temperature = rand.nextInt(10 + ((6 / 3) * (i / 10)), 21 + ((6 / 3) * (i / 10)));
    /// </pre>
    ///
    /// Where i represents the day of the month.
    /// ### Results
    /// At the beginning of the month: Temperature ranges from 0 to 20
    /// At the end of a 30-day month: Temperature ranges from 16 to 26
    /// ### More Information on Data Generation
    /// This method helps simulate realistic temperature changes over time.
    /// Whether or not it is raining for a given day depends if the math.random() is higher than the decimal percentage chance
    /// for it to rain on that particular day.
    /// </a>
    /// These generated values of temperature, humidity and precipitation are then written to the file using a file writer
    public void fillDays(int days, Months month, int year, File file) throws IOException {
        int averageTempLow = 0;
        int averageTempHigh = 0;
        int tempchange = 0;
        double chance_rain = 0;
        //double chance_rain_diff = 2;
        int averageHumidityLow = 0;
        int averageHumidityHigh = 0;
        int humidityChange = 0;
        for(Months month1 : Months.values()) {
            switch(month) {
                case January:
                    averageTempHigh = 41;
                    averageTempLow = 31;
                    tempchange = -1;
                    chance_rain = .23;
                    break;
                case February:
                    averageTempHigh = 40;
                    averageTempLow = 28;
                    tempchange = 5;
                    chance_rain = .24;
                    break;
                case March:
                    averageTempHigh = 46;
                    averageTempLow = 33;
                    tempchange = 11;
                    chance_rain = .27;
                    break;
                case April:
                    averageTempHigh = 55;
                    averageTempLow = 41;
                    tempchange = 11;
                    chance_rain = .29;
                    break;
                case May:
                    averageTempHigh = 67;
                    averageTempLow = 51;
                    tempchange = 8;
                    chance_rain = .31;
                    averageHumidityHigh = 12;
                    humidityChange = 12;
                    break;
                case June:
                    averageTempHigh = 75;
                    averageTempLow = 61;
                    tempchange = 7; // summer solstice
                    chance_rain = .32;
                    averageHumidityLow = 12;
                    averageHumidityHigh = 37;
                    humidityChange = 25;
                    break;
                case July:
                    averageTempHigh = 83;
                    averageTempLow = 69;
                    tempchange = 1;
                    chance_rain = .33;
                    averageHumidityLow = 38;
                    averageHumidityHigh = 54;
                    humidityChange = 16;
                    break;
                case August:
                    averageTempHigh = 84;
                    averageTempLow = 71;
                    tempchange = -4;
                    chance_rain = .31;
                    averageHumidityLow = 35;
                    averageHumidityHigh = 54;
                    humidityChange = -19;
                    break;
                case September:
                    averageTempHigh = 80;
                    averageTempLow = 67;
                    tempchange = -9;
                    chance_rain = .26;
                    averageHumidityLow = 11;
                    averageHumidityHigh = 33;
                    humidityChange = -22;
                    break;
                case October:
                    averageTempHigh = 70;
                    averageTempLow = 57;
                    tempchange = -10;
                    chance_rain = .24;
                    averageHumidityLow = 1;
                    averageHumidityHigh = 11;
                    humidityChange = 10;
                    break;
                case November:
                    averageTempHigh = 59;
                    averageTempLow = 47;
                    tempchange = -9;
                    chance_rain = .24;
                    break;
                case December: //winter solstice
                    averageTempHigh = 49;
                    averageTempLow = 38;
                    tempchange = -7;
                    chance_rain = .26;
                    break;
            }
        }
        //file = new File(path.toString());
        FileWriter myWriter = new FileWriter(file, true);
        Random rand = new Random();
        if(!(averageTempHigh == 0))
            averageTempHigh ++;
        if(!(averageHumidityHigh == 0))
            averageHumidityHigh++;
        for(int i = 1; i <= days; i++) {
            int rainOrNot = 0;
            int humidity;
            int tempture = rand.nextInt(averageTempLow + ((tempchange / 3) * (i / 10)), averageTempHigh + ((tempchange / 3) * (i / 10)));
            if(averageHumidityLow == 0 && averageHumidityHigh == 0)
                humidity = 0;
            else
                humidity = rand.nextInt(averageHumidityLow + ((humidityChange / 3) * (i / 10)), averageHumidityHigh + ((humidityChange / 3) * (i / 10)));
            if(chance_rain > Math.random())
                rainOrNot = 1;
            myWriter.write(year + "-" + (month.ordinal() + 1)+ "-" + i + "," + tempture + "," + humidity + "," + rainOrNot + "\n" + "\n");
        }
        myWriter.close();
    }
}
