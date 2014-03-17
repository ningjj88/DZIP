/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   VariableObject.java

package csii.pe.ibsExtend;

import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.stream.Field;
import com.csii.pe.transform.stream.Format2Stream;
import java.io.OutputStream;
import java.util.Map;

// Referenced classes of package com.csii.pe.transform.stream.var:
//            VariableField
@SuppressWarnings("unchecked")
public abstract class VariableObject extends VariableField
    implements Format2Stream
{

    public VariableObject()
    {
    }

    public Object format(Object data, Map context)
        throws TransformException
    {
        try
        {
            if(data == null)
            {
                if(getDefaultValue() != null)
                    data = getDefaultValue();
                if(data == null && !isOption())
                    throw new TransformException(getClass().getName() + ".format this field can't be null: " + getName() + "Class: " + data.getClass().getName());
            }
            Object object = field.format(data, context);
            return super.format(object, context);
        }
        catch(Exception e)
        {
            throw new TransformException(getClass().getName() + ".format error: " + getName(), e);
        }
    }

    public void format(OutputStream out, Object data, Map context)
        throws TransformException
    {
        try
        {
            if(data == null)
            {
                if(getDefaultValue() != null)
                    data = getDefaultValue();
                if(data == null && !isOption())
                    throw new TransformException(getClass().getName() + ".format this field can't be null: " + getName() + "Class: " + data.getClass().getName());
            }
            Object object = field.format(data, context);
            super.format(out, object, context);
        }
        catch(Exception e)
        {
            throw new TransformException(getClass().getName() + ".format error: " + getName(), e);
        }
    }

    public Object parse(Object in, Map context)
        throws TransformException
    {
        try
        {
            Object object = super.parse(in, context);
            return field.parse(object, context);
        }
        catch(Exception e)
        {
            throw new TransformException(getClass().getName() + ".parse error: " + getName(), e);
        }
    }

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
    }

    public int getLength()
    {
        return super.getLength();
    }

    public void setLength(int i)
    {
        super.setLength(i);
        getField().setLength(i);
    }

    public String getEncoding()
    {
        return super.getEncoding();
    }

    public void setEncoding(String string)
    {
        super.setEncoding(string);
        getField().setEncoding(string);
    }

    public boolean isOption()
    {
        return super.isOption();
    }

    public void setOption(boolean b)
    {
        super.setOption(b);
        getField().setOption(b);
    }

    public String getDefaultValue()
    {
        return super.getDefaultValue();
    }

    public void setDefaultValue(String string)
    {
        super.setDefaultValue(string);
        getField().setDefaultValue(string);
    }

    private Field field;
}
