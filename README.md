# zqMVC是什么？
>zqMVC是一个简易的Java MVC框架，当然，它简易的到了你可以把它看作是一个工具类。

<br/><br/>
# zqMVC可以干什么？
>zqMVC可以摆脱开发传统JavaWeb应用编写大量Servlet的束缚。在传统JavaWeb应
用开发中，一个Servlet只能处理一个URL的对应的请求，zqMVC不一样，它可以使用一个
“Router”处理许多个不同的URL请求，而且这些处理的过程和逻辑，完全有开发者自己定
义。

<br/><br/>
# 如何使用zqMVC？
>1、首先在你的项目你需要导入zqMVC  

>2、在web.xml中配置请求分发器DispacherServlet及其映射，拦截所有请求
```
<servlet>
    <servlet-name>zqMvc</servlet-name>
    <servlet-class>zqmvc.servlets.DispacherServlet</servlet-class>
    <init-param>
      <param-name>packageName</param-name>
      <param-value>zq.controller</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>zqMvc</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
```
>3、处理静态资源  
由于zqMVC目前还没有独自处理静态资源能力，因此可以借助Tomcat内置的defaultServlet处理静态资源，可以直接配置如下：
```
<servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.css</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.js</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.jpg</url-pattern>
  </servlet-mapping>
```
你可如同上述配置一样，自定义你需要放行的静态资源。例如“*.jpg”表示任意以“.jpg”为结尾的URL请求。

**需要特别注意，defaultServlet的映射拦截配置必须配置在zqMVC的映射拦截之前，这样静态资源才能真正被defaultServlet拦截并处理**
```
//完整配置如下：
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
  <display-name>zqMVC</display-name>

  <servlet>
    <servlet-name>zqMvc</servlet-name>
    <servlet-class>zqmvc.servlets.DispacherServlet</servlet-class>
    <init-param>
      <param-name>packageName</param-name>
      <param-value>zq.controller</param-value>
    </init-param>
  </servlet>

<!--  静态资源使用Tomcat默认的defaultServlet处理-->
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.css</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.js</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.jpg</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.JPG</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.png</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>*.PNG</url-pattern>
  </servlet-mapping>

  <servlet-mapping>
    <servlet-name>zqMvc</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>

</web-app>
```
>4、定义Router
```
//示例：
@Router
@URLMapping(value = "/test")
public class TestController {

    @URLMapping("one")
    public String test(){
        return "index.jsp";
    }

    @URLMapping("two")
    public User test2(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getParameter("zqMVC"));
        return new User("zqMVC",21);
    }
}
```
被@Router注解的类将被zqMVC扫描到，zqMVC扫描到该类后便会处理该类的配置的所有映射，当访问对应的URL时便会调用相应的方法进行处理。  
例如：当访问 http://localhost:8080/zqMVC/test/one 这个URL时，zqMVC便会执行TestController类中的test方法，你可以在该方法中处理你的业务请求。  
**特别说明：@URLMapping注解使用在类上时，必须使用value接收一个String类型的值，且该String必须以“/”开头。@URLMapping注解使用在方法上时，可以不用value赋值一个String类型的值，直接接收一个String类型的参数，且该String参数不能以“/”开头或者结尾**
>5、处理请求的参数  
从上述的Router定义中可以看到方法test2方法中有两个参数：request和response,这两个参数的类型分别为HttpServletRequest和HttpServletResponse，通过这两个参数，你可以对请求和相应进行相应的配置、获取请求参数、返回数据。

>6、响应视图和其他类型数据  
zqMVC目前仅支持JSP视图，当Router中的方法返回类型是String类型时，zqMVC会把该值当作一个视图路径，该路径不能以“/”开头，且不能时是相对路径，zqMVC会自动给  返回的String加上“/”，该绝对路径的根目录是webapp目录（你的web项目的部署目录）。当Router中的方法返回类型不是String类型时，zqMVC会把返回值进行JSON字符串化并响应该字符串。

<br/><br/>
# 更新日志
## 2020-08-12
>支持URL的重定向  
>>当Router方法的返回值是String且以“redirect:”开头时，zqMVC会将后面的字符串作为重定向到的URL，“redirect:”后面的字符串以“/”开头时，表示绝对路径，使用相对路径时不能以形如“./”或者“../”开头，只能是以非“/”开头的字符串。
```
	//示例
	@Router
	@URLMapping(value = "/test")
	public class TestController {

		@URLMapping("one")
		public String test(){
			return "index.jsp";
		}

		@URLMapping("two")
		public User test2(HttpServletRequest request, HttpServletResponse response){
			System.out.println(request.getParameter("zqMVC"));
			return new User("zqMVC",21);
		}

		@URLMapping("three")
		public String test3(){
			return "redirect:/test/two";
			//return "redirect:two";
		}
	}
```
当访问URL http://localhost:8080/zqMVC/test/three 后，以上两个return都将重定向到 http://localhost:8080/zqMVC/test/two 。