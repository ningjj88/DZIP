package csii.dzip.action.util.secapipackage;

import java.io.*;
import java.util.*;



public class hsmlog
{
    public static void GetTime(int errlv,String filePath,String fileName) throws IOException
    {
        hsmlog f = new hsmlog();
        if (ShareVar.HsmConf.errlv < errlv)
        {
            return;
        }
        Calendar t= new GregorianCalendar();
        Date Date = t.getTime();
        String c = "abc";
        c = Date.toString();
        f.writeFileString(4, filePath, fileName,c);


    }
    public void writeFileString(int errlv,String filePath,String fileName,String args) throws IOException
    {
        if (ShareVar.HsmConf.errlv < errlv)
        {
            return;
        }
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(filePath+fileName, "rw");

        raf.seek(raf.length());
        raf.writeBytes(args);
        raf.writeBytes("\r\n");

        raf.close();

    }

    public void writeFileByte(int errlv,String filePath,String fileName,byte[] args,int len) throws IOException
    {
        if (ShareVar.HsmConf.errlv < errlv)
        {
            return;
        }
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(filePath+fileName, "rw");
        raf.seek(raf.length());
        raf.write(args,0,len);
        raf.writeBytes("\r\n");
        raf.close();
    }
    public void writeFileInt(int errlv,String filePath,String fileName,int args) throws IOException
    {
        if (ShareVar.HsmConf.errlv < errlv)
        {
            return;
        }
        RandomAccessFile raf = null;
        raf = new RandomAccessFile(filePath+fileName, "rw");
        raf.seek(raf.length());
        raf.write(args);
        raf.writeBytes("\r\n");
        raf.close();
    }
}