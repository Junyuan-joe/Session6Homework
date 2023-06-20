<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- JSTL的核心標籤庫，可以通過前綴 "c" 來使用這些標籤。這些核心標籤提供了許多基本功能，例如變量支持、流程控制（例如if-else條件、for迴圈） -->
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- 格式化標籤庫提供了國際化和本地化的，例如日期和數字的格式化 -->
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<!-- <meta http-equiv="Content-Type" content="text/html; charset=utf-8"> -->
<title>販賣機-後臺</title>
	<script type="text/javascript">

	</script>
<%@ include file="Bank_FunMenu.jsp" %>
	<br/><br/><HR>
		
	<h2>商品新增上架</h2><br/>
	<div style="margin-left:25px;">
	<p style="color:blue;">${sessionScope.createMsg}</p>
	<% session.removeAttribute("createMsg"); %>
<!-- 二進位數據形式的文件（如圖片、音頻文件、PDF)，。這種編碼類型允許你在一次請求中發送不同類型的數據（ASCII和二進位數據） -->
	<form action="BackendAction.do?action=addGoods" enctype="multipart/form-data" method="post">
		<p>
			飲料名稱：
			<input type="text" name="goodsName" size="10"/>
		</p>
		<p>
			設定價格： 
			<input type="number" name="goodsPrice" size="5" value="0" min="0" max="1000"/>
		</p>
		<p>
			初始數量：
			<input type="number" name="goodsQuantity" size="5" value="0" min="0" max="1000"/>
		</p>
		<p>
			商品圖片：
			<input type="file" name="goodsImage"/>
		</p>
		<p>
			商品狀態：
			<select name="status">
				<option value="1">上架</option>
<!-- 				<option value="0">下架</option>				 -->
			</select>
		</p>
		<p>
			<input type="submit" value="送出">
		</p>
	</form>
	</div>
</body>
</html>