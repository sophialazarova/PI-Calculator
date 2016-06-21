
import org.apfloat.Apint;

public class Series {
    public Apint P, Q, T;
    
    public Series() {
        this.P = null;
        this.Q = null;
        this.T = null;
    }
    
    public Series(Apint P, Apint Q, Apint B, Apint T) {
        this.P = P;
        this.Q = Q;
        this.T = T;
    }
    
    public Series mergeWith(Series other) {
        Series res = new Series();
        // put together by the rules
        res.P = this.P.multiply(other.P);
        res.Q = this.Q.multiply(other.Q);
        // res.T = (R.Q * L.T) + (L.P * R.T)
        res.T = (other.Q.multiply(this.T)).add((this.P.multiply(other.T)));
        return res;
    }
    
    @Override
    public String toString() {
        return "P: " + P.toString(true) + "; Q: " + Q.toString(true) + "; T: " + T.toString(true) + "\n";
    }
}
