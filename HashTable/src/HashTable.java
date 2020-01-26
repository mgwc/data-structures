
import java.util.ArrayList;

public class HashTable<K, V> {
	
	public static void main(String[] args) {
		//HashTable<Integer, Integer> table = new HashTable<Integer, Integer>(10, 0.75);
	}
	
	private class HashNode<X, Y> {
		public HashNode<X, Y> next;
		public X key;
		public Y value;
		
		public HashNode(X key, Y value) {
			this.key = key;
			this.value = value;
			this.next = null;
		}
	}
	
	private ArrayList<HashNode<K, V>> bucketArray;
	private int numBuckets;
	private int numNodes;
	private double loadFactor;
	
	public HashTable(int size, double loadFactor) {
		this.loadFactor = loadFactor;
		this.numNodes = 0;
		this.numBuckets = size;
		this.bucketArray = new ArrayList<HashNode<K, V>>();
		for (int i = 0; i < size; i++) {
			bucketArray.add(null);
		}
	}
	
	public int hashFunction(K key) {
		return key.hashCode() % numBuckets;
	}
	
	public void add(K key, V value) throws NullPointerException {
		if (key == null || value == null) {
			throw new NullPointerException();
		}
		
		numNodes++;
		
		// If loadFactor has been met, resize the array and then add the new node
		if (calcLoad() >= loadFactor) {		
			resize();
			add(key, value);
			return;
		
		// Otherwise, add node to bucket in existing ArrayList 
		} else {
			int bucket = hashFunction(key);
			HashNode<K, V> node = bucketArray.get(bucket);
			
			// If bucket does not already have a chain, new (K, V) pair will be head node
			if (node == null) { 
				bucketArray.set(bucket, new HashNode<K, V>(key, value));
				
			// Otherwise, update existing node w/ matching key or add node to end of chain
			} else {
				while (node.next != null && node.key != key) {
					node = node.next;
				}
				if (node.key == key) {
					node.value = value;
				} else {
					node.next = new HashNode<K, V>(key, value);
					return;
				}
			}
		}
	}
	
	public V remove (K key) throws NullPointerException {
		int bucket = hashFunction(key);
		HashNode<K, V> node = bucketArray.get(bucket);
		if (node == null) {
			throw new NullPointerException();
		} else {
			while (node.key != key && node.next != null && node.next.key != key) {
				node = node.next;
			}
			if (node.key == key) {
				V value = node.value;
				bucketArray.set(bucket, node.next);
				return value;
			} else {
				if (node.next == null) {
					throw new NullPointerException();
				} else {
					V value = node.next.value;
					node.next = node.next.next;
					return value;
				}
			}
		}
	}
	
	public int getBucket(K key) throws NullPointerException {
		int bucket = hashFunction(key);
		HashNode<K, V> node = bucketArray.get(bucket);
		while (node != null) {
			if (node.key == key) {
				return bucket;
			}
			node = node.next;
		}
		throw new NullPointerException();
	}
	
	public void resize() {
		// Create copy of bucketArray, then create new bucketArray with twice as many buckets
		Object bucketArrayCopy = bucketArray.clone();
		@SuppressWarnings("unchecked") 	// bucketArrayCopy is always of type ArrayList<HashNode<K, V>>
		ArrayList<HashNode<K, V>> copyAsArrayList = (ArrayList<HashNode<K, V>>) bucketArrayCopy; 
		this.bucketArray = new ArrayList<HashNode<K, V>>();
		numBuckets *= 2;
		for (int i = 0; i < numBuckets; i++) {
			bucketArray.add(null);
		}
		numNodes = 0;
		
		// Iterate over original nodes, adding each to new bucketArray
		for (HashNode<K, V> node : copyAsArrayList) {
			while (node != null) {
				add(node.key, node.value);
				node = node.next;
			}
		}
		
		return;
	}
	
	public boolean isEmpty() {
		for (HashNode<K, V> node : bucketArray) {
			if (node != null) {
				return false;
			}
		}
		return true;
	}
	
	public double calcLoad() {
		return (double) numNodes / (double) numBuckets;
	}
	
	// toString() method implemented primarily for testing purposes
	@Override
	public String toString() {
		String strResult = "";
		for (HashNode<K, V> node : bucketArray) {
			if (strResult.isEmpty()) {	// If strResult is empty, use proper string convention to start it
				if (node != null) {
					strResult = strResult + "(" + node.key + ", " + node.value + ")";
					while (node.next != null) {
						node = node.next;
						strResult = strResult + ", (" + node.key + ", " + node.value + ")";
					}
				} else {
					strResult = strResult + "(null)";
				}
			} else {	// If strResult is not empty, use middle-of-list string convention
				if (node != null) {
					strResult = strResult + ", (" + node.key + ", " + node.value + ")";
					while (node.next != null) {
						node = node.next;
						strResult = strResult + ", (" + node.key + ", " + node.value + ")";
					}
				} else {
					strResult = strResult + ", (null)";
				}
			}
		}
		return strResult;
	}
}
