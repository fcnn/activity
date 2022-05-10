package com.fy.engineserver.message;

import java.nio.ByteBuffer;


public class GameMessageFactory extends AbstractMessageFactory {

	private static long sequnceNum = 0L;
	public synchronized static long nextSequnceNum(){
		sequnceNum ++;
		if(sequnceNum >= 0x7FFFFFFF){
			sequnceNum = 1L;
		}
		return sequnceNum;
	}

	protected static GameMessageFactory self;

	public static GameMessageFactory getInstance(){
		if(self != null){
			return self;
		}
		synchronized(GameMessageFactory.class){
			if(self != null) return self;
			self = new GameMessageFactory();
		}
		return self;
	}

	public Message newMessage(byte[] messageContent,int offset,int size)
		throws MessageFormatErrorException, ConnectionException,Exception {
		int len = (int)byteArrayToNumber(messageContent,offset,getNumOfByteForMessageLength());
		if(len != size)
			throw new MessageFormatErrorException("message length not match");
		int end = offset + size;
		offset += getNumOfByteForMessageLength();
		long type = byteArrayToNumber(messageContent,offset,4);
		offset += 4;
		long sn = byteArrayToNumber(messageContent,offset,4);
		offset += 4;

    if (type == 0x00000021L) {
      return new TARGET_SKILL_REQ(sn, messageContent, offset, end - offset);
    }else{
      throw new MessageFormatErrorException("unknown message type ["+type+"]");
    }
	}
	/**
	* 将对象转化为数组，可能抛出异常
	*/
	public byte[] objectToByteArray(Object obj) {
		if(obj == null) return new byte[0];
		try {
			java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(out);
			oos.writeObject(obj);
			oos.close();
			return out.toByteArray();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	/**
	* 将数组转化为对象，可能抛出异常
	*/
	public Object byteArrayToObject(byte[] bytes, int offset,int numOfBytes) throws Exception{
		if(numOfBytes == 0) return null;
		java.io.ByteArrayInputStream input = new java.io.ByteArrayInputStream(bytes,offset,numOfBytes);
		java.io.ObjectInputStream o = new java.io.ObjectInputStream(input);
		Object obj = o.readObject();
		o.close();
		return obj;
	}
}
