<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>販賣機-後臺</title>
    <script type="text/javascript">
    </script>
</head>
<body>
    <%@ include file="Bank_FunMenu.jsp" %> 
    <br><br><hr>

    <h2>銷售報表</h2><br>
    <div style="margin-left:25px;">
        <form action="BackendAction.do" method="GET">
            <input type="hidden" name="action" value="querySalesReport">
            起&nbsp;<input type="date" name="queryStartDate" style="height:25px;width:180px;font-size:16px;text-align:center;">
            &nbsp;迄&nbsp;<input type="date" name="queryEndDate" style="height:25px;width:180px;font-size:16px;text-align:center;">  
            <input type="submit" value="查詢" style="margin-left:25px;width:50px;height:32px">
        </form>
        <br>
        <table border="1">
            <tbody>
                <tr height="50">
                    <td width="100"><b>訂單編號</b></td>
                    <td width="100"><b>顧客姓名</b></td>
                    <td width="100"><b>購買日期</b></td>
                    <td width="125"><b>飲料名稱</b></td> 
                    <td width="100"><b>購買單價</b></td>
                    <td width="100"><b>購買數量</b></td>
                    <td width="100"><b>購買金額</b></td>
                </tr>
                   <c:if test="${empty salesReports}">
                    <tr>
                        <td colspan="7" align="center"><b><span style="color: red;">無資料查詢</span></b></td>
                    </tr>
                </c:if>
                <c:forEach items="${salesReports}" var="report">
                    <tr height="50">
                        <td width="100">${report.orderID}</td>
                        <td width="100">${report.customerName}</td>
                        <td width="100">
                            <fmt:formatDate value="${report.orderDate}" pattern="yyyy/MM/dd"/>
                        </td>
                        <td width="125">${report.goodsName}</td> 
                        <td width="100">${report.goodsBuyPrice}</td>
                        <td width="100">${report.buyQuantity}</td>
                        <td width="100">${report.buyAmount}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>