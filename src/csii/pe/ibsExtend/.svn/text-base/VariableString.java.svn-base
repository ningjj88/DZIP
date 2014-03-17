/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   VariableString.java

package csii.pe.ibsExtend;

import com.csii.pe.transform.stream.field.StringTransformer;

// Referenced classes of package com.csii.pe.transform.stream.var:
//            VariableObject

public class VariableString extends VariableObject
{

    public VariableString()
    {
        string = new StringTransformer();
        setField(string);
    }

    public void setXml(boolean b)
    {
        string.setXml(b);
    }

    public boolean isXml()
    {
        return string.isXml();
    }

    public boolean isTrim()
    {
        return string.isTrim();
    }

    public void setTrim(boolean b)
    {
        string.setTrim(b);
    }

    public boolean isQuotTrim()
    {
        return string.isQuotTrim();
    }

    public void setQuotTrim(boolean b)
    {
        string.setQuotTrim(b);
    }

    public boolean isU3000Trim()
    {
        return string.isU3000Trim();
    }

    public void setU3000Trim(boolean b)
    {
        string.setU3000Trim(b);
    }

    public void setFormatPattern(String pattern)
    {
        string.setFormatPattern(pattern);
    }

    public String getFormatPattern()
    {
        return string.getFormatPattern();
    }

    public void setMapping(String mapping)
    {
        string.setMapping(mapping);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer("<varString");
        if(getName() != null)
            sb.append(" name=\"").append(getName()).append("\"");
        if(getType() != null)
            sb.append(" type=\"").append(getType()).append("\"");
        if(getDefaultValue() != null)
            sb.append(" defaultValue=\"").append(getDefaultValue()).append("\"");
        if(!isTrim())
            sb.append(" trim=\"").append(isTrim()).append("\"");
        if(string.getFormatPattern() != null)
            sb.append(" formatPattern=\"").append(string.getFormatPattern()).append("\"");
        sb.append("/>");
        return sb.toString();
    }

    public String[] getAttributes()
    {
        return (new String[] {
            "name", "type", "defaultValue", "trim", "formatPattern"
        });
    }

    private StringTransformer string;
}
