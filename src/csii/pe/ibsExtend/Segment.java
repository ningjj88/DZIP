// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2007-7-12 15:11:10
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 

package csii.pe.ibsExtend;

import com.csii.pe.transform.*;
import com.csii.pe.transform.stream.Format2Stream;
import com.csii.pe.transform.stream.NameValuePair;
import com.csii.pe.transform.stream.Skip;
import com.csii.pe.transform.stream.var.VariableDecorator;
import com.csii.pe.util.config.*;
import java.io.*;
import java.util.*;

// Referenced classes of package com.csii.pe.transform.stream:
//            Format2Stream, Skip, NameValuePair
@SuppressWarnings("unchecked")
public class Segment extends TransformerListNode
    implements ElementFactoryAware, Format2Stream
{

    public Segment()
    {
        cb = false;
        setName("segment");
    }


	public Object format(Object obj, Map map)
        throws TransformException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        format(((OutputStream) (bytearrayoutputstream)), obj, map);
        return bytearrayoutputstream.toByteArray();
    }

    public void format(OutputStream outputstream, Object obj, Map map)
        throws TransformException
    {
        if(!(obj instanceof Map))
            if(obj == null)
                obj = new HashMap(0);
            else
                throw new TransformException(getClass().getName() + ".format error: invalid input data:" + obj + "for Element:" + getName());
        Object aobj[] = getChildren();
        for(int i = 0; i < aobj.length; i++)
        {
            Element element = (Element)aobj[i];
            try
            {
                if(element instanceof Skip)
                {
                    byte abyte0[] = (byte[])((TransformerElement)element).format(null, (Map)obj);
                    outputstream.write(abyte0);
                } else
                if(element instanceof VariableDecorator)
                {
                    Object obj1 = ((Map)obj).get(element.getName());
                    if(obj1 == null && (element.getChildren()[0] instanceof Segment))
                        obj1 = obj;
                    byte abyte2[] = (byte[])((VariableDecorator)element).format(obj1, map);
                    outputstream.write(abyte2);
                } else
                if(element instanceof Format2Stream)
                {
                    if(element instanceof Segment)
                    {
                        Object obj2 = ((Map)obj).get(element.getName());
                        if(obj2 == null)
                            obj2 = obj;
                        ((Format2Stream)element).format(outputstream, obj2, (Map)obj);
                    } else
                    {
                        ((Format2Stream)element).format(outputstream, ((Map)obj).get(element.getName()), (Map)obj);
                    }
                } else
                if(element instanceof TransformerElement)
                {
                    byte abyte1[] = (byte[])((TransformerElement)element).format(((Map)obj).get(element.getName()), (Map)obj);
                    outputstream.write(abyte1);
                } else
                if(element instanceof Include)
                {
                    Transformer transformer = (Transformer)((Include)element).getElement("s", (Map)obj);
                    Object obj3 = ((Map)obj).get(element.getName());
                    if(obj3 == null)
                        obj3 = obj;
                    byte abyte3[] = (byte[])transformer.format(obj3, map);
                    outputstream.write(abyte3);
                }
                if(cc.getDebug())
                    System.out.println("format->" + element.getName() + "=" + ((Map)obj).get(element.getName()));
            }
            catch(Exception exception)
            {
                throw new TransformException(getClass().getName() + ".format error: " + element.getName(), exception);
            }
        }

    }

    public Object parse(Object obj, Map map)
        throws TransformException
    {
        if(cb && map == null)
            map = new HashMap();
        Object obj1;
        if(obj instanceof InputStream)
            obj1 = (InputStream)obj;
        else
            obj1 = new ByteArrayInputStream((byte[])obj);
        LinkedHashMap linkedhashmap = new LinkedHashMap();
        Object aobj[] = getChildren();
        for(int i = 0; i < aobj.length; i++)
        {
            Element element = (Element)aobj[i];
            if(element instanceof Skip)
                ((TransformerElement)element).parse(obj1, map);
            else
            if(element instanceof Include)
            {
                Include include = (Include)element;
                HashMap hashmap = new HashMap(3);
                if(include.getKeyName() != null)
                {
                    String s = (String)linkedhashmap.get(include.getKeyName());
                    if(s == null)
                        s = (String)map.get(include.getKeyName());
                    hashmap.put(include.getKeyName(), s);
                }
                if(include.getPrefixName() != null)
                {
                    String s1 = (String)linkedhashmap.get(include.getPrefixName());
                    if(s1 == null)
                        s1 = (String)map.get(include.getPrefixName());
                    hashmap.put(include.getPrefixName(), s1);
                }
                TransformerElement transformerelement = (TransformerElement)((Include)element).getElement("p", hashmap);
                Object obj3 = transformerelement.parse(obj1, map);
                if((transformerelement instanceof Segment) && transformerelement.getName().equals("segment"))
                {
                    linkedhashmap.putAll((Map)obj3);
                    if(cb)
                        map.putAll((Map)obj3);
                } else
                {
                    linkedhashmap.put(transformerelement.getName(), obj3);
                    if(cb)
                        map.put(element.getName(), obj3);
                }
                if(cc.getDebug())
                    System.out.println(transformerelement.getName() + "=" + obj3);
            } else
            {
                Object obj2 = ((TransformerElement)element).parse(obj1, ((Map) (map != null ? map : ((Map) (linkedhashmap)))));
                if(obj2 instanceof NameValuePair)
                {
                    NameValuePair namevaluepair = (NameValuePair)obj2;
                    linkedhashmap.put(namevaluepair.getName(), namevaluepair.getValue());
                    if(cb)
                        map.put(namevaluepair.getName(), namevaluepair.getValue());
                    if(cc.getDebug())
                        System.out.println(namevaluepair.getName() + "=" + namevaluepair.getValue());
                } else
                {
                    linkedhashmap.put(element.getName(), obj2);
                    if(cb || element.getName().charAt(0) == '$')
                        map.put(element.getName(), obj2);
                    if(cc.getDebug())
                        System.out.println("parse->"+element.getName() + "=" + obj2);
                }
            }
        }

        return linkedhashmap;
    }

    public void setElementFactory(ElementFactory elementfactory)
    {
        cc = elementfactory;
    }

    public void setParse2Context(boolean flag)
    {
        cb = flag;
    }

    public String toString()
    {
        if(getName().equals("segment"))
            return "<segment>";
        else
            return "<segment name=\"" + getName() + "\">";
    }

    public String[] getAttributes()
    {
        return (new String[] {
            "name"
        });
    }

    private boolean cb;
    private ElementFactory cc;
}