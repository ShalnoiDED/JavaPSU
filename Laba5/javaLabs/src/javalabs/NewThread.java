/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

/**
 *
 * @author Максим
 */
public class NewThread extends Thread {    
    public double result = 0.0, low = 0.0, high = 0.0, step = 0.0;
    
    public NewThread(double low, double high, double step) {
        this.low = low;
        this.high = high;
        this.step = step;
    }
    
    public double Calculate(double x) {
        return 1/x;
    }
    
    @Override
    public void run() {
        int n = (int) ((high - low) / step);
        double sum = 0.5 * (Calculate(low) + Calculate(high));
        
        for (int i = 1; i < n; i++) {
            double x = low + i * step;
            sum += Calculate(x);
        }
        
        double lastSegment = (high - (low + n * step));
        sum += lastSegment > 0 ? 0.5 * (Calculate(low + n * step) + Calculate(high)) * lastSegment / step : 0;
        
        result = sum * step;
    }
}
