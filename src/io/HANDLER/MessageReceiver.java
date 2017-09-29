package io.handler;

public interface MessageReceiver {
	public Message poll();
}