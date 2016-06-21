
import org.apfloat.ApfloatContext;
import org.apfloat.Apint;
import org.apfloat.ApintMath;

public class PiTask extends Thread {
    
    public static final Apint COEFF_CHUD_A = new Apint(13591409);
    public static final Apint COEFF_CHUD_B = new Apint(545140134);
    public static final Apint COEFF_CHUD_C = new Apint(640320);
    public static final Apint C_TO_THE_THIRD_DIV_24 = ApintMath.pow(COEFF_CHUD_C, 3).divide(new Apint(24));
    public static final Apint TWO = new Apint(2);
    public static final Apint FIVE = new Apint(5);
    public static final Apint SIX = new Apint(6);
    
    private long begI, endI;
    private Series result;
    
    public PiTask(long begI, long endI) {
        this.begI = begI;
        this.endI = endI;
        this.result = null;
    }
    
    public Series getResult() {
        return this.result;
    }

    @Override
    public void run() {
        this.result = sumRecursive(begI, endI);
    }
    
    private Series sumRecursive(long begI, long endI) {
        Series res = new Series();

        // There may be cases for lengths 2,3,4 for faster calculation
        long intervalLength = endI - begI;
        if(intervalLength < 1){
            throw new IllegalArgumentException("Interval is with length less than 1!");
        }
        
        if(intervalLength == 1){
            Apint idx = new Apint(begI);
            res.P = p(idx);
            res.Q = q(idx);
            res.T = a(idx).multiply(res.P);
            // odd
            if(begI % 2 != 0) {
                res.T = res.T.negate();
            }
        } else { // >1
            Series resL, resR;
            // (begI+endI)/2 but with no overflow
            long midI = endI - (endI - begI)/2;
            resL = sumRecursive(begI, midI);
            resR = sumRecursive(midI, endI);
            res = resL.mergeWith(resR);
        }
        
        return res;
    }
    
    private static Apint a(Apint idx) {
        // A + B*idx
        return COEFF_CHUD_A.add(COEFF_CHUD_B.multiply(idx));
    }
    
    private static Apint p(Apint idx) {
         if(idx.compareTo(Apint.ZERO) == 0){
            return Apint.ONE;
        }
        Apint sixTimesIdx = idx.multiply(SIX);
        Apint twoTimesIdx = idx.multiply(TWO);
        //(6*idx - 5)(2*idx - 1)(6*idx - 1)
        return (sixTimesIdx.subtract(FIVE)).multiply((twoTimesIdx.subtract(Apint.ONE))).multiply((sixTimesIdx.subtract(Apint.ONE)));
    }
    
    private static Apint q(Apint idx) {
        // not checking if < 0
        if(idx.compareTo(Apint.ZERO) == 0){
            return Apint.ONE;
        }
        Apint idxToTheThird = ApintMath.pow(idx, 3);
        // idx^3 * C^3/24
        return idxToTheThird.multiply(C_TO_THE_THIRD_DIV_24);
    }
}
