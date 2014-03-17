/*
 * 创建日期 2006-10-25
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package csii.dzip.action.util.secapipackage;

/**
 * @author zhaozheng
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class DataDump
{


    public DataDump()
    {
    }
    
    public static void hexPrint1(String sInfo, byte[] bData, int nDataLen)
    {
/*        int DUMP;
        DUMP = 1;
        if (ShareVar.HsmConf.errlv < 1)
        {
            return;
        }
        if (DUMP == 0)
            return;*/
        byte tmp = 0;
        System.out.println("[" + sInfo + "]" + "length" + "[" + nDataLen + "]");
        int prev = 0;
        int n = 0;
        int i = 0;
        for (i = 0; i < nDataLen; i++)
        {
            if (i == (prev + 16))
            {
                System.out.print("   : ");
                for (int j = prev; j < prev + 16; j++)
                {
                    tmp = bData[j];
                    if (Character.isLetter((char) tmp) == true)
                    {
                        System.out.print((char)tmp);
                    }
                    else if (Character.isDigit((char)tmp) == true)
                    {
                        System.out.print(tmp - '0');
                    }
                    else
                    {
                        System.out.print(" ");
                    }
                }
                prev += 16;
                System.out.println();
            }
            tmp = bData[i];
            if (Integer.toHexString(tmp & 0xff).length() == 1)
            {
                System.out.print("0" + Integer.toHexString(tmp & 0xff) + " ");
            }
            else
            {
                System.out.print(Integer.toHexString(tmp & 0xff) + " ");
            }
        }
        if (i != prev)
        {
            n = i;
            for (; i < prev + 16; i++)
            {
                System.out.print("   ");
            }
        }
        System.out.print("   : ");
        for (i = prev; i < n; i++)
        {
            tmp = bData[i];
            if (Character.isLetter((char) tmp) == true)
            {
                System.out.print((char)(tmp));
            }
            else if (Character.isDigit((char)tmp) == true)
            {
                System.out.print(tmp - '0');
            }
            else
            {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
    
    public static void hexPrint2(String sInfo, char[] bData, int nDataLen)
    {
/*        int DUMP;
        DUMP = 1;
        if (ShareVar.HsmConf.errlv < 1)
        {
            return;
        }
        if (DUMP == 0)
            return;*/
        char tmp = 0;
        System.out.println("[" + sInfo + "]" + "length" + "[" + nDataLen + "]");
        int prev = 0;
        int n = 0;
        int i = 0;
        for (i = 0; i < nDataLen; i++)
        {
            if (i == (prev + 16))
            {
                System.out.print("   : ");
                for (int j = prev; j < prev + 16; j++)
                {
                    tmp = bData[j];
                    if (Character.isLetter((char) tmp) == true)
                    {
                        System.out.print((char)tmp);
                    }
                    else if (Character.isDigit((char)tmp) == true)
                    {
                        System.out.print(tmp - '0');
                    }
                    else
                    {
                        System.out.print(" ");
                    }
                }
                prev += 16;
                System.out.println();
            }
            tmp = bData[i];
            if (Integer.toHexString(tmp & 0xff).length() == 1)
            {
                System.out.print("0" + Integer.toHexString(tmp & 0xff) + " ");
            }
            else
            {
                System.out.print(Integer.toHexString(tmp & 0xff) + " ");
            }
        }
        if (i != prev)
        {
            n = i;
            for (; i < prev + 16; i++)
            {
                System.out.print("   ");
            }
        }
        System.out.print("   : ");
        for (i = prev; i < n; i++)
        {
            tmp = bData[i];
            if (Character.isLetter((char) tmp) == true)
            {
                System.out.print((char)(tmp));
            }
            else if (Character.isDigit((char)tmp) == true)
            {
                System.out.print(tmp - '0');
            }
            else
            {
                System.out.print(" ");
            }
        }
        System.out.println();
    }    
    
    
    public static void hexPrint(String sInfo, byte[] bData, int nDataLen)
    {
        int DUMP;
        DUMP = 1;
        if (ShareVar.HsmConf.errlv < 1)
        {
            return;
        }
        if (DUMP == 0)
            return;
        byte tmp;
        System.out.println("[" + sInfo + "]" + "length" + "[" + nDataLen + "]");
        int prev = 0;
        int n = 0;
        int i = 0;
        for (i = 0; i < nDataLen; i++)
        {
            if (i == (prev + 16))
            {
                System.out.print("    ;");
                for (int j = prev; j < prev + 16; j++)
                {
                    tmp = bData[j];
                    if (Character.isLetter((char) tmp) == true)
                    {
                        System.out.print((char)tmp);
                    }
                    else if (Character.isDigit((char)tmp) == true)
                    {
                        System.out.print(tmp - '0');
                    }
                    else
                    {
                        System.out.print(" ");
                    }
                }
                prev += 16;
                System.out.println();
            }
            tmp = bData[i];
            if (Integer.toHexString(tmp & 0xff).length() == 1)
            {
                System.out.print("0" + Integer.toHexString(tmp & 0xff) + " ");
            }
            else
            {
                System.out.print(Integer.toHexString(tmp & 0xff) + " ");
            }
        }
        if (i != prev)
        {
            n = i;
            for (; i < prev + 16; i++)
            {
                System.out.print("   ");
            }
        }
        System.out.print("    ;");
        for (i = prev; i < n; i++)
        {
            tmp = bData[i];
            if (Character.isLetter((char) tmp) == true)
            {
                System.out.print((char)(tmp));
            }
            else if (Character.isDigit((char)tmp) == true)
            {
                System.out.print(tmp - '0');
            }
            else
            {
                System.out.print(" ");
            }
        }
        System.out.println();
    }


    public static void hexPrint(String sInfo)
    {
        int DUMP;
        DUMP = 0;
        if (DUMP == 0)
            return;

        System.out.println("[" + sInfo + "]");
    }


    public static void hexPrint(String sInfo, int iData)
    {
        int DUMP;
        DUMP = 0;
        if (DUMP == 0)
            return;

        System.out.println("[" + sInfo + "] = " + iData);
    }

}
