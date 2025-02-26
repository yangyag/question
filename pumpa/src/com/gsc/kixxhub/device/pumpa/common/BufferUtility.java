package com.gsc.kixxhub.device.pumpa.common;

import java.nio.ByteBuffer;

public class BufferUtility {

	private final int BUFFER_SIZE = 8046;
	
	public ByteBuffer byteArray_To_bytebuffer(byte[] bArray) {
		ByteBuffer buffer = null;
		return byteArray_To_bytebuffer(bArray, buffer, BUFFER_SIZE);
	}
	
	public ByteBuffer byteArray_To_bytebuffer(byte[] bArray, ByteBuffer buffer, int bufsize){
		buffer = ByteBuffer.allocate(bufsize);
		buffer.put(bArray);
		buffer.flip();
		return buffer;
	}
	
	public byte[] byteBuffer_To_byteArray(ByteBuffer bytebuffer, int size) {
		return bytebuffer.array();
	}
	
	public String byteBuffer_To_String(ByteBuffer bytebuffer, int size) {
		byte[] buf = new byte[size];
		bytebuffer.get(buf, 0, size);
		return new String(buf);
	}
	
	public ByteBuffer string_To_bytebuffer(String str){
		ByteBuffer buffer = null;
		return string_To_bytebuffer(str, buffer, BUFFER_SIZE);
	}
	
	public ByteBuffer string_To_bytebuffer(String str, ByteBuffer buffer, int bufsize) {
		return byteArray_To_bytebuffer(str.getBytes(), buffer, bufsize);
	}
}
