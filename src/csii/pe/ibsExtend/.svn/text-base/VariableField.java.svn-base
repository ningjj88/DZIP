/*jadclipse*/ // Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   VariableField.java

package csii.pe.ibsExtend;

import com.csii.pe.transform.TransformException;
import com.csii.pe.transform.TransformRuntimeException;
import com.csii.pe.transform.stream.*;
import java.io.*;
import java.util.Map;
@SuppressWarnings("unchecked")
public class VariableField extends Field implements Format2Stream {
	private interface InnerTransformer {

		public abstract byte[] formatStream(Object obj, Map map) throws TransformException;

		public abstract void formatStream(OutputStream outputstream, Object obj, Map map) throws TransformException;

		public abstract Object parseStream(InputStream inputstream, Map map) throws TransformException;
	}

	private class TypeBTransformer implements InnerTransformer {

		TypeBTransformer() {
			super();
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			try {
				if (object == null)
					return new byte[getWidth()];
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					byte result[] = new byte[getWidth() + bytes.length];
					int realLength = bytes.length;
					for (int i = 0; i < getWidth(); i++)
						result[i] = (byte) (realLength >> (getWidth() - i - 1) * 8 & 255);

					System.arraycopy(bytes, 0, result, getWidth(), bytes.length);
					return result;
				} else {
					throw new TransformException(
						"BinaryLengthVariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString.format error: " + getName(), e);
			}
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			try {
				if (object == null)
					out.write(new byte[getWidth()]);
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					byte lengthBytes[] = new byte[getWidth()];
					int realLength = bytes.length;
					for (int i = 0; i < getWidth(); i++)
						lengthBytes[i] = (byte) (realLength >> (getWidth() - i - 1) * 8 & 255);

					out.write(lengthBytes);
					out.write(bytes);
				} else {
					throw new TransformException(
						"BinaryLengthVariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString.format error: " + getName(), e);
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			try {
				byte lengthBytes[] = new byte[getWidth()];
				in.read(lengthBytes);
				int realLength = 0;
				for (int i = 0; i < lengthBytes.length; i++)
					realLength += (lengthBytes[i] & 255) << (getWidth() - i - 1) * 8;

				byte tmpBytes[] = new byte[realLength];
				in.read(tmpBytes);
				return tmpBytes;
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString:" + getName(), e);
			}
		}
	}

	private class TypeBWithZeroEndTransformer extends TypeBTransformer {

		TypeBWithZeroEndTransformer() {
			super();
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			byte b[] = super.formatStream(object, context);
			byte bb[] = new byte[b.length + 1];
			System.arraycopy(b, 0, bb, 0, b.length);
			return bb;
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			super.formatStream(out, object, context);
			try {
				out.write(0);
			} catch (IOException ioexception) {
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			Object object = super.parseStream(in, context);
			try {
				in.read();
			} catch (IOException ioexception) {
			}
			return object;
		}
	}

	private class TypeDTransformer implements InnerTransformer {

		private byte delimeter[];

		public TypeDTransformer(String delimeter) {
			super();
			this.delimeter = new byte[1];
			if (delimeter.length() == 1)
				this.delimeter[0] = (byte) delimeter.charAt(0);
			else
				this.delimeter[0] = (byte) (Byte.decode(delimeter).byteValue() & 255);
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			try {
				if (object == null)
					return delimeter;
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					byte result[] = new byte[bytes.length + 1];
					System.arraycopy(bytes, 0, result, 0, bytes.length);
					System.arraycopy(delimeter, 0, result, bytes.length, delimeter.length);
					return result;
				} else {
					throw new TransformException(
						"BinaryLengthVariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString.format error: " + getName(), e);
			}
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			try {
				if (object == null) {
					out.write(delimeter);
					return;
				}
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					out.write(bytes);
					out.write(delimeter);
				} else {
					throw new TransformException(
						"BinaryLengthVariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString.format error: " + getName(), e);
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			do try {
					int oneByte = in.read();
					if ((byte) oneByte == delimeter[0] || oneByte == -1)
						return out.toByteArray();
				if (delimeter[0]=="}".getBytes()[0]&&(byte) oneByte == ":".getBytes()[0])
					return out.toByteArray();
					out.write(oneByte);
				} catch (Exception e) {
					throw new TransformException("BinaryLengthVariableString:" + getName(), e);
				} while (true);
		}
	}

	private class TypeFTransformer implements InnerTransformer {

		private byte delimeter[];

		public TypeFTransformer(String delimeter) {
			super();
			if (delimeter.equals("UNIX"))
				this.delimeter = VariableField.UNIX;
			else
				this.delimeter = VariableField.DOS;
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			try {
				if (object == null)
					return delimeter;
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					byte result[] = new byte[bytes.length + delimeter.length];
					System.arraycopy(bytes, 0, result, 0, bytes.length);
					System.arraycopy(delimeter, 0, result, bytes.length, delimeter.length);
					return result;
				} else {
					throw new TransformException(
						"BinaryLengthVariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString.format error: " + getName(), e);
			}
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			try {
				if (object == null)
					out.write(delimeter);
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					out.write(bytes);
					out.write(delimeter);
				} else {
					throw new TransformException(
						"BinaryLengthVariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("BinaryLengthVariableString.format error: " + getName(), e);
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			do try {
					int oneByte;
					do {
						oneByte = in.read();
						if ((byte) oneByte == 10 || oneByte == -1)
							return out.toByteArray();
					} while ((byte) oneByte == 13);
					out.write(oneByte);
				} catch (Exception e) {
					throw new TransformException("BinaryLengthVariableString:" + getName(), e);
				}
			while (true);
		}
	}

	private class TypeLTransformer implements InnerTransformer {

		TypeLTransformer() {
			super();
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			try {
				if (object == null) {
					byte lengthBytes[] = Util.prefixZero(0L, getWidth()).getBytes(getEncoding());
					return lengthBytes;
				}
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					int realLength = bytes.length;
					byte lengthBytes[] = Util.prefixZero(realLength, getWidth()).getBytes(getEncoding());
					byte result[] = new byte[lengthBytes.length + realLength];
					System.arraycopy(lengthBytes, 0, result, 0, lengthBytes.length);
					System.arraycopy(bytes, 0, result, lengthBytes.length, realLength);
					return result;
				} else {
					throw new TransformException(
						"VariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("VariableString.format error: " + getName(), e);
			}
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			try {
				if (object == null) {
					byte lengthBytes[] = Util.prefixZero(0L, getWidth()).getBytes(getEncoding());
					out.write(lengthBytes);
				}
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					int realLength = bytes.length;
					byte lengthBytes[] = Util.prefixZero(realLength, getWidth()).getBytes(getEncoding());
					out.write(lengthBytes);
					out.write(bytes);
				} else {
					throw new TransformException(
						"VariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("VariableString.format error: " + getName(), e);
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			try {
				byte lengthBytes[] = new byte[getWidth()];
				int realNumber = in.read(lengthBytes);
				if (realNumber == -1 || realNumber == 0) {
					throw new TransformException(getClass().getName() + ":" + getName() + "end of stream is reached");
				} else {
					int realLength = Integer.parseInt((new String(lengthBytes, getEncoding())).trim());
					byte tmpBytes[] = new byte[realLength];
					in.read(tmpBytes);
					return tmpBytes;
				}
			} catch (Exception e) {
				throw new TransformException(getClass().getName() + ":" + getName(), e);
			}
		}
	}

	private class TypeLWithZeroEndTransformer extends TypeLTransformer {

		TypeLWithZeroEndTransformer() {
			super();
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			byte b[] = super.formatStream(object, context);
			byte bb[] = new byte[b.length + 1];
			System.arraycopy(b, 0, bb, 0, b.length);
			return bb;
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			super.formatStream(out, object, context);
			try {
				out.write(0);
			} catch (IOException ioexception) {
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			Object object = super.parseStream(in, context);
			try {
				in.read();
			} catch (IOException ioexception) {
			}
			return object;
		}
	}

	private class TypePTransformer implements InnerTransformer {

		TypePTransformer() {
			super();
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			try {
				if (object == null)
					return Util.packDecimal(Util.prefixZero(0L, getWidth() * 2));
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					int realLength = bytes.length;
					byte lengthBytes[] = Util.packDecimal(Util.prefixZero(realLength, getWidth() * 2));
					byte result[] = new byte[lengthBytes.length + realLength];
					System.arraycopy(lengthBytes, 0, result, 0, lengthBytes.length);
					System.arraycopy(bytes, 0, result, lengthBytes.length, realLength);
					return result;
				} else {
					throw new TransformException(
						"VariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("VariableString.format error: " + getName(), e);
			}
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			try {
				if (object == null)
					out.write(Util.packDecimal(Util.prefixZero(0L, getWidth() * 2)));
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					int realLength = bytes.length;
					byte lengthBytes[] = Util.packDecimal(Util.prefixZero(realLength, getWidth() * 2));
					out.write(lengthBytes);
					out.write(bytes);
				} else {
					throw new TransformException(
						"VariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("VariableString.format error: " + getName(), e);
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			try {
				byte lengthBytes[] = new byte[getWidth()];
				int realNumber = in.read(lengthBytes);
				if (realNumber == -1 || realNumber == 0) {
					throw new TransformException(getClass().getName() + ":" + getName() + "end of stream is reached");
				} else {
					String tmpStr = Util.unPackDecimal(lengthBytes);
					int realLength = Integer.parseInt(tmpStr);
					byte tmpBytes[] = new byte[realLength];
					in.read(tmpBytes);
					return tmpBytes;
				}
			} catch (Exception e) {
				throw new TransformException(getClass().getName() + ":" + getName(), e);
			}
		}
	}

	private class TypePWithZeroEndTransformer extends TypePTransformer {

		TypePWithZeroEndTransformer() {
			super();
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			byte b[] = super.formatStream(object, context);
			byte bb[] = new byte[b.length + 1];
			System.arraycopy(b, 0, bb, 0, b.length);
			return bb;
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			super.formatStream(out, object, context);
			try {
				out.write(0);
			} catch (IOException ioexception) {
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			Object object = super.parseStream(in, context);
			try {
				in.read();
			} catch (IOException ioexception) {
			}
			return object;
		}
	}

	private class TypeRTransformer implements InnerTransformer {

		private String refField;

		public TypeRTransformer(String refField) {
			super();
			this.refField = refField;
		}

		public byte[] formatStream(Object object, Map context) throws TransformException {
			try {
				if (object == null)
					return new byte[0];
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					int realLength = bytes.length;
					context.put(refField, new Integer(realLength));
					return bytes;
				} else {
					throw new TransformException(
						"VariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("VariableString.format error: " + getName(), e);
			}
		}

		public void formatStream(OutputStream out, Object object, Map context) throws TransformException {
			try {
				if (object == null)
					return;
				if (object instanceof byte[]) {
					byte bytes[] = (byte[]) object;
					int realLength = bytes.length;
					context.put(refField, new Integer(realLength));
					out.write(bytes);
				} else {
					throw new TransformException(
						"VariableString.format unsupported type: "
							+ getName()
							+ "Class: "
							+ object.getClass().getName());
				}
			} catch (Exception e) {
				throw new TransformException("VariableString.format error: " + getName(), e);
			}
		}

		public Object parseStream(InputStream in, Map context) throws TransformException {
			try {
				Object object = context.get(refField);
				if (object == null)
					throw new TransformException(
						getClass().getName() + ".parse error: referred field can't be found" + refField);
				Integer length;
				if (object instanceof String)
					length = new Integer((String) object);
				else if (object instanceof Integer)
					length = (Integer) object;
				else
					throw new TransformException(
						getClass().getName() + ".parse error: the type of referred field is invalid" + refField);
				int realLength = length.intValue();
				byte tmpBytes[] = new byte[realLength];
				in.read(tmpBytes);
				return tmpBytes;
			} catch (Exception e) {
				throw new TransformException("VariableString:" + getName(), e);
			}
		}
	}
	private static byte DOS[] = { 13, 10 };
	private static byte UNIX[] = { 10 };
	private InnerTransformer innerTransformer;
	private String type;

	private int width;

	public VariableField() {
	}

	public Object format(Object data, Map context) throws TransformException {
		return innerTransformer.formatStream(data, context);
	}

	public void format(OutputStream out, Object data, Map context) throws TransformException {
		innerTransformer.formatStream(out, data, context);
	}

	public String getType() {
		return type;
	}

	private int getWidth() {
		return width;
	}

	public Object parse(Object data1, Map context) throws TransformException {
		InputStream data;
		if (data1 instanceof byte[])
			data = new ByteArrayInputStream((byte[]) data1);
		else
			data = (InputStream) data1;
		return innerTransformer.parseStream(data, context);
	}

	public void setType(String string) {
		type = string;
		if (type.charAt(0) == 'L') {
			innerTransformer = new TypeLTransformer();
			setWidth(type.length());
		} else if (type.charAt(0) == 'l') {
			innerTransformer = new TypeLWithZeroEndTransformer();
			setWidth(type.length());
		} else if (type.charAt(0) == 'P') {
			innerTransformer = new TypePTransformer();
			setWidth(type.length());
		} else if (type.charAt(0) == 'p') {
			innerTransformer = new TypePWithZeroEndTransformer();
			setWidth(type.length());
		} else if (type.charAt(0) == 'B') {
			innerTransformer = new TypeBTransformer();
			setWidth(type.length());
		} else if (type.charAt(0) == 'b') {
			innerTransformer = new TypeBWithZeroEndTransformer();
			setWidth(type.length());
		} else if (type.charAt(0) == 'D')
			innerTransformer = new TypeDTransformer(type.substring(1));
		else if (type.charAt(0) == 'R')
			innerTransformer = new TypeRTransformer(type.substring(1));
		else if (type.charAt(0) == 'F')
			innerTransformer = new TypeFTransformer(type.substring(1));
		else
			throw new TransformRuntimeException("Unsupported Variable String Type ");
	}

	private void setWidth(int i) {
		width = i;
	}

}
