package crow.jonathan.stegonographer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import jm.audio.io.AudioFileIn;
import jm.util.Read;
import jm.util.Write;

public class AudioSteganographer extends Steganographer
{
    private static float CONVERSION = 100;
    private FixedPoint[] audio;
    private int chans, sampleRate, sampleSize, index;
    
    private AudioSteganographer(float[] samps, int chans, int sampleRate, int sampleSize)
    {
        audio = new FixedPoint[samps.length];
        for(int i = 0; i < audio.length; i++)
            audio[i] = new FixedPoint(samps[i]);
        this.chans = chans;
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        index = 0;
    }
    private void nextFloat()
    {
        index++;
        if(index >= audio.length)
            index = 0;
    }
    @Override
    public void injectByte(byte b) 
    {
        for(int i = 0; i < Byte.SIZE; i++)
        {
            int manip = audio[index].intPart;
            int bit = b&(LSB_MASK << i);
            
            if(bit != 0)
                manip |= LSB_MASK;
            else
                manip &= ~LSB_MASK;
            
            audio[index].intPart = manip;
            nextFloat();
        }
    }
    @Override
    public byte ejectByte() 
    {
        byte b = 0;
        
        for(int i = 0; i < Byte.SIZE; i++)
        {
            int bit = audio[index].intPart&LSB_MASK;
            nextFloat();
            
            if(bit != 0)
                b |= (LSB_MASK << i);
            else
                b &= ~(LSB_MASK << i);
        }
        
        return b;
    }
    @Override
    public boolean fileWillFit(File f) 
    {
        return true;
    }
    @Override
    public boolean save(File f) 
    {
        float[] samps = new float[audio.length];
        for(int i = 0; i < samps.length; i++)
            samps[i] = audio[i].toFloat();
        Write.audio(samps, f.getAbsolutePath(), chans, sampleRate, sampleSize);
        return true;
    }
    
    public static AudioSteganographer fromFile(File f)
    {
        AudioFileIn afi = new AudioFileIn(f.getAbsolutePath());
        return new AudioSteganographer(afi.getSampleData(), afi.getChannels(), afi.getSampleRate(), afi.getSampleBitDepth());
    }
    
    private static class FixedPoint
    {
        private final float SHIFT = 100000.0f;
        private int intPart, fracPart;
        
        private FixedPoint(float f)
        {
            intPart = (int)f;
            fracPart = (int)((f-intPart)*SHIFT);
        }
        private float toFloat()
        {
            return intPart + fracPart/SHIFT;
        }
    }
}
