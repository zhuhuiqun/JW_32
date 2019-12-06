package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "Filter 0", urlPatterns = {"/*"})/*可以对所有资源进行过滤*/

public class Filter0_Encoding implements Filter {

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Filter 0 - encoding begins");
        //将servletRequest强制类型转换为HttpServletRequest
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        //将servletResponse强制类型转换为HttpServletResponse
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        String method=request.getMethod();
            //设置响应字符编码为UTF-8
            response.setContentType("text/html;charset=UTF-8");
            System.out.println("设置响应对象字符编码格式为utf8");
            if (method.equals("POST")||method.equals("PUT")){
                //设置请求字符编码为UTF-8
                request.setCharacterEncoding("UTF-8");
                System.out.println("设置请求对象字符编码格式为utf8");
            }
        //放行，执行其他过滤器，如过滤器已经执行完毕，则执行原请求
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("Filter 0 - encoding ends"+"\n");
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
}
