package dao;

import domain.Teacher;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.TreeSet;


public final class TeacherDao {
	private static TeacherDao teacherDao=
			new TeacherDao();
	private TeacherDao(){}
	public static TeacherDao getInstance(){
		return teacherDao;
	}
	//返回结果集对象
	public Collection<Teacher> findAll(){
		Collection<Teacher> teachers = new TreeSet<Teacher>();
		try{
			//获得数据库连接对象
			Connection connection = JdbcHelper.getConn();
			//在该连接上创建语句盒子对象
			Statement stmt = connection.createStatement();
			//执行SQL查询语句并获得结果集对象
			ResultSet resultSet = stmt.executeQuery("SELECT * FROM Teacher");
			//若结果存在下一条，执行循环体
			while (resultSet.next()) {
				//打印结果集中记录的id字段
				System.out.print(resultSet.getInt("id"));
				System.out.print(",");
				//打印结果集中记录的no字段
				System.out.print(resultSet.getInt("no"));
				System.out.print(",");
				//打印结果集中记录的name字段
				System.out.print(resultSet.getString("name"));
				System.out.print(",");
				//打印结果集中记录的profTitle字段
				System.out.print(resultSet.getString("title_id"));
				System.out.print(",");
				//打印结果集中记录的degree字段
				System.out.print(resultSet.getString("degree_id"));
				System.out.print(",");
				//打印结果集中记录的department字段
				System.out.print(resultSet.getString("department_id"));
				//根据数据库中的数据,创建Teacher类型的对象
				Teacher teacher = new Teacher(resultSet.getInt("id"), resultSet.getString("no"),resultSet.getString("name"), ProfTitleDao.getInstance().find(resultSet.getInt("title_id")), DegreeDao.getInstance().find(resultSet.getInt("degree_id")), DepartmentDao.getInstance().find(resultSet.getInt("department_id")));
				//添加到集合teachers中
				teachers.add(teacher);
			}
			connection.close();
		}catch (SQLException e){
			e.printStackTrace();
		}
		return teachers;
	}
	public Teacher find(Integer id) throws SQLException{
		//声明一个Teacher类型的变量
		Teacher teacher = null;
		//获得数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//写sql语句
		String deleteTeacher_sql = "SELECT * FROM teacher WHERE id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(deleteTeacher_sql);
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		//执行预编译语句
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()){
			teacher = new Teacher(resultSet.getInt("id"),resultSet.getString("no"), resultSet.getString("name"), ProfTitleDao.getInstance().find(resultSet.getInt("title_id")), DegreeDao.getInstance().find(resultSet.getInt("degree_id")), DepartmentDao.getInstance().find(resultSet.getInt("department_id")));
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return teacher;
	}
	public boolean add(Teacher teacher) throws SQLException,ClassNotFoundException{
		PreparedStatement preparedStatement = null;
		Connection connection = null;
		boolean isCommit = false;
		int enterInfoId = 0;
		try {
			//获得数据库连接对象
			connection = JdbcHelper.getConn();
			//关闭自动提交
			connection.setAutoCommit(false);
			String addTeacher_sql = "INSERT INTO teacher (no,name,title_id,degree_id,department_id) VALUES" + " (?,?,?,?,?)";
			//在该连接上创建预编译语句对象，该对象可获得自动生成的键
			preparedStatement = connection.prepareStatement(addTeacher_sql,Statement.RETURN_GENERATED_KEYS);
			//为预编译参数赋值
			preparedStatement.setString(1, teacher.getNo());
			preparedStatement.setString(2, teacher.getName());

			preparedStatement.setInt(3, teacher.getTitle().getId());
			preparedStatement.setInt(4, teacher.getDegree().getId());
			preparedStatement.setInt(5, teacher.getDepartment().getId());
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if(rs.next()) {
			    //获得第一个键值,这里只有id为自增,所以获取第一个的值
				enterInfoId = rs.getInt(1);
			}

			String addUser_sql = "INSERT INTO USER (username,password,teacher_id) VALUES" + "(?,?,?)";
			preparedStatement = connection.prepareStatement(addUser_sql);
			preparedStatement.setString(1, teacher.getNo());
			preparedStatement.setString(2, teacher.getNo());
			//preparedStatement.setTimestamp(3, new Timestamp((new java.util.Date()).getTime()));
			preparedStatement.setInt(3, enterInfoId);
			preparedStatement.executeUpdate();
			//提交当前连接所做的操作
			connection.commit();
			//将判断提交变量设为true
			isCommit = true;
		}catch (SQLException e){
			System.out.println(e.getMessage()+"\n errorCode="+e.getErrorCode());
			try{
				//回滚当前连接所做的操作
				if (connection != null){
					connection.rollback();
				}
			}catch (SQLException e1){
				e1.printStackTrace();
			}
		}finally {
			try{
				//恢复自动提交
				if (connection != null){
					connection.setAutoCommit(true);
				}
			}catch (SQLException e){
				e.printStackTrace();
			}
			//关闭资源
			JdbcHelper.close(preparedStatement, connection);
		}
		return isCommit;
	}
	//delete方法，根据teacher的id值，删除数据库中对应的teacher对象
	public boolean delete(int id) throws ClassNotFoundException,SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean isCommit = false;
		try {
			//获得数据库连接对象
			connection = JdbcHelper.getConn();
			//关闭自动提交
			connection.setAutoCommit(false);

			//因为user表中含有外键，所以想删除teacher的一行元祖，需先删除user表中含有teacher的id的元祖
			String deleteUser_sql = "DELETE FROM User WHERE teacher_id=?";
			//在该连接上创建预编译语句对象
			preparedStatement = connection.prepareStatement(deleteUser_sql);
			//为预编译参数赋值
			//因为user中teacher_id属性为外键，teacher_id的值与teacher表中的id值关联，所以该id即为user中teacher_id
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();

			String deleteTeacher_sql = "DELETE FROM teacher WHERE id=?";
			//在该连接上创建预编译语句对象
			preparedStatement = connection.prepareStatement(deleteTeacher_sql);
			//为预编译参数赋值
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();

			connection.commit();
			isCommit = true;
		}catch (SQLException e){
			System.out.println(e.getMessage()+"\n errorCode="+e.getErrorCode());
			try{
				//回滚当前连接所做的操作
				if (connection != null){
					connection.rollback();
				}
			}catch (SQLException e1){
				e1.printStackTrace();
			}
		}finally {
			try{
				//恢复自动提交
				if (connection != null){
					connection.setAutoCommit(true);
				}
			}catch (SQLException e){
				e.printStackTrace();
			}
			//关闭资源
			JdbcHelper.close(preparedStatement, connection);
		}
		return isCommit;
	}
	public boolean update(Teacher teacher) throws ClassNotFoundException,SQLException{
		//获得数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//写sql语句
		String updateDegree_sql = " update teacher set no=?,name=?,title_id=?,degree_id=?,department_id=? where id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(updateDegree_sql);
		//为预编译参数赋值
		preparedStatement.setString(1, teacher.getNo());
		preparedStatement.setString(2,teacher.getName());
		preparedStatement.setInt(3,teacher.getTitle().getId());
		preparedStatement.setInt(4,teacher.getDegree().getId());
		preparedStatement.setInt(5,teacher.getDepartment().getId());
		preparedStatement.setInt(6,teacher.getId());
		//执行预编译语句，获取改变记录行数并赋值给affectedRowNum
		int affectedRows = preparedStatement.executeUpdate();
		System.out.println("修改了"+affectedRows+"行记录");
		//关闭资源
		JdbcHelper.close(preparedStatement,connection);
		return affectedRows>0;
	}
}