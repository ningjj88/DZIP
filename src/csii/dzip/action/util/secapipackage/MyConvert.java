package csii.dzip.action.util.secapipackage;

public class MyConvert
{
    private int char2Hex(byte indata)
    {
        byte outdata;

        if ( (indata >= '0') && (indata <= '9'))
        {
            outdata = (byte) (indata - 0x30);
        }
        else if ( (indata >= 'A') && (indata <= 'F'))
        {
            outdata = (byte) (indata - 'A' + 10);
        }
        else if ( (indata >= 'a') && (indata <= 'f'))
        {
            outdata = (byte) (indata - 'a' + 10);
        }
        else
        {
            return  -1;
        }

        return (outdata & 0x0f);

    }



    private int hex2Char(byte indata)
    {
        byte outdata;

        if (indata < 10)
        {
            outdata = (byte) (indata + '0');
        }
        else if (indata < 17)
        {
            outdata = (byte) (indata + ('A' - 10));
        }
        else
        {
            return -1;
        }
        return  (outdata & 0xff);

    }



    /**
     *
     * @param indata
     * @param inlen
     * @param outdata
     * @throws Exception
     */
    public byte[] str2Hex(byte[] indata, int inlen)
    {
        if (indata.length < inlen)
        {
            return null;
        }
        if (inlen%2 != 0)
        {
            return null;
        }

        byte[] outdata = new byte[inlen/2];
        int n = 0;
        for (int i = 0; i < inlen/2; i++)
        {
            int tmp1, tmp2;

            tmp1 = char2Hex(indata[i*2]);

            tmp2 = char2Hex(indata[i*2+1]);

            outdata[n++] = (byte) (((tmp1 << 4) & 0xf0) + (tmp2 & 0x0f));
        }
        return outdata;
    }


    public byte[] hex2Str(byte[] indata, int inlen)
    {

        if (indata.length < inlen)
        {
            return null;
        }

        byte[] outdata = new byte[inlen * 2];

        for (int i = 0; i < inlen; i++)
        {
            int tmp1, tmp2;
            tmp1 = hex2Char((byte) ((indata[i] >> 4) & 0x0f));
            tmp2 = hex2Char((byte) (indata[i] & 0x0f));
            outdata[i*2] = (byte)tmp1;
            outdata[i*2+1] = (byte)tmp2;
        }
        return outdata;
    }

}
