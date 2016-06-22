
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.Apint;
import org.apfloat.FixedPrecisionApfloatHelper;

public class PiCalculator {
    //101 digits precision is more than enough - I add +1 either way
    public static final String DPT_STRING =
            "14.18164746272547765552552167818177086376912528982872695981685433294579740853885002187642589487261035";
    
    public static Apfloat calculate(long digits, int threadsCount, boolean quiet) {
        // Instead of exceptions, silent handling
        if(threadsCount < 1){
            threadsCount = 1;
        }
        if(digits < 3){
            digits = 3;
        }
        
        long begInit = System.nanoTime();
        Apfloat digits_per_term = new Apfloat(DPT_STRING);
        Apint termsAp = (((new Apint(digits)).divide(digits_per_term)).add(Apfloat.ONE)).floor();
        long termsAll = termsAp.toBigInteger().longValue();
        if(threadsCount > termsAll){
            threadsCount = (int) termsAll;
        }
        
        // TPP = ((threads-1) + (length)) / threads, but length = terms + 1
        // the last thread may calculate up to termsPerThread + (threads - 1) terms
        long termsPerThread = (termsAll + threadsCount) / threadsCount;
        
        if(!quiet){
            System.out.println("Calculating Pi up to " + digits + " digits. Number of terms: " + termsAll +
                    ". " + System.lineSeparator() + "Using " + threadsCount + ((threadsCount>1)?" threads" : " thread") +
                    " with average " + (termsPerThread - 1) + " terms per thread.");
            Main.log = "Calculating Pi up to " + digits + " digits. Number of terms: " + termsAll +
                    ". " + System.lineSeparator() + "Using " + threadsCount + ((threadsCount>1)?" threads" : " thread") +
                    " with average " + (termsPerThread - 1) + " terms per thread." + System.lineSeparator();
        }
        
        PiTask[] tasks = new PiTask[threadsCount];
        for (int th = 0; th < threadsCount; th++) {
            long begin = th * (termsPerThread - 1);
            long end = begin + (termsPerThread - 1);
            if(th == (threadsCount-1)){
                // up to end + (threads - 1)
                end = termsAll;
            }
            tasks[th] = new PiTask(begin, end);
        }
        long endInit = System.nanoTime();
        if(!quiet){
            System.out.println("        Time for initialization: " +
                (endInit-begInit)/1_000_000 + " ms.");
            Main.log += "        Time for initialization: " +
                    (endInit-begInit)/1_000_000 + " ms." + System.lineSeparator();
        }
        
        long begCalc = System.nanoTime();
        for (int i = 0; i < tasks.length; i++) {
            ApfloatContext context = (ApfloatContext) ApfloatContext.getGlobalContext().clone();
            context.setNumberOfProcessors(1);
            ApfloatContext.setThreadContext(context, tasks[i]);
            tasks[i].start();
        }
        
        if(!quiet){
            System.out.println("    Series calculation started (binary-splitting).");
            Main.log += "    Series calculation started (binary-splitting)." + System.lineSeparator();
        }
        
        for (int i = 0; i < tasks.length; i++) {
            try {
                tasks[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(PiCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        long endCalc = System.nanoTime();
        if(!quiet){
            System.out.println("        Time for calculation of binary-split series: " +
                (endCalc-begCalc)/1_000_000 + " ms.");
            Main.log += "        Time for calculation of binary-split series: " +
                    (endCalc-begCalc)/1_000_000 + " ms."+ System.lineSeparator();
        }
        
        Deque<Series> results = new ArrayDeque<Series>();
        
        for (int i = 0; i < tasks.length; i++) {
            results.addLast(tasks[i].getResult());
        }
        
        if(!quiet){
            System.out.println("    Started merging of final series.");
            Main.log += "    Started merging of final series." +System.lineSeparator();
        }
        
        long begMerge = System.nanoTime();
        
        int maxMergers = results.size() / 2;
        // Array size of 0 is OK in Java => OK with 1 thread (1/2=0)
        SeriesMergeTask[] mergers = new SeriesMergeTask[maxMergers];

        while(results.size() > 1){
            int mergesCount = results.size() / 2;
            
            // If single merge left do not spawn another thread
            if(mergesCount == 1){
                results.addFirst(results.pollFirst().mergeWith(results.pollFirst()));
                // If there were actually 3 elements - merge the last too
                if (results.size() == 2){
                    results.addFirst(results.pollFirst().mergeWith(results.pollFirst()));
                }
                break;
            }
            
            for (int i = 0; i < mergesCount; i++) {
                mergers[i] = new SeriesMergeTask(results.pollFirst(), results.pollFirst());
            }
            for (int i = 0; i < mergesCount; i++) {
                ApfloatContext context = (ApfloatContext) ApfloatContext.getGlobalContext().clone();
                context.setNumberOfProcessors(1);
                ApfloatContext.setThreadContext(context, mergers[i]);
                mergers[i].start();
            }
            for (int i = 0; i < mergesCount; i++) {
                try {
                    mergers[i].join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PiCalculator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            // Reversed so that we can use addFirst, takes care of the case where there are
            // odd number of series to merge and the last one is left out for the next round
            for (int i = mergesCount - 1; i >= 0; --i) {
                results.addFirst(mergers[i].getResult());
            }
        }

        long endMerge = System.nanoTime();
        if(!quiet){
            System.out.println("        Time for merging binary-split series: " +
                (endMerge-begMerge)/1_000_000 + " ms.");
            Main.log += "        Time for merging binary-split series: " +
                    (endMerge-begMerge)/1_000_000 + " ms." + System.lineSeparator();
        }
        
        Series res = results.pollFirst();
        
        if(!quiet){
            System.out.println("    Started final calculation of Pi from merged binary-split series.");
            Main.log += "    Started final calculation of Pi from merged binary-split series." + System.lineSeparator();
        }
        long begFinal = System.nanoTime();
        // Calculate final result
        // S(0,n) = T(0,n) / (B(0,n)*Q(0,n))
        Apfloat floatT = res.T;
        FixedPrecisionApfloatHelper calcFixed = new FixedPrecisionApfloatHelper(digits);
        Apfloat summedSeriesRev = calcFixed.divide(res.Q, floatT);
        Apfloat sqrtC = calcFixed.sqrt((new Apfloat(10005)));
        Apint coefPi2 = new Apint(426880);
        Apfloat pi = summedSeriesRev.multiply(sqrtC).multiply(coefPi2);
        //pi.precision(digits);
        long endFinal = System.nanoTime();
        if(!quiet){
            System.out.println("        Time for final calculation of Pi: " +
                (endFinal-begFinal)/1_000_000 + " ms.");
            Main.log += "        Time for final calculation of Pi: " +
                    (endFinal-begFinal)/1_000_000 + " ms." + System.lineSeparator();
        }
        
        return pi;
        //System.out.println(pi.toString(true));
    }
}
