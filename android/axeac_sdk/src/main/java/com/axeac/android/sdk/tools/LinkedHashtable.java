package com.axeac.android.sdk.tools;

import java.util.Hashtable;
import java.util.Vector;

@SuppressWarnings({ "rawtypes", "serial" })
public class LinkedHashtable extends Hashtable {

	private Vector keys = new Vector();

	public synchronized Vector linkedKeys() {
		return keys;
	}

	// describe:Add element to the set
	/**
	 * 向集合中添加元素
	 * @param key
	 * @param obj
	 * */
	@SuppressWarnings("unchecked")
	public synchronized Object put(Object key, Object obj) {
		keys.removeElement(key);
		keys.addElement(key);
		return super.put(key, obj);
	}

	// describe:get the first element from the set
	/**
	 * 获取集合中第一个元素
	 * */
	public synchronized Object firstElement() {
		if (keys.size() > 0)
			return get(keys.firstElement());
		return null;
	}

	// describe:get the last element from the set
	/**
	 * 获取集合中最后一个元素
	 * */
	public synchronized Object lastElement() {
		if (keys.size() > 0)
			return get(keys.lastElement());
		return null;
	}

	// describe:Remove the element from the set
	/**
	 * 删除集合中某个元素
	 * @param key
	 * the element which will be remove
	 * <br>删除的元素
	 * */
	public synchronized Object remove(Object key) {
		keys.removeElement(key);
		return super.remove(key);
	}

	// describe:Remove the first element from the set
	/**
	 * 删除集合中第一个元素
	 * */
	public synchronized Object removeFirst() {
		Object obj = firstElement();
		if (keys.size() > 0)
			return remove(keys.elementAt(0));
		return obj;
	}

	// describe:Remove the last element from the set
	/**
	 * 删除集合中最后一个元素
	 * */
	public synchronized Object removeLast() {
		Object obj = lastElement();
		if (keys.size() > 0)
			return remove(keys.elementAt(keys.size()));
		return obj;
	}
}