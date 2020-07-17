package com.vijayiyer97.util;

public interface Vector<E> {
	public int length();
	public int capacity();
	public void append(E element);
	public void prepend(E element);
	public E pop();
	public E pop(int index);
	public E get(int index);
	public E[] toArray();
}
