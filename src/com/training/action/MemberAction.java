package com.training.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.training.dao.FrontEndDao;
import com.training.formbean.Goodsform;
//import com.training.formbean.CartActionForm;
import com.training.model.Goods;
import com.training.service.MemberService;
import com.training.service.MemberService;
import com.training.vo.ShoppingCartGoods;
import com.training.vo.ShoppingCartGoodsInfo;

@MultipartConfig
public class MemberAction extends DispatchAction {
	
private static MemberService memberService = MemberService.getInstance();

private static FrontEndDao frontEndDao = FrontEndDao.getInstance();

public ActionForward addCartGoodsView(ActionMapping mapping, ActionForm form, 
	    HttpServletRequest req, HttpServletResponse response) throws IOException {
	

	return mapping.findForward("addCartGoodsView");
}
@SuppressWarnings({ "unchecked" })
public ActionForward addCartGoods(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response) throws IOException {
    HttpSession session = req.getSession();

    // 商品加入購物車
    String goodsIDParam = req.getParameter("goodsID");
    if (goodsIDParam != null && !goodsIDParam.isEmpty()) {
        long goodsID = Long.parseLong(goodsIDParam);

        String buyQuantity = req.getParameter("buyQuantity");
        int quantityToAdd = 0;
        try {
            quantityToAdd = Integer.parseInt(buyQuantity);//加入商品數
        } catch (NumberFormatException e) {
            // 處理數字格式錯誤的情況
            e.printStackTrace();
            return null;
        }

        Goods goods = frontEndDao.queryByGoods(goodsID);//查詢ID

        // 檢查庫存數量
        int stockQuantity = goods.getGoodsQuantity();

        // 將商品和購買數量存儲在購物車中
        Object cartGoodsObj = session.getAttribute("cartGoods");
        Map<Goods, Integer> cartGoods;

        if (cartGoodsObj instanceof Map<?, ?>) {//检查"cartGoodsObj"是否是一个映射
            cartGoods = (Map<Goods, Integer>) cartGoodsObj;//如果是，它将"cartGoodsObj"轉换为一个商品到整数的映射
        } else {
            cartGoods = new LinkedHashMap<>();//保持了元素插入的顺序
        }

        int existingQuantity = cartGoods.getOrDefault(goods, 0);
        int totalQuantity = existingQuantity + quantityToAdd;

        // 檢查新添加的商品數量是否超過庫存量
        if (totalQuantity > stockQuantity) {
            // 如果新添加的商品數量大於庫存量，不再允許添加，返回錯誤信息
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("{\"message\": \"加入商品已達庫存上限，無法繼續添加此商品\"}");
            out.flush();
            out.close();
            return null;
        }

        cartGoods.put(goods, totalQuantity);
        session.setAttribute("cartGoods", cartGoods); // 保存商品及其數量

        // 計算購物車中所有商品的總數量
        int cartCount = 0;
        for (Integer quantity : cartGoods.values()) {
            cartCount += quantity;
        }

        session.setAttribute("cartCount", cartCount);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        List<ShoppingCartGoods> shoppingCartGoods = new ArrayList<>();
        for (Map.Entry<Goods, Integer> entry : cartGoods.entrySet()) {
            ShoppingCartGoods shoppingCartGood = new ShoppingCartGoods();
            shoppingCartGood.setGoodsID(entry.getKey().getGoodsID()); // 使用 Map 中的商品 ID
            shoppingCartGood.setBuyQuantity(entry.getValue()); // 使用 Map 中的購買數量
            shoppingCartGoods.add(shoppingCartGood);
        }
        out.println(JSONArray.fromObject(shoppingCartGoods));
        out.flush();
        out.close();
    }
    return null;
}
public ActionForward queryCartGoodsView(ActionMapping mapping, ActionForm form,
        HttpServletRequest req, HttpServletResponse response) throws IOException {


	return mapping.findForward("queryCartGoodsView");
}

	public ActionForward queryCartGoods(ActionMapping mapping, ActionForm form,
	        HttpServletRequest req, HttpServletResponse response) throws IOException {
	    // 列出商品名稱、購買數量
	    // 購物車商品總金額
	    ShoppingCartGoodsInfo cartGoodsInfo = new ShoppingCartGoodsInfo();

	    HttpSession session = req.getSession();
	    Object cartGoodsObj = session.getAttribute("cartGoods");

	    if (cartGoodsObj instanceof Map<?, ?>) {
	        Map<Goods, Integer> cartGoods = new HashMap<>();//Key透過hashcode是否相同KEY
	        for (Map.Entry<?, ?> entry : ((Map<?, ?>) cartGoodsObj).entrySet()) {
	            if (entry.getKey() instanceof Goods && entry.getValue() instanceof Integer) {
	                cartGoods.put((Goods) entry.getKey(), (Integer) entry.getValue());
	            }
//	            遍历过程中，代码检查每個键和值是否分别是Goods类型和Integer类型。
//	            如果是，则将键和值强制转换为相应的类型，并将它们添加到cartGoods中。
	            
	            
	        }

	        Set<ShoppingCartGoods> cartGoodsSet = new HashSet<>(); // 初始化一個空的Set<ShoppingCartGoods>
	        int totalAmount = 0; // 初始化購物車商品總金額
	        int totalQuantity = 0; // 初始化購物車商品總數量

	        for (Map.Entry<Goods, Integer> entry : cartGoods.entrySet()) {
	            Goods goods = entry.getKey();
	            int quantity = entry.getValue();
	            totalQuantity += quantity; // 累加購物車商品總數量

	            Goods updatedGoods = frontEndDao.queryByGoods(goods.getGoodsID()); // 查詢資料庫最新商品價格
	            int subtotal = updatedGoods.getGoodsPrice() * quantity; // 計算商品小計
	            totalAmount += subtotal; // 累加到總金額

	            // 將每個鍵值對轉換為ShoppingCartGoods物件並添加到cartGoodsSet中
	            ShoppingCartGoods shoppingCartGoods = new ShoppingCartGoods();
	            shoppingCartGoods.setGoodsID(updatedGoods.getGoodsID());
	            shoppingCartGoods.setGoodsImageName(updatedGoods.getGoodsImageName());
	            shoppingCartGoods.setGoodsName(updatedGoods.getGoodsName());
	            shoppingCartGoods.setGoodsPrice(updatedGoods.getGoodsPrice());
	            shoppingCartGoods.setBuyQuantity(quantity);

	            cartGoodsSet.add(shoppingCartGoods);
	        }

	        // 將購物車商品資訊填充到cartGoodsInfo物件中
	        cartGoodsInfo.setShoppingCartGoods(cartGoodsSet);
	        cartGoodsInfo.setTotalAmount(totalAmount);
	        session.setAttribute("totalQuantity", totalQuantity); // 將購物車商品總數量存入session
	    
	    }


    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(JSONObject.fromObject(cartGoodsInfo));
    out.flush();
    out.close();

    return null;
}
//	public ActionForward increaseQuantity(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response) throws IOException {
//	    HttpSession session = req.getSession();
//	    Object cartGoodsObj = session.getAttribute("cartGoods");
//	    int goodsID = Integer.parseInt(req.getParameter("goodsID"));  	    
//	    if (cartGoodsObj instanceof Map<?, ?>) {
//	        @SuppressWarnings("unchecked")
//	        Map<Goods, Integer> cartGoods = (Map<Goods, Integer>) cartGoodsObj;
//
//	        Goods goods = frontEndDao.queryByGoods(goodsID); // 查詢資料庫商品並且加入購物車         
//	        if (goods != null) {
//	            int currentStock = goods.getGoodsQuantity(); // 假設 Goods 類有一個 getStock 方法可以返回當前庫存量
//	            int quantity = cartGoods.getOrDefault(goods, 0); // 獲取購物車中的商品數量，如果購物車不包含該商品，則返回0
//	            if(quantity + 1 > currentStock){
//	                sendResponseMessage(response, "加入商品已達庫存上限，無法繼續添加此商品");
//	                return null;         
//	            }
//	            cartGoods.put(goods, quantity + 1); // 更新購物車中的商品數量
//	            session.setAttribute("cartGoods", cartGoods); // 保存商品及其數量
//
//	            // 計算購物車中所有商品的總數量
//	            int cartCount = 0;
//	            for (Integer quantityValue : cartGoods.values()) {
//	                cartCount += quantityValue;
//	            }
//	            session.setAttribute("cartCount", cartCount);
//	        }
//	        // 將更新後的購物車數據轉換為 JSON 格式並返回給前端
//	        response.setCharacterEncoding("UTF-8");
//	        response.setContentType("application/json");
//	        PrintWriter out = response.getWriter();
//	        out.println(JSONArray.fromObject(cartGoods.values()));
//	        out.flush();
//	        out.close();
//	    }
//
//	    return null;
//	}
//	private void sendResponseMessage(HttpServletResponse response, String message) throws IOException {
//	    response.setCharacterEncoding("UTF-8");
//	    response.setContentType("application/json");
//    PrintWriter out = response.getWriter();
//	    out.println("{\"message\": \"" + message + "\"}");
//    out.flush();
//    out.close();
//}
	public ActionForward decreaseQuantity(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response) throws IOException {
	    HttpSession session = req.getSession();
	    Object cartGoodsObj = session.getAttribute("cartGoods");
	    int goodsID = Integer.parseInt(req.getParameter("goodsID"));  // 從請求中獲取商品ID

	    if (cartGoodsObj instanceof Map<?, ?>) {     
	        @SuppressWarnings("unchecked")
			Map<Goods, Integer> cartGoods = (Map<Goods, Integer>) cartGoodsObj;
	        Goods toRemove = null;  // 儲存需要被移除的商品

	        for (Map.Entry<Goods, Integer> entry : cartGoods.entrySet()) {//循环来遍历cartGoods數（键值对）
	            Goods goods = entry.getKey();//cartGoods.entrySet()返回一个Set集合

	            if (goods.getGoodsID() == goodsID) {
	                int newQuantity = Math.max(0, entry.getValue() - 1);  // 減少商品數量，數量不能少於0
	                if (newQuantity == 0) {
	                    toRemove = goods;  // 如果商品數量為0，則需要從購物車中移除
	                } else {
	                    cartGoods.put(goods, newQuantity);
	                }
	                break;
	            }
	        }

	        if (toRemove != null) {
	            cartGoods.remove(toRemove);  // 移除數量為0的商品
	        }

	        // 更新購物車數量      
	        int cartCount = cartGoods.values().stream().mapToInt(Integer::intValue).sum();
	        //cartGoods.values()返回一个包含购物车中所有值（商品数量）的Collection
	        //将Integer物件轉換为對應的基本类型int
	        session.setAttribute("cartCount", cartCount);

	        // 將更新後的購物車數據轉換為 JSON 格式並返回給前端
	        response.setCharacterEncoding("UTF-8");
	        response.setContentType("application/json");
	        PrintWriter out = response.getWriter();
	        out.println(JSONArray.fromObject(cartGoods.values()));
	        out.flush();
	        out.close();
	    }

	    return null;
	}

    public ActionForward clearCartGoods(ActionMapping mapping, ActionForm form, 
            HttpServletRequest req, HttpServletResponse response) throws IOException  {    	
    
    	 // 清空購物車
        HttpSession session = req.getSession();
        session.removeAttribute("cartGoods");
        session.removeAttribute("inputMoney");
        session.removeAttribute("buyMoney");
        session.removeAttribute("changeMoney");
        session.removeAttribute("cartGood");
        session.removeAttribute("cartCount");
      
        
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        List<ShoppingCartGoods> shoppingCartGoods = new ArrayList<>();
        out.println(JSONArray.fromObject(shoppingCartGoods));
        out.flush();
        out.close();

        return null;
}

    public ActionForward removeCartItem(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse response) throws IOException {
        HttpSession session = req.getSession();
        Object cartGoodsObj = session.getAttribute("cartGoods");
        int goodsID = Integer.parseInt(req.getParameter("goodsID"));  // 從請求中獲取商品ID

        if (cartGoodsObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
			Map<Goods, Integer> cartGoods = (Map<Goods, Integer>) cartGoodsObj;

            // 刪除與商品ID相符的項目
            Iterator<Map.Entry<Goods, Integer>> iterator = cartGoods.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Goods, Integer> entry = iterator.next();
                if (entry.getKey().getGoodsID() == goodsID) {
                    iterator.remove();
                    break;
                }
            }

            // 將更新後的購物車數據轉換為 JSON 格式並返回給前端
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(JSONArray.fromObject(cartGoods.values()));
            out.flush();
            out.close();
        }

        return null;
    }
    }






