package crow.jonathan.stegonographer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public abstract class Steganographer 
{
    protected static final int LSB_MASK = 0x1;
    protected static final int BYTE_MASK = 0xFF;
    protected static final int BUFFER_SIZE = 1024;
    public abstract void injectByte(byte b);
    public abstract byte ejectByte();
    public abstract boolean fileWillFit(File f);
    public abstract boolean save(File f);
    
    public void injectInt(int val)
    {
        for(int i = 0; i < Integer.BYTES; i++)
        {
            injectByte((byte)(val&BYTE_MASK));
            val = val >> Byte.SIZE;
        }
    }
    public void injectString(String str)
    {
        injectInt(str.length());
        for(byte b : str.getBytes())
            injectByte(b);
    }
    public int ejectInt()
    {
        int val = 0;
        for(int i = 0; i < Integer.BYTES; i++)
            val |= (((int)ejectByte())&BYTE_MASK << (Byte.SIZE*i));
        return val;
    }
    public String ejectString()
    {
        int len = ejectInt();
        byte[] bytes = new byte[len];
        for(int i = 0; i < len; i++)
            bytes[i] = ejectByte();
        return new String(bytes);
    }
    public boolean injectFile(File f)
    {
        if(!fileWillFit(f))
            return false;
        FileInputStream fIn = null;
        try
        {
            injectInt((int)f.length());
            injectString(f.getName());
            
            fIn = new FileInputStream(f);
            byte[] buff = new byte[BUFFER_SIZE];
            while(fIn.available() > 0)
            {
                int len = fIn.read(buff);
                for(int i = 0; i < len; i++)
                    injectByte(buff[i]);
            }
            
            return true;
        }
        catch(Exception err)
        {
            err.printStackTrace();
        }
        finally
        {
            if(fIn != null)
            {
                try
                {
                    fIn.close();
                }
                catch(Exception err)
                {}
            }
        }
        return false;
    }
    public boolean ejectFile()
    {
        FileOutputStream fOut = null;
        try
        {
            int size = ejectInt();
            String name = ejectString();
            System.out.println("filename: " + name);
            
            fOut = new FileOutputStream(new File(name));
            for(int i = 0; i < size; i++)
                fOut.write(ejectByte());
            
            return true;
        }
        catch(Exception err)
        {
            err.printStackTrace();
        }
        finally
        {
            if(fOut != null)
            {
                try
                {
                    fOut.close();
                }
                catch(Exception err)
                {}
            }
        }
        return false;
    }
}
