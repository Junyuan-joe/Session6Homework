<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- <c:url value="/" var="WEB_PATH"/> --%>
<html>
<head>
    <meta http-equiv="Content-Language" content="zh-tw">
<!--     <meta http-equiv="Content-Type" content="text/html; charset=utf-8"> -->
    <title>BankLogin</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> 

<script type="text/javascript">
function getUpdatedGoods() {
    var goodsID = $("#goodsID").val();
    if (goodsID) {
        $.ajax({
            url: "BackendAction.do?action=getupdateGoodsView",
            type: "GET",  //URL長度有限，不能用於傳輸敏感資料/GET請求只是從服務器獲取資源，而不對資源進行修改/可分享性網址之訊
            data: {goodsID: goodsID},
            dataType: "json",
            success: function(goods) {
                // 使用返回的 goods 數據更新相應的表單
                $("input[name='goodsPrice']").val(goods.goodsPrice);
                $("#goodsQuantityDisplay").text("庫存數量：" + goods.goodsQuantity);
                $("input[name='status'][value='" + goods.status + "']").prop("checked", true);
            },
            error: function() {
                alert("獲取商品信息失敗！");
            }
        });
    }
}
function accountSelected() {
    document.updateGoodsForm.action.value = "updateGoodsView";
    document.updateGoodsForm.submit();
}
function deleteGoods(goodsID) {
    $.ajax({
        url: "BackendAction.do?action=deleteGoods",
        type: "Post",
        data: {goodsID: goodsID},
        success: function(result) {
            window.location.href = "BackendAction.do?action=updateGoodsView";
            return true; // 在成功的情況下返回true
        },
        error: function() {
            alert("刪除商品失敗！");
        }
    });
}

function goodsSubmit(event) {
    event.preventDefault();//阻止表單的自動提交
    var goodsID = $("#goodsID").val();
    var status = $("input[name='status']:checked").val(); //  checked status  button

    if (!goodsID) {
        alert("請選擇商品!");
        return false;
    } else {
        if (status === "下架") { 
     
        } else {
            document.updateGoodsForm.action = "BackendAction.do?action=updateGoods";
            document.updateGoodsForm.submit();
        }
    }
}
$(document).ready(function() {
    getUpdatedGoods();
});
//$(document).ready()中调用getUpdatedGoods()，
//确保函数仅在DOM完全加载後执行，避免了可能出现的访问尚未可用的DOM元素的问题。
</script>
</head>
<body>
<%@ include file="Bank_FunMenu.jsp" %>
    <br/><br/><hr>       
    <h2>商品修改</h2><br/>
    <div style="margin-left:25px;">
   
        <p style="color:blue;"><c:out value="${sessionScope.updateMsg}"/></p>
        <% session.removeAttribute("updateMsg");%>
          <p style="color:blue;"><c:out value="${sessionScope.deleteMsg}"/></p>
        <% session.removeAttribute("deleteMsg");%>
       <form name="updateGoodsForm" action="BackendAction.do" method="post" onsubmit="goodsSubmit(event);">
            <input type="hidden" name="action" value="updateGoods"/>
            <p>
                飲料名稱：
         <select size="1" name="goodsID" id="goodsID" onchange="getUpdatedGoods();">
               <option value="">請選擇商品</option>
                
                   <c:forEach items="${goodsList}" var="goods">
   
<!--     頁面載入時，這個選項將預設被選中  updateGoods 物件表示）將被預設選中-->
    <option value="${goods.goodsID}" <c:if test="${goods.goodsID eq updateGoods.goodsID}">selected</c:if>>
                                      ${goods.goodsName}
    </option>
</c:forEach>
                </select>
            </p>    
            <p>
                更改價格：<input type="number" name="goodsPrice" size="5" value="${updateGoods.goodsPrice}" min="0" max="1000">
            </p>
           <p id="goodsQuantityDisplay">
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