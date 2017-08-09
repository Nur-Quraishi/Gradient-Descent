package nur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class GradientDescent
{
    public static List<List<Double>> trainingSet = new ArrayList<>();
    public static Map<Integer, List<Double>> normalizeMap = new TreeMap<>();
    public static List<Double> coEfficientSet = new ArrayList<>();
    public static List<String> featureList = new ArrayList<>();
    public static Double learningRate;
    public static Double toleranceRate;
    public static Integer totalIteration;
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        readInputFile();

        featureScaling();

        initializeCoEfficientSet();

        enterParams();

        estimateCost();

        testAlgorithm();
    }

    public static void  readInputFile()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("input.txt"));

            String line = reader.readLine();
            String[] features = line.split(", ");
            for(String feature : features)
            {
                featureList.add(feature);
            }

            line = reader.readLine();

            while(line != null)
            {
                List<Double> temp = new ArrayList<>();
                String[] elements = line.split(" ");

                temp.add(1.0);
                for(String element : elements)
                {
                    temp.add(Double.parseDouble(element));
                }

                trainingSet.add(temp);
                line = reader.readLine();
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("File doesn't Found!!!");
        }
    }

    public static void featureScaling()
    {
        for(int i = 1; i < trainingSet.get(0).size()-1; i++)
        {
            calculateMaxMean(i);
        }

        for(int i = 0; i < trainingSet.size(); i++)
        {
           for(int j = 1; j < trainingSet.get(0).size()-1; j++)
           {
               double previousElement = trainingSet.get(i).get(j);
               double newElement = (previousElement - normalizeMap.get(j).get(0)) / normalizeMap.get(j).get(1);

               trainingSet.get(i).remove(j);
               trainingSet.get(i).add(j, newElement);
           }
        }

        /*System.out.println("Normalize Map: " + normalizeMap);
        System.out.println("Training Set: " + trainingSet);*/
    }

    public static void calculateMaxMean(int column)
    {
        List<Double> temp = new ArrayList<>();
        double max = -1000.0;
        double mean = 0.0;
        for(int i = 0; i < trainingSet.size(); i++)
        {
            mean += trainingSet.get(i).get(column);

            if(trainingSet.get(i).get(column) > max)
                max = trainingSet.get(i).get(column);
        }
        mean /= trainingSet.size();

        temp.add(mean);
        temp.add(max);
        normalizeMap.put(column, temp);
    }


    public static void initializeCoEfficientSet()
    {
        Random generator = new Random();

        for(int i = 0; i < trainingSet.get(0).size()-1; i++)
        {
            coEfficientSet.add(generator.nextInt(9)*0.1 + 0.1);
        }
    }

    public static void enterParams()
    {
        System.out.println("Please enter the learning rate (alpha): ");
        learningRate = scanner.nextDouble();

        System.out.println("Please enter the tolerance rate: ");
        toleranceRate = scanner.nextDouble();

        System.out.println("Please enter the total number of iterations: ");
        totalIteration = scanner.nextInt();
    }

    public static void estimateCost()
    {
        int numberOfIteration = 1;
        List<Double> delTheta = new ArrayList<>();
        for(int i=0; i < coEfficientSet.size(); i++)
        {
            delTheta.add(100.0);
        }

        while(delTheta.stream().anyMatch(i -> i > toleranceRate) && numberOfIteration <= totalIteration)
        {
            System.out.println( "Step-"+ numberOfIteration + ": "+ coEfficientSet);
            for (int i = 0; i < coEfficientSet.size(); i++)
            {
                Double previousElement = coEfficientSet.get(i);
                Double newElement = previousElement - (learningRate / trainingSet.size()) * calculatePartialDerivation(i);

                coEfficientSet.remove(i);
                coEfficientSet.add(i, newElement);

                delTheta.remove(i);
                delTheta.add(i, Math.abs(newElement - previousElement));
            }

            System.out.println("Del theta: " + delTheta);
            numberOfIteration++;
        }
    }

    public static double calculatePartialDerivation(int k)
    {
        double result = 0.0;

        for(int i =0; i < trainingSet.size(); i++)
        {
            for(int j =0; j < trainingSet.get(0).size()-1; j++)
            {
                result += trainingSet.get(i).get(j) * coEfficientSet.get(j);
            }
            result -= trainingSet.get(i).get(trainingSet.get(0).size()-1);
            result *= trainingSet.get(i).get(k);
        }

        return result;
    }

    public static void testAlgorithm()
    {
        System.out.printf("Please enter the value of ");
        for(int i = 0; i < featureList.size() -1; i++)
        {
            if(i < featureList.size() -2)
                System.out.printf(featureList.get(i) + ", ");
            else
                System.out.printf(featureList.get(i) + ": ");
        }

        List<Double> testSet = new ArrayList<>();
        testSet.add(1.0);
        for(int i = 1; i < coEfficientSet.size(); i++)
        {
            double temp = scanner.nextDouble();

            testSet.add((temp - normalizeMap.get(i).get(0)) / normalizeMap.get(i).get(1));
        }

        Double result = 0.0;
        for(int i = 0; i < coEfficientSet.size(); i++)
        {
            result += coEfficientSet.get(i) * testSet.get(i);
        }

        //System.out.println("Test Set: " + testSet);
        System.out.println(featureList.get(featureList.size()-1) + ": " +  result);

        scanner.close();
    }
}
