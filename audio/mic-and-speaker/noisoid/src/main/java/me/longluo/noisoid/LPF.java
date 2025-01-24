/*

MIT License

Copyright © 2024 HARDCODED JOY S.R.L. (https://hardcodedjoy.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package me.longluo.noisoid;

public class LPF {

    private final int N;
    private final float[] mem;
    private int index;
    private float sum;

    private final float cutoff;
    private final float sampleRateOverTwo;

    public LPF(float sampleRate, float cutoff) {

        this.cutoff = cutoff;

        sampleRateOverTwo = sampleRate/2;

        N = (int)( sampleRate / (2*cutoff) );
        mem = new float[N];

        for(int i=0; i<N; i++) { mem[i] = 0; }
        index = 0;
        sum = 0;
    }


    public float filter(float x) {

        if(cutoff >= sampleRateOverTwo) return x;

        sum -= mem[index]; // remove oldest sample from sum
        sum += x; // add newest sample to sum
        mem[index] = x; // add newest sample to memory
        index++;
        index = index % N;

        return (float)(sum/N);
    }


    public void settle() { // fill buffer with last sample
        int j = index-1;
        if(index == 0) j = N-1;
        float sample = mem[j];
        sum = 0;
        for(int i=0; i<N; i++) {
            mem[i] = sample;
            sum += sample;      // sum stores the sum of all samples
        }
    }

    @SuppressWarnings("unused")
    public float getCutoff() { return cutoff; }
}