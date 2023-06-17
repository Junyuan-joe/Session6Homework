<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script>
document.addEventListener('DOMContentLoaded', function() {

    var table = document.getElementById('cart-table');
    cartData.shoppingCartGoods.forEach(function(item) {
        var row = table.insertRow(-1);
        var cell1 = row.insertCell(0);
        var cell2 = row.insertCell(1);
        var cell3 = row.insertCell(2);
        var cell4 = row.insertCell(3);
        
        cell1.innerHTML = '<img src="' + item.goodsImageUrl + '" alt="' + item.goodsName + '" style="width: 100px; height: auto;">';
        cell2.innerHTML = item.goodsName;
        cell3.innerHTML = item.goodsPrice;
        cell4.innerHTML = item.buyQuantity;
    });

    document.getElementById('cart-info').innerHTML = '<p>總金額：' + cartData.totalAmount + '</p>';
});
</script>
<title>購物車</title>
</head>
<body align="center">
    <h3>購物車內容</h3>
  <table id="cartTable" border="1">
    <thead>
        <tr>
            <th>商品圖片</th>
            <th>商品名稱</th>
            <th>商品價格</th>
            <th>商品數量</th>
        </tr>
    </thead>
    <tbody id="cartTableBody">
    </tbody>
</table>
<div id="cartDetails" style="display: none;">
    <!-- 購物車總金額將插入到這裡 -->
</div>
    <div id="cart-info"></div>
    <button onclick="redirectToCheckout()">結帳</button>
</body>
</html>