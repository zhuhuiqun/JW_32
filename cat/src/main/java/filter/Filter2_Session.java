package filter;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "Filter 2", urlPatterns = {"/*"})/*可以对所有资源进行过滤*/

public class Filter2_Session implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Filter 2 - session begins");
        //将servletRequest强制类型转换为HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //将servletResponse强制类型转换为HttpServletResponse
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI();
        if (path.contains("/login")) {/*不能为login目录下的资源进行过滤*/
            //放行，执行其他过滤器，如过滤器已经执行完毕，则执行原请求
            filterChain.doFilter(servletRequest, servletResponse);
            System.out.println("执行其它过滤器或原请求" + "\n");
        } else {
            //创建JSON对象message，以便往前端响应信息
            JSONObject message = new JSONObject();
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("currentUser") == null) {
                message.put("message", "请登录或重新登陆");
                //响应message到前端
                response.getWriter().println(message);
            } else {
                //放行，执行其他过滤器，如过滤器已经执行完毕，则执行原请求
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
        System.out.println("Filter 2 - session ends" + "\n");
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
}
