import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class Test {

    public static void main(String[] args) {

        // Create a collection of data points
        WeightedObservedPoints points = new WeightedObservedPoints();

        points.add(1, 3);
        points.add(2, 5);
        points.add(3, 7);
        points.add(4, 9);
        points.add(5, 11);

        // Fit a degree-1 polynomial (linear function)
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);

        double[] coefficients = fitter.fit(points.toList());

        // coefficients[0] = intercept
        // coefficients[1] = slope

        System.out.println("Intercept: " + coefficients[0]);
        System.out.println("Slope: " + coefficients[1]);

        System.out.println(
            "Function: y = " +
            coefficients[1] + "x + " +
            coefficients[0]
        );
    }
}


