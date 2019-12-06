package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import domain.Department;
import service.DepartmentService;
import util.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 将所有方法组织在一个Controller(Servlet)中
 */
@WebServlet("/department.ctl")
public class DepartmentController extends HttpServlet {
    //请使用以下JSON测试增加功能（id为空）
    //{"description":"id为null的新专业","no":"05","remarks":""}
    //请使用以下JSON测试修改功能
    //{"description":"修改id=1的专业","id":1,"no":"05","remarks":""}

    /**
     * POST, http://49.234.218.81:8080/Department.ctl, 增加专业
     * 增加一个专业对象：将来自前端请求的JSON对象，增加到数据库表中
     * @param request 请求对象
     * @param response 响应对象
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        //根据request对象，获得代表参数的JSON字串
        String Department_json = JSONUtil.getJSON(request);

        //将JSON字串解析为Department对象
        Department DepartmentToAdd = JSON.parseObject(Department_json, Department.class);
        //前台没有为id赋值，此处模拟自动生成id。如果Dao能真正完成数据库操作，删除下一行。
        DepartmentToAdd.setId(4 + (int)(Math.random()*100));

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //在数据库表中增加Department对象
        try {
            DepartmentService.getInstance().add(DepartmentToAdd);
            message.put("message", "增加成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * DELETE, http://49.234.218.81:8080/Department.ctl?id=1, 删除id=1的专业
     * 删除一个专业对象：根据来自前端请求的id，删除数据库表中id的对应记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //读取参数id
        String id_str = request.getParameter("id");
        int id = Integer.parseInt(id_str);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();

        //到数据库表中删除对应的专业
        try {
            DepartmentService.getInstance().delete(id);
            message.put("message", "删除成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
        }catch(Exception e){
            message.put("message", "网络异常");
        }
        //响应message到前端
        response.getWriter().println(message);
    }


    /**
     * PUT, http://49.234.218.81:8080/Department.ctl, 修改专业
     *
     * 修改一个专业对象：将来自前端请求的JSON对象，更新数据库表中相同id的记录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置请求字符编码为UTF-8
        request.setCharacterEncoding("UTF-8");
        String Department_json = JSONUtil.getJSON(request);
        //将JSON字串解析为Department对象
        Department DepartmentToAdd = JSON.parseObject(Department_json, Department.class);

        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        //到数据库表修改Department对象对应的记录
        try {
            DepartmentService.getInstance().update(DepartmentToAdd);
            message.put("message", "修改成功");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
        }catch(Exception e){
            message.put("message", "网络异常");
            e.printStackTrace();
        }
        //响应message到前端
        response.getWriter().println(message);
    }

    /**
     * GET, http://49.234.218.81:8080/Department.ctl?id=1, 查询id=1的专业
     * GET, http://49.234.218.81:8080/Department.ctl, 查询所有的专业
     * 把一个或所有专业对象响应到前端
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //设置响应字符编码为UTF-8
        response.setContentType("text/html;charset=UTF-8");
        //读取参数id
        String id_str = request.getParameter("id");
        String paraType = request.getParameter("paraType");

        //创建JSON对象message，以便往前端响应信息
        JSONObject message = new JSONObject();
        try {
            //如果id = null且paraType, 表示响应所有专业对象
            if (id_str == null&&paraType==null) {
                responseDepartments(response);
            }else if(paraType==null) {
                //如果paraType = null,表示根据id响应指定的专业对象
                int id = Integer.parseInt(id_str);
                responseDepartment(id, response);
            }else if(paraType.equals("school")&&id_str!=null){
                //如果paraType=school且id不为空，表示响应id对应的学院的专业对象
                int id = Integer.parseInt(id_str);
                responseDepartmentsOfSchool(id,response);
            }else
                //如果上述信息都不满足，则显示参数设置错误
                response.getWriter().println("参数设置错误");
        }catch (SQLException e){
            message.put("message", "数据库操作异常");
            e.printStackTrace();
            //响应message到前端
            response.getWriter().println(message);
        }catch(Exception e){
            message.put("message", "网络异常");
            e.printStackTrace();
            //响应message到前端
            response.getWriter().println(message);
        }
    }
    //响应一个专业对象
    private void responseDepartment(int id, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //根据id查找专业
        Department Department = DepartmentService.getInstance().find(id);
        String Department_json = JSON.toJSONString(Department);

        //响应Department_json到前端
        response.getWriter().println(Department_json);
    }
    //响应所有专业对象
    private void responseDepartments(HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得所有专业
        Collection<Department> Departments = DepartmentService.getInstance().findAll();
        String Departments_json = JSON.toJSONString(Departments, SerializerFeature.DisableCircularReferenceDetect);

        //响应Departments_json到前端
        response.getWriter().println(Departments_json);
    }
    //相应一个学院的所有专业
    private void responseDepartmentsOfSchool(int id,HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        //获得此学院的所有专业
        Collection<Department> departments = DepartmentService.getInstance().findAllBySchool(id);
        String departments_json = JSON.toJSONString(departments, SerializerFeature.DisableCircularReferenceDetect);

        //响应departments_json到前端
        response.getWriter().println(departments_json);
    }
}
