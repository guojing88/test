package yingrong.springdemo.hello;

public class PersonServiceBean implements PersonService {
	// private PersonDao personDao = new PersonDaoBean();

	@Override
	public void save(Person person) {
		// personDao.save(person);
		System.out.println("save[Person"+person.toString()+"]");
	}
}
