/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

/**
 *
 * @author Максим
 */
public class RecIntegral {
    
    private double result = 0.0, low = 0.0, high = 0.0, step = 0.0;
   
    public double[] GetResultFields() {
        double[] arr = {result, low, high, step};
        return arr;
    }
    
    public RecIntegral(double low, double high, double step) throws RangeException {
        if(low <= 0.000001 || low >= 1000000 ||
           high <= 0.000001 || high >= 1000000 ||
           step <= 0.000001 || step >= 1000000) {
           throw new RangeException("Одно из чисел находится вне разрешенного диапазона [0.000001;1000000]");
        }
        
        if(high < low){
            throw new RangeException("Верхняя граница меньше нижней");
        }
        
        if(step > high - low) {
            throw new RangeException("Шаг больше интервала интегрирования");
        }
        
        this.low = low;
        this.high = high;
        this.step = step;
        this.result = 0.0;
    }
    
    public double Calculate(double x) {
        return 1/x;
    }
    
    public double Result() {
        int n = (int) ((high - low) / step);
        double sum = 0.5 * (Calculate(low) + Calculate(high));
        
        for (int i = 1; i < n; i++) {
            double x = low + i * step;
            sum += Calculate(x);
        }
        
        double lastSegment = (high - (low + n * step));
        sum += lastSegment > 0 ? 0.5 * (Calculate(low + n * step) + Calculate(high)) * lastSegment / step : 0;
        
        result = sum * step;
        
        return result;
    }
}
