package com.magus;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class CacheMap<T> extends LinkedHashMap<String, SoftReference<T>>{
	private int cacheLenght = 15;
	public CacheMap() {
		this(16,0.5f,true);
	}
	public CacheMap(int size) {
		this(16,0.5f,true);
		this.cacheLenght = size;
	}
	
	public CacheMap(int initialCapacity, float loadFactor,boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}
	@Override
	public boolean removeEldestEntry(Map.Entry<String, SoftReference<T>> eldest) {
		return size()>cacheLenght;
	}
}
