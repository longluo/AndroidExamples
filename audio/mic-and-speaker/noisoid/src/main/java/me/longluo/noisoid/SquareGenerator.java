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

public class SquareGenerator extends Source {

    public SquareGenerator(int sampleRate, float frequency) {
        this.sampleRate = sampleRate;
        this.k = TWO_PI * frequency / sampleRate;
    }

    @Override
    protected float getNextSample() {
        float res = (float) Math.sin(alpha);

        // make square:
        if(res > 0) { res = 1.0f; }
        else { res = -1.0f; }

        alpha += k;
        if(alpha > TWO_PI) { alpha -= TWO_PI; }
        return res;
    }
}