public class SeriesMergeTask extends Thread {

    Series m1, m2;
    Series result;

    public SeriesMergeTask(Series s1, Series s2) {
        this.result = null;
        this.m1 = s1;
        this.m2 = s2;
    }
    
    public Series getResult() {
        return this.result;
    }
    
    @Override
    public void run() {
        this.result = m1.mergeWith(m2);
    }
}
