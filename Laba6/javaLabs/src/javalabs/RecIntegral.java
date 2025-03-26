/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public void SetResult(double res){
        this.result = res;
    }
    
    public RecIntegral(double low, double high, double step) throws RangeException {
        if(low <= 0.000001 || low >= 1000000 ||
           high <= 0.000001 || high >= 1000000000 ||
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
    
    public double Result() {
        result = 0.0;
        
        // Делим отрезок интегрирования на 3 равных кусков для каждой нити.
        double length = (high - low) / 3;
        // Массив нитей.
        ArrayList<NewThread> threads = new ArrayList<>();
        
        long startTime = System.currentTimeMillis();
        // Цикл - каждой нити передаем координаты кусков, которые они вычисляют "параллельно"
        for (double start = low; start + length < high; start += length) {
            // Передаем нити данные
            threads.add(new NewThread(start, start + length, step));
            // Запускаем
            threads.getLast().start();
        }
        
        for (NewThread thread : threads) {
            // Ждем нить, если она еще не посчитала результат
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(RecIntegral.class.getName()).log(Level.SEVERE, null, ex);
            }
            // к общему результату прибавляем вычисленный результат нити
            result += thread.result;
            thread.isAlive();
        }
        
        long endTime = System.currentTimeMillis();
        long timeElapced = endTime - startTime;
        System.out.println("Calculation time: " + timeElapced + " ms");
        
        return result;
    }
}
