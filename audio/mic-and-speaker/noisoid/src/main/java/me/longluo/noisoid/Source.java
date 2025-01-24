package me.longluo.noisoid;


abstract public class Source {

    static protected final double PI = Math.PI;
    static protected final double TWO_PI = Math.PI * 2;

    static private int nextId = 0;
    protected int id;

    protected final Object sync;

    protected int sampleRate;
    protected double k;
    protected double alpha;

    protected float amplitudeL;
    protected float amplitudeR;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Source() {
        sync = new Object();
        setId(nextId);
        nextId++;
        if (nextId < 0) {
            nextId = 0;
        }

        this.amplitudeL = 1.0f;
        this.amplitudeR = 1.0f;
    }

    public void setAmplitude(float left, float right) {
        synchronized (sync) {
            this.amplitudeL = left;
            this.amplitudeR = right;
        }
    }

    synchronized public void setFrequency(float frequency) {
        synchronized (sync) {
            this.k = TWO_PI * frequency / this.sampleRate;
        }
    }

    protected float getNextSample() {
        return 0;
    } // to  be overridden by implementations

    public void readTo(float[] buf, int offset, int len) { // also can be overridden if needed
        float sample;
        for (int i = 0; i < len; ) {
            synchronized (sync) {
                sample = getNextSample();
                buf[offset + (i++)] += sample * amplitudeL; // left
                buf[offset + (i++)] += sample * amplitudeR; // right
                // using "+=" because we are mixing with what is already in buf
            }
        }
    }
}
