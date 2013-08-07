package yingrong.springdemo.hello;

/**
 * @author yingrong
 * 
 */
public class Person {
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + "]";
	}

}
