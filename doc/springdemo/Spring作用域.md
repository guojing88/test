## [Spring作用域](http://doc.javanb.com/spring-framework-reference-zh-2-0-5/ch03s04.html) 

### 用法

```
	<bean id="role" class="spring.chapter2.maryGame.Role" scope="singleton"/>
```

scope属性用来配置标识bean的作用域。

### 一、singleton (默认)   
> 单例。每个Spring容器只创建这个bean的唯一实例。	
> 与GOF中单例区别：GOF中单例设计模式表示在一个ClassLoader中只有一个class存在；而这里表示一个Spring容器对应一个bean。

### 二、prototype   

> 每一次请求（将其注入到另一个bean中，或者以程序的方式调用容器的getBean()方法）都会产生一个新的bean实例，相当与一个new的操作。	

> 不管何种作用域，容器都会调用所有对象的`初始化生命周期回调方法`（init-method），而对prototype而言，任何配置好的`析构生命周期回调方法`都将不会被调用。	

>  Spring不能对一个prototype bean的整个生命周期负责，容器在初始化、配置、装饰或者是装配完一个prototype实例后，将它交给客户端后就不再保留对该bean的引用,即随后就对该prototype实例不闻不问了。清除prototype作用域的对象并释放任何prototype bean所持有的昂贵资源，都是客户端代码的职责。


### 三、request   

```
	<bean id="loginAction" class="com.foo.LoginAction" scope="request"/>
```

针对每次HTTP请求，Spring容器LoginAction bean定义创建一个全新 bean实例，且该LoginAction bean实例仅在当前HTTP request内有效，因此可以根据需要放心的更改所建实例的内部状态，而其他请求中根据LoginAction bean定义创建的实例，将不会看到这些特定于某个请求的状态变化。当处理请求结束，request作用域的bean实例将被销毁。

### 四、session    

```
	<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>
```

针对某个HTTP Session，Spring容器会根据userPreferences bean定义创建一个全新的userPreferences bean实例，且该userPreferences bean仅在当前HTTP Session内有效。与request作用域一样，你可以根据需要放心的更改所创建实例的内部状态，而别的HTTP Session中根据userPreferences创建的实例，将不会看到这些特定于某个HTTP Session的状态变化。当HTTP Session最终被废弃的时候，在该HTTP Session作用域内的bean也会被废弃掉。

### 五、global session  

```
	<bean id="userPreferences" class="com.foo.UserPreferences" scope="globalSession"/>
```

global session作用域类似于标准的HTTP Session作用域，不过它仅仅在基于portlet的web应用中才有意义。[Portlet](http://baike.baidu.com/view/58961.htm)规范定义了全局Session的概念，它被所有构成某个portlet web应用的各种不同的portlet所共享。在global session作用域中定义的bean被限定于全局portlet Session的生命周期范围内。		

请注意，假如你在编写一个标准的基于Servlet的web应用，并且定义了一个或多个具有global session作用域的bean，系统会使用标准的HTTP Session作用域，并且不会引起任何错误。


* 注意

	request、session、global session的作用域仅仅在使用基于web的Spring ApplicationContext实现（如XmlWebApplicationContext）时有用。如果在普通的Spring IoC容器中，比如像XmlBeanFactory或ClassPathXmlApplicationContext，尝试使用这些作用域，你将会得到一个IllegalStateException异常（未知的bean作用域）

* 初始化web配置

	要使用request、session和 global session作用域的bean（即具有web作用域的bean），在开始设置bean定义之前，还要做少量的初始配置。请注意，假如你只想要“常规的”作用域，也就是singleton和prototype，就不需要这一额外的设置。
	在目前的情况下，根据你的特定servlet环境，有多种方法来完成这一初始设置。如果你使用的是Servlet 2.4及以上的web容器，那么你仅需要在web应用的XML声明文件web.xml中增加下述ContextListener即可			

```
	<web-app>
	  ...
	  <listener>
	    <listener-class>org.springframework.web.context.request.RequestContextListener</listener-class>
	  </listener>
	  ...
	</web-app>
```     
         
   如果你用的是早期版本的web容器（Servlet 2.4以前），那么你要使用一个javax.servlet.Filter的实现。请看下面的web.xml配置片段：    

```
	<web-app>
	  ..
	  <filter> 
	    <filter-name>requestContextFilter</filter-name> 
	    <filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
	  </filter> 
	  <filter-mapping> 
	    <filter-name>requestContextFilter</filter-name> 
	    <url-pattern>/*</url-pattern>
	  </filter-mapping>
	  ...
	</web-app>
```

RequestContextListener和RequestContextFilter两个类做的都是同样的工作：将HTTP request对象绑定到为该请求提供服务的Thread。这使得具有request和session作用域的bean能够在后面的调用链中被访问到。

### 六、自定义    


### 七、作用域bean与依赖

	能够在HTTP request或者Session（甚至自定义）作用域中定义bean固然很好，但是Spring IoC容器除了管理对象（bean）的实例化，同时还负责协作者（或者叫依赖）的实例化。如果你打算将一个Http request范围的bean注入到另一个bean中，那么需要注入一个AOP代理来替代被注入的作用域bean。也就是说，你需要注入一个代理对象，该对象具有与被代理对象一样的公共接口，而容器则可以足够智能的从相关作用域中（比如一个HTTP request）获取到真实的目标对象，并把方法调用委派给实际的对象。

* 注意

	<aop:scoped-proxy/>不能和作用域为singleton或prototype的bean一起使用。为singleton bean创建一个scoped proxy将抛出BeanCreationException异常。
	让我们看一下将相关作用域bean作为依赖的配置，配置并不复杂（只有一行），但是理解“为何这么做”以及“如何做”是很重要的。
```
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xmlns:aop="http://www.springframework.org/schema/aop"
	       xsi:schemaLocation="
	http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.0.xsd
	http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.0.xsd">

	    <!-- a HTTP Session-scoped bean exposed as a proxy -->
	    <bean id="userPreferences" class="com.foo.UserPreferences" scope="session">
	          
	          <!-- this next element effects the proxying of the surrounding bean -->
	          <aop:scoped-proxy/>
	    </bean>
	    
	    <!-- a singleton-scoped bean injected with a proxy to the above bean -->
	    <bean id="userService" class="com.foo.SimpleUserService">
	    
	        <!-- a reference to the proxied 'userPreferences' bean -->
	        <property name="userPreferences" ref="userPreferences"/>

	    </bean>
	</beans>
```


在XML配置文件中，要创建一个作用域bean的代理，只需要在作用域bean定义里插入一个<aop:scoped-proxy/>子元素即可（你可能还需要在classpath里包含CGLIB库，这样容器就能够实现基于class的代理；还可能要使用基于XSD的配置）。上述XML配置展示了“如何做”，现在讨论“为何这么做”。在作用域为request、session以及globalSession的bean定义里，为什么需要这个<aop:scoped-proxy/>元素呢？下面我们从去掉<aop:scoped-proxy/>元素的XML配置开始说起：

```
	<bean id="userPreferences" class="com.foo.UserPreferences" scope="session"/>

	<bean id="userManager" class="com.foo.UserManager">
	    <property name="userPreferences" ref="userPreferences"/>
	</bean>
```

从上述配置中可以很明显的看到singleton bean userManager被注入了一个指向HTTP Session作用域bean userPreferences的引用。singleton userManager bean会被容器仅实例化一次，并且其依赖（userPreferences bean）也仅被注入一次。这意味着，userManager在理论上只会操作同一个userPreferences对象，即原先被注入的那个bean。而注入一个HTTP Session作用域的bean作为依赖，有违我们的初衷。因为我们想要的只是一个userManager对象，在它进入一个HTTP Session生命周期时，我们希望去使用一个HTTP Session的userPreferences对象。

当注入某种类型对象时，该对象实现了和UserPreferences类一样的公共接口（即UserPreferences实例）。并且不论我们底层选择了何种作用域机制（HTTP request、Session等等），容器都会足够智能的获取到真正的UserPreferences对象，因此我们需要将该对象的代理注入到userManager bean中, 而userManager bean并不会意识到它所持有的是一个指向UserPreferences引用的代理。在本例中，当UserManager实例调用了一个使用UserPreferences对象的方法时，实际调用的是代理对象的方法。随后代理对象会从HTTP Session获取真正的UserPreferences对象，并将方法调用委派给获取到的实际的UserPreferences对象。

这就是为什么当你将request、session以及globalSession作用域bean注入到协作对象中时需要如下正确而完整的配置：

```
	<bean id="userPreferences" class="com.foo.UserPreferences" scope="session">
	    <aop:scoped-proxy/>
	</bean>

	<bean id="userManager" class="com.foo.UserManager">
	    <property name="userPreferences" ref="userPreferences"/>
	</bean>
```