## [Spring生命周期](http://doc.javanb.com/spring-framework-reference-zh-2-0-5/ch03s05.html#beans-factory-lifecycle)

### Lifecycle接口   

   实现InitializingBean和DisposableBean这两个接口的bean在初始化和析构时容器会调用前者的afterPropertiesSet()方法，以及后者的destroy()方法。

### 初始化回调

   实现org.springframework.beans.factory.InitializingBean接口允许容器在设置好bean的所有必要属性后，执行初始化事宜。InitializingBean接口仅指定了一个方法：

```	
	void afterPropertiesSet() throws Exception;
```

通常，要避免使用InitializingBean接口（而且不鼓励使用该接口，因为这样会将代码和Spring耦合起来）可以在Bean定义中指定一个普通的初始化方法，即在XML配置文件中通过指定init-method属性来完成。如下面的定义所示：


	<bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>
</n>

	public class ExampleBean {
	    
	    public void init() {
	        // do some initialization work
	    }
	}


（效果）与下面完全一样

	<bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
</n>

	public class AnotherExampleBean implements InitializingBean {
	    
	    public void afterPropertiesSet() {
	        // do some initialization work
	    }
	}

但是没有将代码与Spring耦合在一起。

### 析构回调

	同初始化回调

### 缺省的初始化和析构方法

生命周期回调方法的名称最好在一个项目范围内标准化，这样团队中的开发人员就可以使用同样的方法名称，并且确保了某种程度的一致性。

```java
	public class DefaultBlogService implements BlogService {

	    private BlogDao blogDao;

	    public void setBlogDao(BlogDao blogDao) {
	        this.blogDao = blogDao;
	    }

	    // this is (unsurprisingly) the initialization callback method
	    public void init() {
	        if (this.blogDao == null) {
	            throw new IllegalStateException("The [blogDao] property must be set.");
	        }
	    }
	}
```

	<beans default-init-method="init">

	    <bean id="blogService" class="com.foo.DefaultBlogService">
	        <property name="blogDao" ref="blogDao" />
	    </bean>

	</beans>

### 在非web应用中优雅地关闭Spring IoC容器

* 在基于web的ApplicationContext实现中已有相应的代码来处理关闭web应用时如何恰当地关闭Spring IoC容器

如果你正在一个非web应用的环境下使用Spring的IoC容器,想让容器优雅的关闭，并调用singleton bean上的相应析构回调方法，你需要在JVM里注册一个“关闭钩子”（shutdown hook）。

这将会确保你的Spring IoC容器被恰当关闭，以及所有由单例持有的资源都会被释放（当然，为你的单例配置销毁回调，并正确实现销毁回调方法，依然是你的工作）。

```java
	import org.springframework.context.support.AbstractApplicationContext;
	import org.springframework.context.support.ClassPathXmlApplicationContext;

	public final class Boot {

	    public static void main(final String[] args) throws Exception {
	        AbstractApplicationContext ctx
	            = new ClassPathXmlApplicationContext(new String []{"beans.xml"});

	        // add a shutdown hook for the above context... 
	        ctx.registerShutdownHook();

	        // app runs here...

	        // main method exits, hook is called prior to the app shutting down...
	    }
	}
```
