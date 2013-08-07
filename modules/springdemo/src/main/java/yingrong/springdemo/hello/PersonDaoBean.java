package yingrong.springdemo.hello;

public class PersonDaoBean implements PersonDao {

	@Override
	public void save(Person person) {
		System.out.println("save" + person.toString());
	}

}
