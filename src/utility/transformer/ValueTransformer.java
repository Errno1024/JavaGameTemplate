package utility.transformer;

import utility.GameUtils;

public class ValueTransformer {
    private double init;
    private double dest;
    private double initSmooth;
    private double destSmooth;
    private long totalTime;
    private long count;
    
    public ValueTransformer(double init, double dest, long totalTime, double initSmooth, double destSmooth) {
        this.init = init;
        this.dest = dest;
        this.totalTime = totalTime;
        this.initSmooth = initSmooth;
        this.destSmooth = destSmooth;
        reset();
    }
    
    public ValueTransformer(double init, double dest, long totalTime) {
        this(init, dest, totalTime, 0.3333333333, 0.6666666667);
    }
    
    public void reset() {
        count = 0;
    }
    
    public void refresh() {
        if (count < totalTime) {
            count += 1;
        }
    }
    
    public double getValue() {
        return GameUtils.valueTransition((double) count / totalTime, init, dest, initSmooth, destSmooth);
    }

    public boolean isEnd() {
        return count >= totalTime;
    }
}
