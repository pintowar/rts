package br.uece.goes.rts.dto;

import java.io.Serializable;

/**
 * Created by thiago on 01/01/17.
 */
public class Stats implements Serializable {

    final double min, max, mean, median, q1, q3, stdDev;

    public Stats(double min, double max, double mean, double median, double q1, double q3, double stdDev) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.stdDev = stdDev;
    }

    public Stats() {
        this(0, 0, 0, 0, 0, 0, 0);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getQ1() {
        return q1;
    }

    public double getQ3() {
        return q3;
    }

    public double getStdDev() {
        return stdDev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stats stats = (Stats) o;

        if (Double.compare(stats.min, min) != 0) return false;
        if (Double.compare(stats.max, max) != 0) return false;
        if (Double.compare(stats.mean, mean) != 0) return false;
        if (Double.compare(stats.median, median) != 0) return false;
        if (Double.compare(stats.q1, q1) != 0) return false;
        if (Double.compare(stats.q3, q3) != 0) return false;
        return Double.compare(stats.stdDev, stdDev) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(min);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(max);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(median);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q1);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(q3);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stdDev);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
