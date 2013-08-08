package yingrong.springdemo.hello;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloTest {
	@Test
	public void testInstallSpring() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-beans.xml");
		PersonService personService = context.getBean("personService",
				PersonService.class);
		Person person = new Person();
		person.setId(123456l);
		personService.save(person);
	}

}
