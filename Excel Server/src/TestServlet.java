import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpServlet;

@WebListener
public class TestServlet implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        System.out.println("contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        System.out.println("contextDestroyed");
    }
}