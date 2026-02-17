package agh;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class Presenter {

    @FXML
    private TextField nField;

    @FXML
    private Button computeButton;

    @FXML
    private Button computeButton2;

    @FXML
    private CheckBox checkBox;

    @FXML
    private LineChart<Number, Number> chart;

    private final EquationSolver equationSolver = new EquationSolver();

    @FXML
    private void initialize() {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        xAxis.setLabel("x");
        yAxis.setLabel("u(x)");
        xAxis.setLowerBound(-0.05);
        xAxis.setUpperBound(2.05);
        yAxis.setLowerBound(-1);
        yAxis.setUpperBound(30);
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setTickUnit(0.01);
        yAxis.setTickUnit(1);
        chart.setLegendVisible(false);
        chart.setAnimated(true);
    }

    @FXML
    private void onClicked() {
        int n = Integer.parseInt(nField.getText());
        double[] u = equationSolver.solve(n);
        chart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double h = 2.0/n;
        for(int i = 0; i <= n; i++) {
            double x = i*h;
            series.getData().add(new XYChart.Data(x, u[i]));
        }
        chart.getData().add(series);
    }

    @FXML
    private void onClicked2() {
        int n = Integer.parseInt(nField.getText());
        double[] u = equationSolver.solveThomasAlgorithm(n);
        chart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        double h = 2.0/n;
        for(int i = n; i >=0    ; i--) {
            double x = i*h;
            series.getData().add(new XYChart.Data(x, u[i]));
        }
        chart.getData().add(series);
    }

    @FXML
    private void onCheck() {
        chart.setCreateSymbols(checkBox.isSelected());
    }
}
