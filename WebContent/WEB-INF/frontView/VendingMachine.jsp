<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!-- 控制流程和数据处理标签  c-->
<!-- 格式化标签库 fmt-->
<!-- 第三方標籤函式庫 -->

<html>
<head>
<meta http-equiv="Content-Language" content="zh-tw">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>販賣機</title>
<style type="text/css">
.product-image {
    display: flex;
    justify-content: center;
}
.add-to-cart {
    display: inline-block;
    margin: 0 auto;
    white-space: nowrap; /* 添加此样式以防止换行 */
}

.inventory {
    color: red;
}

.page {
    display: inline-block;
    padding-left: 10px;
}

.page a:link, .page a:visited {
    color: blue;
    text-decoration: none;
}

.page a:hover {
    color: red;
}
</style>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.3/dist/umd/popper.min.js"></script>

</head>
<body align="center">
	<!-- 修改後的購物車模態框 -->
	<div class="modal fade" id="cartModal" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalLabel" aria-hidden="true"
		style="display: none;">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">購物車商品</h5>
<!-- 					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button> -->
				
				</div>
				<div class="modal-body" id="cart-content">
					<!-- 購物車內容將顯示在這裡 -->
				</div>
				<div class="modal-footer">
					<input type="number" name="inputMoney" min="0" step="1"
						placeholder="請输入投入金额" required>
<!-- 						关闭当前的模态窗    Bootstrap的一个特殊属性 -->
					<button type="button" class="btn btn-secondary"
						data-bs-dismiss="modal">繼續購物</button>
						
<!-- 					btn btn-primary	藍      Bootstrap套用按鈕(顏色) btn btn-secondary  灰                  -->
					<button type="button" class="btn btn-primary" onclick="checkout()"
						data-bs-dismiss="modal">結帳</button>
				</div>
			</div>
		</div>
	</div>

	</div>
	<script type="text/javascript">
// 	$(document).on("click", ".increase-quantity", function(event) {
// 		  var $this = $(this);
// 	    var goodsID = $this.data("goods-id");
// 	    var stockQuantity = parseInt($this.data("goods-quantity"));
// 	    var currentQuantity = parseInt($this.closest("tr").find(".buy-quantity").val());

// 	    // 检查库存数量
// 	    if (stockQuantity <= 0) {
// 	        alert("此商品库存不足，无法增加购物车数量。");
// 	        return;
// 	    }

// 	    // 检查购物车是否已有该商品达到库存量
// 	    var cartItemQuantity = parseInt(localStorage.getItem('cartItemQuantity' + goodsID)) || 0;
// 	    if (cartItemQuantity + 1 > stockQuantity) {
// 	        alert("购物车已有该商品达到库存量，无法再加入该商品。");
// 	        return;
// 	    }

// 	    // 检查增加的数量是否超过库存数量
// 	    if (currentQuantity + 1 > stockQuantity) {
// 	        alert("增加的数量超过库存数量，无法增加购物车数量。");
// 	        return;
// 	    }
	
// 	    // 发送 Ajax 请求到后端
// 	    $.ajax({
// 	        type: "POST",
// 	        url: "MemberAction.do?action=increaseQuantity",
// 	        data: {
// 	            goodsID: goodsID,
	            
// 	        },
// 	        dataType: "json",
// 	        success: function(response) {
// 	            // 檢查後端返回的 JSON 是否包含 "error" 鍵
// 	            if (response.error) {
// 	              alert(response.error);
// 	              return;
// 	            }

// 	            // 檢查後端返回的 JSON 是否包含 "message" 鍵
// 	            if (response.message) {
// 	              alert(response.message);
// 	              return;
// 	            }

// 	            // 更新購物車內容
// 	            $("#queryCart").click();

// 	            // 更新 localStorage 中的購物車數量
// 	            var currentCartCount = parseInt(localStorage.getItem('cartCount')) || 0;
// 	            currentCartCount += 1;
// 	            localStorage.setItem('cartCount', currentCartCount);

// 	            // 更新購物車數量顯示
// 	            $("#queryCart").attr("data-cart-count", currentCartCount);
// 	            $("#queryCart").text("購物車(" + currentCartCount + ")");

// // 	            // 更新該商品在購物車中的數量
// // 	            var updatedCartItemQuantity = cartItemQuantity + 1;
// // 	            sessionStorage.setItem('cartItemQuantity' + goodsID, updatedCartItemQuantity);

// 	            // 更新商品數量的屬性值
// 	            $this.closest("tr").find(".buy-quantity").val(currentQuantity + 1);

// 	            // 更新庫存數量
// 	            stockQuantity -= 1;
// 	            $this.data("goods-quantity", stockQuantity);

// 	            // 如果庫存數量已經為0，禁止增加商品至購物車
// 	            if (stockQuantity <= currentCartCount) {
// 	              $this.prop("disabled", true);
// 	            }
// 	        },
// 	        error: function(xhr, status, error) {
// 	            // 处理请求错误
// 	            console.log("Error: " + error);
// 	        }
// 	    });
// 	});
	$(document).on("click", ".decrease-quantity", function(event) {//点击按钮、提交表单
	    var goodsID = $(this).data("goods-id");
	    var buyQuantity = $(this).closest("tr").find(".buy-quantity").val(); //讀取購買數量的
 //触发事件的元素this  /元素向上遍历 DOM ，并返回与选择器 "tr" 匹配/查找与选择器 .buy-quantity 匹配的元素/取第一个匹配元素的当前值

	    // 詢問使用者確認移除点击 
	  
	    if (confirm("確認移除商品？")) {
	        // 發送 Ajax 請求到後端
	        $.ajax({
	            type: "POST",
	            url: "MemberAction.do?action=decreaseQuantity",
	            data: {
	                goodsID: goodsID
	            },
	            dataType: "json",
	            success: function(response) {
	                // 更新購物車內容
	                if (buyQuantity == 0) {
	                    removeCartItem(goodsID); // 調用移除商品的函數
	                } else {
	                    $("#queryCart").click();
	                }
	                // 更新購物車數量顯示
	                var currentCartCount = parseInt($("#queryCart").attr("data-cart-count"));//attr函数取值/加入商品數/.attr() 方法用于获取或设置匹配元素的属性值
	                currentCartCount -= 1;
	               
	                $("#queryCart").attr("data-cart-count", currentCartCount);
	                $("#queryCart").text("購物車(" + currentCartCount + ")");
	             

	            },
	            error: function(xhr, status, error) {
	                // 處理請求錯誤
	            }
	        });
	    }
	});	
	$(document).ready(function() {
	    if (localStorage.getItem('cartCount') === null) {
	    	 localStorage.setItem('cartCount', '0');
	    }
    $(document).on("click", ".add-to-cart", function(event) {
	        event.preventDefault();//阻止表单的默认提交

	        var goodsID = $(this).data("goods-id");
// 	        var stockQuantity = parseInt($(this).data("goods-quantity"));

	        var buyQuantity = parseInt($("input[name='buyQuantity" + goodsID + "']").val());
	        if (isNaN(buyQuantity) || buyQuantity < 1) {
	            buyQuantity = 1;
	        }
	        var currentCartCount = parseInt($("#queryCart").attr("data-cart-count"));//加入商品數	
	        if (isNaN(currentCartCount)) {
	            currentCartCount = 0;
	        }
	        // 發送 Ajax 請求到後端
	        $.ajax({
	            type: "POST",
	            url: "MemberAction.do?action=addCartGoods",
	            data: {
	                goodsID: goodsID,
	                buyQuantity: buyQuantity
	            },
	            dataType: "json",
	            success: function(response) {
	                // 檢查後端返回的 JSON 是否包含 "error" 鍵
	                if (response.error) {
	                    alert(response.error);
	                    return;
	                }

	                // 檢查後端返回的 JSON 是否包含 "message" 鍵
	                if (response.message) {
	                    alert(response.message);
	                    return;
	                }

	                // 更新購物車內容
	                var currentCartCount = parseInt($("#queryCart").attr("data-cart-count"));
	                if (isNaN(currentCartCount)) {
	                    currentCartCount = 0;
	                }
	                
	                currentCartCount += buyQuantity; // 將購買數量添加到購物車商品數量中
	                // 更新sessionStorage中的購物車數量
	                localStorage.setItem('cartCount', currentCartCount);
	                $("#queryCart").attr("data-cart-count", currentCartCount);
	                $("#queryCart").text("購物車(" + currentCartCount + ")");

	                // 將購物車數量保存到 localStorage 中
	                localStorage.setItem('cartCount', currentCartCount);
	            },
	            error: function(xhr, status, error) {
	                // 處理請求錯誤
	                console.log("Error: " + error);
	            }
	        });
	    });
	    window.openCartPage = function() {
	        $('#cartModal').modal('show');
	    }
	    $("#queryCart").click(function() {
	        // localStorage讀取購物車数量
	        var cartCount = parseInt(localStorage.getItem('cartCount'));
	        //localStorage 是 Web Storage API /可以存储更大量的数据（通常最多为 5MB）
	        //并且它的内容不会每次请求时都发送给服务器/永久性
	        // 檢查 cartCount 是否是一个数字
	        if (isNaN(cartCount)) {
	            cartCount = 0;
	        }
	        // 發送Ajax请求到后端
	        $.ajax({
	            type: "POST",
	            url: "MemberAction.do?action=queryCartGoods",
	            dataType: "json",
	            success: function(response) {
	                console.log(response);
	                // 清空購物車内容區域
	                $("#cart-content").empty();
	                if (response.shoppingCartGoods && response.shoppingCartGoods.length > 0) {
	                    $.each(response.shoppingCartGoods, function(index, item) {
	                        $("#cart-content").append(
	                            '<img src="DrinksImage/' + item.goodsImageName + '" alt="' + item.goodsName + '" width="100" height="100">' +
	                            '<p>商品名稱: ' + item.goodsName + '</p>' +
	                            '<p>商品價格: ' + item.goodsPrice + '</p>' +
	                            '<p>購買數量: ' + item.buyQuantity + '</p>' +
//  	                            '<button class="increase-quantity" data-goods-id="' + item.goodsID + '">+</button>' +
	                            '<button class="decrease-quantity" data-goods-id="' + item.goodsID + '">-</button>' +
	                            '<hr>'
	                        );
	                    });
	                    // 顯示金额/ jQuery 的 append() 方法向 ID 为 "cart-content" 的元素的内容末尾添加一段 HTML
	                    $("#cart-content").append('<p>總金額：' + response.totalAmount + ' 元</p>');
	                } else {
	                    $("#cart-content").append('<p>購物車內無商品</p>');
	                }
	                // 顯示購物車模態框
	                openCartPage();
	                // 更新localStorage中的購物車数量
	                localStorage.setItem('cartCount', cartCount);
	            },
	            error: function(xhr, status, error) {
	            	//xhr：XMLHttpRequest对象，它包含有關请求的信息。
                    //status：错误的HTTP狀態码。
                     //error：错误的描述信息。
	                console.log("Error: " + error);
	            },
	        });
	    });
	

	    $("#clearCart").click(function() {
	        $.ajax({
	            type: "POST",
	            url: "MemberAction.do?action=clearCartGoods",
	            dataType: "json",
	            success: function(response) {
	                $("#cartDetails").hide();
	                $("#queryCart").attr("data-cart-count", 0);//加入數
	                $("#queryCart").text("購物車(0)");

	                window.location.href = "FrontendAction.do?action=searchGoods";

// 	                $("#cart-info").empty();		
	            },
	            error: function(xhr, status, error) {
	                console.log("Error: " + error);
	            }
	        });
	    });
    });
	function checkout() {
		// 讀取输入字段中的值
		var inputMoney = $("input[name='inputMoney']").val();
		
		var totalAmount = parseInt($("#cart-content").find("p:last").text().split("：")[1]);//總金額
                // jQuery 方法，它在前面选择的元素中查找最后一个 <p> 元素/取前面找到的 <p> 元素/第二个元素（索引为 1）
		// 判断投入金額是否不足
		if (parseInt(inputMoney) < totalAmount) {
		    alert("投入金額不足");
		    return; // 不執行後續的 Ajax 请求
		}

	    // 發送 Ajax 请求到后端
	    $.ajax({
	        type: "POST",
	        url: "FrontendAction.do?action=buyGoods",
	        data: {
	            inputMoney: inputMoney
	        },
	        success: function(response) {
	            if (response.errorMessage) {
	                alert(response.errorMessage);
	            } else {
	                window.location.href = "FrontendAction.do?action=searchGoods";
	                $("#queryCart").click();
	                $("#cart-content").empty();
	                localStorage.setItem('cartCount', 0);
	                $("#queryCart").attr("data-cart-count", 0);
	                $("#queryCart").text("購物車(0)");
	            }
	        },
	        error: function(xhr, status, error) {
	            // 處理請求錯誤
	            console.log("Error: " + error);
	        }
	    });
	}

	
	
	</script>
<body align="center">

</body>
</html>

<table width="1200" height="300" align="center">
	<tr>
		<td colspan="2" align="right">
			<button id="queryCart" onclick="openCartPage()"
				data-cart-count="${sessionScope.cartCount}">購物車(${sessionScope.cartCount})</button>
                  <button id="clearCart" onclick="clearCart()">清空購物車</button>
<!--                   绑定事件和修改属性 -->
		</td>
	</tr>
	</td>
	</tr>
	<tr>
		<td colspan="2" align="right">
			<form action="FrontendAction.do" method="Post">
				<input type="hidden" name="action" value="searchGoods" /> <input
					type="text" name="searchKeyword" /> <input type="submit"
					value="搜尋" />
			</form>
		</td>
	</tr>
	<tr>

		<form action="FrontendAction.do" method="Post">
		<td width="400" height="200" align="center"><img border="0"
		src="DrinksImage/coffee.jpg" width="200" height="200">
		<h1>歡迎光臨，${member.customerName}！</h1> <a
		href="BackendAction.do?action=queryGoods" align="left">後臺頁面</a>&nbsp;
		&nbsp; <a href="LoginAction.do?action=logout" align="left">登出</a> <br />
<!-- 		<br /> <font face="微軟正黑體" size="4"> <b>投入:</b> <input -->
<!-- 		type="number" name="inputMoney" max="100000" min="0" size="5" -->
<!-- 		value="0"> <b>元</b> <b><input type="submit" value="送出"> -->
		<br /> 
		<br />
		</font>
				<div
					style="border-width: 3px; border-style: dashed; border-color: #FFAC55; padding: 5px; width: 300px;">
					<p style="color: blue;">~~~~~~~ 消費明細 ~~~~~~~</p>
					<p style="margin: 10px;">投入金額：${sessionScope.inputMoney}</p>
					<p style="margin: 10px;">購買金額：${sessionScope.buyMoney}</p>
					<p style="margin: 10px;">找零金額：${sessionScope.changeMoney}</p>
					<c:forEach items="${cartGood}" var="entry">
                 ${entry.key.goodsName} ${entry.key.goodsPrice} * ${entry.value}<br />
					</c:forEach>
				</div></td>
                  <% session.removeAttribute("inputMoney");%>
                       <% session.removeAttribute("buyMoney");%>
                            <% session.removeAttribute("changeMoney");%>
                                 <% session.removeAttribute("cartGood");%>
<%--                                                                           <% session.removeAttribute("cartCount");%>   --%>
                                        
			<td width="600" height="300">
			<input type="hidden" name="action" value="buyGoods" />

				<table border="1" style="border-collapse: collapse">
					<tr>
						<c:forEach items="${frontgoods}" var="i" varStatus="counter">

					    <c:if test="${counter.index % 3 == 0 and !counter.first}">
					</tr>
					<tr>
						</c:if>
                <td width="300" style="text-align: center;">
    <div style="display: flex; flex-direction: column; justify-content: space-between; height: 80%;">
        <div style="height: 60px;">
            <font face="微軟正黑體" size="5">
                ${i.goodsName} 
            </font>
        </div>
        <div class="product-image" style="text-align: center;">
            <img border="0" src="DrinksImage/${i.goodsImageName}" width="150" height="150" style="display: block; margin: auto;">
        </div>
        <div>
            <font face="微軟正黑體" size="4" style="color: gray;"> 
                ${i.goodsPrice} 元 
            </font>
        </div>
        <div>
            <font face="微軟正黑體" size="3"> 
                <input type="hidden" name="goodsID" value="${i.goodsID}">
                <!-- 購買<input type="number" name="buyQuantity" min="0" max="30" size="5" value="0">罐 -->
                <br> <br>
              <button class="add-to-cart" data-goods-id="${i.goodsID}" data-goods-quantity="${i.goodsQuantity}" style="display: block; margin: 0 auto; margin-top: -20px;">加入購物車</button>      
                <span class="stock-quantity" style="color: red;">(庫存 ${i.goodsQuantity} 罐)</span> 
            </font> 
<!--              .data("goods-id")方法获取，并用于后续的操作，例如发送Ajax请求。 -->
<!-- .data("goods-quantity")方法获取，并用于后续的操作，例如在添加商品到购物车时检查库存数量。 -->
        </div>
    </div>
</td>
						</c:forEach>
					</tr>
				</table>
		</form>
	<tr>

	<tr>
			<td colspan="2" align="right">
						<h3 class="page">
							<c:if test="${pageIndex > 1}">
								<a
									href="FrontendAction.do?action=searchGoods&amp;pageIndex=${pageIndex - 1}&amp;searchKeyword=${searchKeyword}">上一頁</a>
							</c:if>
						</h3> <c:forEach items="${pageList}" var="pageNum">
							<h3 class="page">
								<h3 class="page">
									<a
										href="FrontendAction.do?action=searchGoods&amp;pageIndex=${pageNum}&amp;searchKeyword=${searchKeyword}"
										style="${pageNum == pageIndex ? 'color:red; font-weight: bold;' : 'color:blue;'}">${pageNum}</a>
								</h3>
							</h3>
						</c:forEach>
						<h3 class="page">
							<c:if test="${pageIndex < totalPage}">
								<a
									href="FrontendAction.do?action=searchGoods&amp;pageIndex=${pageIndex + 1}&amp;searchKeyword=${searchKeyword}">下一頁</a>
							</c:if>
						</h3>
		</td>
	</tr>
</table>

</body>
</html>