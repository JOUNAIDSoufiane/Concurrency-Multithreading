package ndfs.mcndfs_2_improved;

public class Counter {
	private int value;
	
	Counter(int value) {
		this.value = value;
	}
	
	public Counter incrementAndGet() {
		this.value++;
		return this;
	}
	
	public int get() {
		return value;
	}
	
	public Counter decrementAndGet() {
		this.value--;
		return this;
	}
}
