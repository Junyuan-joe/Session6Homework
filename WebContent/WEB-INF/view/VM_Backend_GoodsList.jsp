<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
	<meta http-equiv="Content-Language" content="zh-tw">
<!-- 	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"> -->
	<title>販賣機-後臺</title>
	<style type="text/css">
/* 	設置寬度和高度。padding-left: 10px;給元素的左側添加了10像素的內邊距。 */
		.page {
			display:inline-block;
			padding-left: 10px;
		}
		.status-off {
			color: red;
		}
	</style>
	<style>
    .disabled-link {
        pointer-events: none;
        cursor: default;
        text-decoration: none;
        color: gray;
    }
</style>
	<script type="text/javascript">
// 	 JavaScript 函數
    function handleLinkClick(action, goodsID, status) {
        window.location.href = "BackendAction.do?action=" + action + "&goodsID=" + goodsID + "&status=" + status;
    }
</script>
	</script>
	
	
</head>

<body>
<%@ include file="Bank_FunMenu.jsp" %>
	<br/><br/><HR>
<h2>商品列表</h2><br/>
	<div style="margin-left:25px;">
	<table border="1">
		<tbody>
		<tr height="50">
    <td width="150" style="text-align: center;"><b>商品ID</b></td>
    <td width="150" style="text-align: center;"><b>商品名稱</b></td>
    <td width="100" style="text-align: center;"><b>商品價格</b></td>
    <td width="100" style="text-align: center;"><b>現有庫存</b></td>
    <td width="100" style="text-align: center;"><b>商品狀態</b></td>
</tr>
          <c:forEach items="${pageGoodsList}" var="goods">
            <tr height="30">
                <td>${goods.goodsID}</td>
                <td>${goods.goodsName}</td>
                <td>${goods.goodsPrice}</td>
                <td>${goods.goodsQuantity}</td>
             <td style="text-align: center;">
           
            <c:choose>
    <c:when test="${goods.status == '1'}">
<!--     鏈接被點擊時，這個函數就會被調用  防止點擊鏈接時頁面跳轉javascript:void(0)-->
        <a href="javascript:void(0)" onclick="handleLinkClick('updateGoods', '${goods.goodsID}', '0')">上架</a>
    </c:when>
    <c:otherwise>
        <a href="javascript:void(0)" class="status-off" onclick="handleLinkClick('updateGoods', '${goods.goodsID}', '1')">下架</a>
    </c:otherwise>
</c:choose>
        </td>
    </tr>                    
</c:forEach>
    </tbody>
</table>
	<tr>
    <td colspan="2" align="right">
        <h3 class="page">
            <c:if test="${pageIndex > 1}">
                <a href="BackendAction.do?action=queryGoodsPage&amp;pageIndex=${pageIndex - 1}">上一頁</a>
            </c:if>
        </h3>
        <c:forEach items="${pageButtonList}" var="pageNum">
            <h3 class="page">
                <c:choose>
                    <c:when test="${pageNum == pageIndex}">
                        <span style="color: red;">${pageNum}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="BackendAction.do?action=queryGoodsPage&amp;pageIndex=${pageNum}">${pageNum}</a>
                    </c:otherwise>
                </c:choose>
            </h3>
        </c:forEach>
        <h3 class="page">
            <c:if test="${pageIndex < totalPage}">
                <a href="BackendAction.do?action=queryGoodsPage&amp;pageIndex=${pageIndex + 1}">下一頁</a>
            </c:if>
        </h3>
    </td>
</tr>
	</div>
</body>
</html>