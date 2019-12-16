<%--
  Created by IntelliJ IDEA.
  User: 56591
  Date: 2019/12/11
  Time: 22:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>优乐选一</title>
</head>
<body>

欢迎来到优乐选一
<%=request.getRemoteUser()%>

<br>
<a href="http://localhost:8080/cas/logout?service=http://www.baidu.com">退出登录</a>

</body>
</html>
