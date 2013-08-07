package yingrong.springdemo.hello;

public class PersonServiceBean {
	private PersonDao personDao = new PersonDaoBean();

	public void save(Person person) {
		personDao.save(person);
	}
}
