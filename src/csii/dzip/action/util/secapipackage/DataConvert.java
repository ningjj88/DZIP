package csii.dzip.action.util.secapipackage;


public class DataConvert
{


    public static int char2Hex(byte indata)
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



    public static int hex2Char(byte indata)
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
    public static int str2Hex(byte[] indata, int inlen, byte[] outdata)
    {
    	/* -- commented by xu.guoyou on 26 June 2009
        if (inlen > 1024)
            return -1;
        */

        /* length cannot divise 2 */
        if ((inlen & 0x01) == 1)
            return -1;

        int n = 0;
        for (int i = 0; i < inlen/2; i++)
        {
            int tmp1, tmp2;

            tmp1 = char2Hex(indata[i*2]);
            if (tmp1 < 0)
            {
                return -1;
            }

            tmp2 = char2Hex(indata[i*2+1]);
            if (tmp2 < 0)
            {
                return -1;
            }

            outdata[n++] = (byte) (((tmp1 << 4) & 0xf0) + (tmp2 & 0x0f));
        }
        return 0;
    }


    public static int hex2Str(byte[] indata, int inlen, byte[] outdata)
    {

        for (int i = 0; i < inlen; i++)
        {
            int tmp1, tmp2;
            tmp1 = hex2Char((byte) ((indata[i] >> 4) & 0x0f));
            if (tmp1 < 0)
            {
                return -1;
            }
            tmp2 = hex2Char((byte) (indata[i] & 0x0f));
            if (tmp2 < 0)
            {
                return -1;
            }
            outdata[i*2] = (byte)tmp1;
            outdata[i*2+1] = (byte)tmp2;
        }
        return 0;
    }
}

