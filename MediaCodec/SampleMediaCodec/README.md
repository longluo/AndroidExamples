#Android MediaCodec example
API Level 21 is required

Flow of video playback

    1.MediaExtractor set source video resource (R.raw.xxx)
    2.MediaExtractor get video type (In MediaFormat) and select first video track ("video/")
    3.MediaCodec creates decoder with video type (MediaFormat.KEY_MINE)
    4.Configure MediaCodec as "decoder" and start()
    5.Looping if not End-Of-Stream
    6.   Request (De-queue) input buffer from MediaCodec by dequeueInputBuffer()
    7.   Read video data source (SampleData) by MediaExtractor.readSampleData() to input buffer
    8.   if has valid video data,send input buffer to MediaCodec for decode
    9.   otherwise. set BUFFER_FLAG_END_OF_STREAM to MediaCodec, and set eos
    10.  Request (De-queue) output buffer from MediaCodec by dequeueOutputBuffer()
    11.  If video frame is valid in output buffer, render it on surface by releaseOutputBuffer()
    12.End of loop
    13.Release MediaCodec, MediaExtractor
