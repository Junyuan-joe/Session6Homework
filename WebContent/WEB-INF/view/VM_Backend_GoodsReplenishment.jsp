<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<c:url value="/my/js" var="jPATH"/>

<html>
<head>
    <meta http-equiv="Content-Language" content="zh-tw">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>BankLogin</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <script src="${jPATH}/jquery-1.11.1.min.js"></script>
    <script src="${jPATH}/jquery.selectboxes.js"></script>
<script type="text/javascript">

function accountSelected() {
    document.updateGoodsForm.action.value = "updateGoodsView";
    document.updateGoodsForm.submit();
}

function deleteGoods(goodsID) {
    if (confirm("確認要刪除這個商品嗎？")) {
        $.ajax({
            url: "BackendAction.do",
            type: "POST",
            data: {action: "deleteGoods", goodsID: goodsID},
            success: function(result) {
                alert("商品已刪除！");
                window.location.href = "BackendAction.do?action=updateGoodsView";
            },
            error: function() {
                alert("刪除商品失敗！");
                return false;
            }
        });
    }
}

function goodsSubmit(event) {
    event.preventDefault();
    var goodsID = document.getElementById("goodsID").value;
    if (!goodsID) {
        alert("請選擇商品!");
        return false;
    } else {
        if (document.getElementsByName("status")[1].checked) { // check if status is "下架"
            deleteGoods(goodsID); // call deleteGoods function
            return false;
        } else { // status is not "下架"
            document.updateGoodsForm.action.value = "updateGoods";
            document.updateGoodsForm.submit();
        }
    }
}
</script>
</head>
<body>
<%@ include file="Bank_FunMenu.jsp" %>
    <br/><br/><hr>       
    <h2>帳戶修改</h2><br/>
    <div style="margin-left:25px;">
        <p style="color:blue;"><c:out value="${sessionScope.updateMsg}"/></p>
        <% session.removeAttribute("updateMsg");%>
       <form name="updateGoodsForm" action="BackendAction.do" method="post" onsubmit="goodsSubmit(event);">
            <input type="hidden" name="action" value="updateGoods"/>
            <p>
                飲料名稱：
                <select size="1" name="goodsID" id="goodsID" onchange="accountSelected();">
                    <option value="">請選擇商品</option>
                    <c:forEach items="${goodsList}" var="goods">
                        <option value="${goods.goodsID}" <c:if test="${updateGoods.goodsID eq goods.goodsID}">selected</c:if>>
                            ${goods.goodsName}
                        </option>
                    </c:forEach>
                </select>
            </p>    
            <p>
                更改價格：<input type="number" name="goodsPrice" size="5" value="${updateGoods.goodsPrice}" min="0" max="1000">
            </p>
            <p>
                庫存數量：<c:out value="${updateGoods.goodsQuantity}"/>
            </p>
            <p>        
                補貨數量：<input type="number" name="goodsQuantity" size="5" value="0" min="0">
            </p>
            <p>
                商品狀態：
                <label><input type="radio" name="status" value="1" <c:if test="${updateGoods.status eq '1'}">checked</c:if>>上架</label>
                <label><input type="radio" name="status" value="0" <c:if test="${updateGoods.status eq '0'}">checked</c:if>>下架</label>
            </p>
            <p>
                <input type="submit" value="送出">
            </p>
            
        </form>
</div>
</body>
</html>