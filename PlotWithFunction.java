import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import javax.swing.*;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;


public class PlotWithFunction extends JFrame {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    private final double[] coefficients;
    private static final int degree = 5;

    private final double[] dataX;  
    private final double[] dataY;
    private final DoubleUnaryOperator function;

    public PlotWithFunction() {
        super("Workout Volume");

        double[][] data = readCSV("workouts.csv");

        dataX = data[0];
        dataY = data[1];

        coefficients = PolynomialFitter(dataX, dataY);

    

       function = x -> {
            double result = 0;

            for (int i = 0; i < coefficients.length; i++) {
                result += coefficients[i] * Math.pow(x, i);
            }

            return result;
        };

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new PlotPanel(dataX, dataY, function));
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlotWithFunction().setVisible(true));
    }

    private static class PlotPanel extends JPanel {
        private final double[] dataX;
        private final double[] dataY;
        private final DoubleUnaryOperator function;

        private PlotPanel(double[] dataX, double[] dataY, DoubleUnaryOperator function) {
            this.dataX = dataX;
            this.dataY = dataY;
            this.function = function;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int paddingLeft = 70;
            int paddingRight = 30;
            int paddingTop = 30;
            int paddingBottom = 60;

            double minX = min(dataX);
            double maxX = max(dataX);
            double minY = min(dataY);
            double maxY = max(dataY);

            List<Double> sampledX = new ArrayList<>();
            List<Double> sampledY = new ArrayList<>();
            int samples = 400;
            for (int i = 0; i <= samples; i++) {
                double x = minX + (maxX - minX) * i / samples;
                double y = function.applyAsDouble(x);
                sampledX.add(x);
                sampledY.add(y);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }

            int width = getWidth();
            int height = getHeight();
            int plotWidth = width - paddingLeft - paddingRight;
            int plotHeight = height - paddingTop - paddingBottom;

            double xScale = plotWidth / (maxX - minX);
            double yScale = plotHeight / (maxY - minY);

            // Plot background grid
            g2.setColor(new Color(230, 230, 230));
            for (int i = 0; i <= 10; i++) {
                int x = paddingLeft + (int) ((i / 10.0) * plotWidth);
                g2.drawLine(x, paddingTop, x, height - paddingBottom);
                int y = paddingTop + (int) ((i / 10.0) * plotHeight);
                g2.drawLine(paddingLeft, y, width - paddingRight, y);
            }

            // Axes
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom);
            g2.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom);

            // Function line
            g2.setColor(new Color(0, 123, 255));
            g2.setStroke(new BasicStroke(2.5f));
            for (int i = 1; i < sampledX.size(); i++) {
                double x1 = sampledX.get(i - 1);
                double y1 = sampledY.get(i - 1);
                double x2 = sampledX.get(i);
                double y2 = sampledY.get(i);

                int x1Px = paddingLeft + (int) ((x1 - minX) * xScale);
                int y1Px = height - paddingBottom - (int) ((y1 - minY) * yScale);
                int x2Px = paddingLeft + (int) ((x2 - minX) * xScale);
                int y2Px = height - paddingBottom - (int) ((y2 - minY) * yScale);

                g2.drawLine(x1Px, y1Px, x2Px, y2Px);
            }

            // Scatter data points
            g2.setColor(new Color(220, 53, 69));
            for (int i = 0; i < dataX.length; i++) {
                int x = paddingLeft + (int) ((dataX[i] - minX) * xScale);
                int y = height - paddingBottom - (int) ((dataY[i] - minY) * yScale);
                g2.fillOval(x - 5, y - 5, 10, 10);
            }

            // Labels
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Data Points vs. Function", width / 2 - 95, 20);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("X", width - 20, height - 25);
            g2.drawString("Y", 18, paddingTop + 8);

            g2.dispose();
        }
    }

    private static double min(double[] arr) {
        double m = arr[0];
        for (double v : arr) m = Math.min(m, v);
        return m;
    }

    private static double max(double[] arr) {
        double m = arr[0];
        for (double v : arr) m = Math.max(m, v);
        return m;
    }

        private static double[][] readCSV(String fileName) {

        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

        reader.readLine();

        String line;
        int lineNumber = 1; // Track line number for easier debugging

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            
            // 1. Skip completely empty or blank lines (common at the end of CSV files)
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");

            // 2. Ensure the row actually has both required columns
            if (parts.length < 2) {
                System.out.println("Skipping malformed row at line " + lineNumber + ": " + line);
                continue;
            }

            try {
                // 3. Clean up accidental trailing/leading spaces using .trim()
                double workoutNumber = Double.parseDouble(parts[0].trim());
                double volume = Double.parseDouble(parts[1].trim());

                xValues.add(workoutNumber);
                yValues.add(volume);
                
            } catch (NumberFormatException e) {
                // 4. Safely log the exact text that broke the parser
                System.out.println("Error parsing numbers at line " + lineNumber + ": " + line);
            }
        }

        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }

        double[] x = new double[xValues.size()];
        double[] y = new double[yValues.size()];

        for (int i = 0; i < xValues.size(); i++) {
            x[i] = xValues.get(i);
            y[i] = yValues.get(i);
        }

        return new double[][] {x, y};
    }

        private static double[] PolynomialFitter(double[] x, double[] y) {
            WeightedObservedPoints points = new WeightedObservedPoints();
            for (int i = 0; i < x.length; i++) {
                points.add(x[i], y[i]);
                }

                return PolynomialCurveFitter.create(degree).fit(points.toList());
            }


    }

