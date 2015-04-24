/*******************************************************************************
 * Copyright (c) Jun 27, 2011 Zend Technologies Ltd. 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 *******************************************************************************/
package org.zend.sdklib.mapping;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.zend.sdklib.internal.mapping.Mapping;
import org.zend.sdklib.internal.mapping.MappingEntry;
import org.zend.sdklib.mapping.IMappingEntry.Type;

/**
 * Abstract implementation of {@link IMappingLoader}. It is basic loader for
 * resource mapping stored in a properties file.
 * 
 * @author Wojciech Galanciak, 2011
 * 
 */
public abstract class PropertiesBasedMappingLoader implements IMappingLoader {

	private static final String TAB = "\t";
	public static final String EXCLUDES = ".excludes";
	public static final String SEPARATOR = ",";
	public static final String INCLUDES = ".includes";
	public static final String GLOBAL = "**/";

	final String EOL = System.getProperty("line.separator");

	private class LineReader {
		public LineReader(InputStream inStream) {
			this.inStream = inStream;
			inByteBuf = new byte[8192];
		}

		byte[] inByteBuf;
		char[] inCharBuf;
		char[] lineBuf = new char[1024];
		int inLimit = 0;
		int inOff = 0;
		InputStream inStream;
		Reader reader;

		int readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isCommentLine = false;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = (inStream == null) ? reader.read(inCharBuf)
							: inStream.read(inByteBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0 || isCommentLine) {
							return -1;
						}
						return len;
					}
				}
				if (inStream != null) {
					// The line below is equivalent to calling a
					// ISO8859-1 decoder.
					c = (char) (0xff & inByteBuf[inOff++]);
				} else {
					c = inCharBuf[inOff++];
				}
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						isCommentLine = true;
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					// flip the preceding backslash flag
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				} else {
					// reached EOL
					if (isCommentLine || len == 0) {
						isCommentLine = false;
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = (inStream == null) ? reader.read(inCharBuf)
								: inStream.read(inByteBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						// skip the leading whitespace characters in following
						// line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zend.sdklib.mapping.IMappingLoader#load(java.io.InputStream)
	 */
	@Override
	public List<IMappingEntry> load(InputStream stream) throws IOException {
		List<IMappingEntry> mapping = new ArrayList<IMappingEntry>();
		if (stream != null) {
			mapping = loadMapping(stream);
			stream.close();
		}
		return mapping;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.zend.sdklib.mapping.IMappingLoader#store(org.zend.sdklib.mapping.
	 * IResourceMapping, java.io.File)
	 */
	@Override
	public void store(IMappingModel model, File output) throws IOException {
		byte[] bytes = getByteArray(model);
		OutputStream out = new FileOutputStream(output);
		out.write(bytes);
		out.close();
	}

	protected List<IMapping> getMappings(String[] result) throws IOException {
		List<IMapping> mappings = new ArrayList<IMapping>();
		for (int i = 0; i < result.length; i++) {
			String file = result[i].trim();
			if (file.isEmpty()) {
				continue;
			}
			boolean isGlobal = file.startsWith(GLOBAL);
			if (isGlobal) {
				file = file.substring(GLOBAL.length());
			}
			mappings.add(new Mapping(file, isGlobal));
		}
		return mappings;
	}

	protected byte[] getByteArray(IMappingModel model) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		List<IMappingEntry> entries = model.getEnties();
		for (IMappingEntry entry : entries) {
			String entryString = getEntry(entry);
			result.write(entryString.getBytes());
			result.write(EOL.getBytes());
		}
		return result.toByteArray();
	}

	protected List<IMappingEntry> loadMapping(InputStream stream)
			throws IOException {
		LineReader reader = new LineReader(stream);
		List<IMappingEntry> result = new ArrayList<IMappingEntry>();
		char[] convtBuf = new char[1024];
		int limit;
		int keyLen;
		int valueStart;
		char c;
		boolean hasSep;
		boolean precedingBackslash;
		while ((limit = reader.readLine()) >= 0) {
			c = 0;
			keyLen = 0;
			valueStart = limit;
			hasSep = false;
			precedingBackslash = false;
			while (keyLen < limit) {
				c = reader.lineBuf[keyLen];
				// need check if escaped.
				if ((c == '=' || c == ':') && !precedingBackslash) {
					valueStart = keyLen + 1;
					hasSep = true;
					break;
				} else if ((c == ' ' || c == '\t' || c == '\f')
						&& !precedingBackslash) {
					valueStart = keyLen + 1;
					break;
				}
				if (c == '\\') {
					precedingBackslash = !precedingBackslash;
				} else {
					precedingBackslash = false;
				}
				keyLen++;
			}
			while (valueStart < limit) {
				c = reader.lineBuf[valueStart];
				if (c != ' ' && c != '\t' && c != '\f') {
					if (!hasSep && (c == '=' || c == ':')) {
						hasSep = true;
					} else {
						break;
					}
				}
				valueStart++;
			}
			String key = loadConvert(reader.lineBuf, 0, keyLen, convtBuf);
			String value = loadConvert(reader.lineBuf, valueStart, limit
					- valueStart, convtBuf);
	
			if (key != null && value != null) {
				String[] files = value.trim().split(SEPARATOR);
				List<IMapping> mappings = getMappings(files);
				String folderName = key.substring(0, key.indexOf("."));
				String kind = key.substring(key.indexOf(".")).trim();
				Type type = INCLUDES.equals(kind) ? Type.INCLUDE : Type.EXCLUDE;
				result.add(new MappingEntry(folderName, mappings, type));
			}
		}
		return result;
	}

	private String getEntry(IMappingEntry entry) {
		StringBuilder result = new StringBuilder();
		result.append(entry.getFolder());
		result.append(entry.getType() == Type.INCLUDE ? INCLUDES : EXCLUDES);
		result.append(" = ");
		result.append(getValue(entry.getMappings()));
		return result.toString();
	}

	private String getValue(List<IMapping> mappings) {
		StringBuilder result = new StringBuilder();
		int size = mappings.size() - 1;
		for (IMapping entry : mappings) {
			String file = entry.getPath();
			if (entry.isGlobal()) {
				file = GLOBAL + file;
			}
			result.append(file);
			if (size-- > 0) {
				result.append(SEPARATOR);
				result.append("\\");
				result.append(EOL);
				result.append(TAB);
				result.append(TAB);
			}
		}
		return result.toString();
	}

	/*
	 * Converts encoded &#92;uxxxx to unicode chars and changes special saved
	 * chars to their original forms
	 */
	private String loadConvert(char[] in, int off, int len, char[] convtBuf) {
		if (convtBuf.length < len) {
			int newLen = len * 2;
			if (newLen < 0) {
				newLen = Integer.MAX_VALUE;
			}
			convtBuf = new char[newLen];
		}
		char aChar;
		char[] out = convtBuf;
		int outLen = 0;
		int end = off + len;

		while (off < end) {
			aChar = in[off++];
			if (aChar == '\\') {
				aChar = in[off++];
				if (aChar == 'u') {
					// Read the xxxx
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = in[off++];
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed \\uxxxx encoding.");
						}
					}
					out[outLen++] = (char) value;
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					out[outLen++] = aChar;
				}
			} else {
				out[outLen++] = aChar;
			}
		}
		return new String(out, 0, outLen);
	}

}
