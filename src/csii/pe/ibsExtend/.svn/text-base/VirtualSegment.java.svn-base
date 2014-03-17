/*jadclipse*/ // Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   VirtualSegment.java

package csii.pe.ibsExtend;

import com.csii.pe.transform.*;
import com.csii.pe.transform.object.ObjectTransformer;
import com.csii.pe.transform.stream.Format2Stream;
import com.csii.pe.util.config.*;
import com.csii.pe.validation.rule.Expr;
import com.csii.pe.validation.rule.OgnlExpr;
import java.io.*;
import java.util.*;

// Referenced classes of package com.csii.pe.transform.stream:
//            Format2Stream
@SuppressWarnings("unchecked")
public class VirtualSegment extends TransformerListNode implements ElementFactoryAware, Format2Stream {

	private ElementFactory elementFactory;

	public VirtualSegment() {
	}

	public Object format(Object data, Map context) throws TransformException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		format(((OutputStream) (out)), data, context);
		return out.toByteArray();
	}

	public void format(OutputStream out, Object data, Map context) throws TransformException {
		if (!(data instanceof Map))
			if (data == null)
				data = new HashMap(0);
			else
				throw new TransformException(
					getClass().getName() + ".format error: invalid input data:" + data + "for Element:" + getName());
		Object children[] = getChildren();
		for (int i = 0; i < children.length; i++) {
			Element element = (Element) children[i];
			try {
				if (element instanceof Format2Stream) {
					((Format2Stream) element).format(out, data, (Map) data);
					continue;
				}
				if (element instanceof TransformerElement) {
					byte result[] = (byte[]) ((TransformerElement) element).format(data, (Map) data);
					out.write(result);
					continue;
				}
				if (!(element instanceof Include))
					continue;
				Include include = (Include) element;
				String condition = include.getCondition();
				if (condition != null) {
					Expr expr = new OgnlExpr();
					boolean b = ((Boolean) expr.getValue(condition, null, (Map) data)).booleanValue();
					if (!b)
						continue;
				}
				Transformer includeTransformer = (Transformer) ((Include) element).getElement("f", (Map) data);
				if (includeTransformer instanceof ObjectTransformer) {
					data = includeTransformer.format(data, context);
				} else {
					byte result[] = (byte[]) includeTransformer.format(data, context);
					out.write(result);
				}
			} catch (Exception e) {
				throw new TransformException(getClass().getName() + ".format error: " + element.getName(), e);
			}
		}

	}

	public Object parse(Object in1, Map context) throws TransformException {
		InputStream in;
		if (in1 instanceof InputStream)
			in = (InputStream) in1;
		else
			in = new ByteArrayInputStream((byte[]) in1);
		HashMap resultData = new LinkedHashMap();
		Object children[] = getChildren();
		for (int i = 0; i < children.length; i++) {
			Element element = (Element) children[i];
			if (element instanceof Include) {
				Include include = (Include) element;
				String condition = include.getCondition();
				if (condition != null) {
					Expr expr = new OgnlExpr();
					boolean b = ((Boolean) expr.getValue(condition, null, resultData)).booleanValue();
					if (!b)
						continue;
				}
				Map tempContext = new HashMap(3);
				if (include.getKeyName() != null) {
					String value = (String) resultData.get(include.getKeyName());
					if (value == null)
						value = (String) context.get(include.getKeyName());
					tempContext.put(include.getKeyName(), value);
				}
				if (include.getPrefixName() != null) {
					String value = (String) resultData.get(include.getPrefixName());
					if (value == null)
						value = (String) context.get(include.getPrefixName());
					tempContext.put(include.getPrefixName(), value);
				}
				TransformerElement includeTransformer =
					(TransformerElement) ((Include) element).getElement("p", tempContext);
				if (includeTransformer instanceof ObjectTransformer) {
					resultData = (HashMap) includeTransformer.parse(resultData, context);
				} else {
					Object object = includeTransformer.parse(in, context);
					resultData.putAll((Map) object);
				}
			} else {
				Object object =
					((TransformerElement) element).parse(
						in,
						((Map) (context != null ? context : ((Map) (resultData)))));
				resultData.putAll((Map) object);
			}
			context.putAll(resultData);
		}

		return resultData;
	}

	public void setElementFactory(ElementFactory ef) {
		elementFactory = ef;
	}
}
