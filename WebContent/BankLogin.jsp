<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>BankLogin</title>
	<style>
		body {
			background-image: url(images/banner.jpg);
			background-size: 100% 100%;
			background-attachment: fixed;
		}
		.form-container {
			text-align: center;
			margin-top: 100px;
		}
		.form-container label {
			display: inline-block;
			margin-bottom: 10px;
		}
		.form-container input[type="text"],
		.form-container input[type="password"] {
			display: block;
			width: 300px; /* 設定輸入框寬度為300px */
			padding: 10px;
			margin: 0 auto 20px auto; /* 設定輸入框上下外距為20px，左右內距為auto */
			border-radius: 5px;
			border: none;
			box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
		}
		.form-container input[type="submit"] {
			background-color: #00bfff;
			color: #fff;
			border: none;
			padding: 10px 20px;
			border-radius: 5px;
			cursor: pointer;
			font-size: 16px;
			box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);
			transition: all 0.3s ease-in-out;
		}
		.form-container input[type="submit"]:hover {
			background-color: #00a1ff;
		}
	</style>
</head>
<body>
	<c:if test="${not empty requestScope.loginMsg}">
		系統回應：<p style="color:blue;">${requestScope.loginMsg}</p>
	</c:if>
	<div class="form-container">
		<form action="LoginAction.do" method="post">
		<input type="hidden" name="action" value="login"/>
			<label for="identificationNo">IDENTIFICATION_NO:</label>
			<input type="text" name="identificationNo" id="identificationNo"/>
			<label for="password">PASSWORD:</label>
			<input type="password" name="password" id="password"/>
			<input type="submit" value="登入"/>
		</form>
	</div>
</body>
</html>