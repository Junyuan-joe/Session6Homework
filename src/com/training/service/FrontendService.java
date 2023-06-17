package com.training.service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.training.dao.FrontEndDao;
import com.training.model.Goods;
import com.training.model.Member;
import com.training.vo.BuyGoods;

public class FrontendService {
	private static FrontendService frontendService = new FrontendService();
	private FrontendService(){}
	public static FrontendService getInstance()
	{
		return frontendService;
	}
	
	private FrontEndDao frontendDao = FrontEndDao.getInstance(); 
	
	public Set<Goods> searchGoods(String searchKeyword, int startRowNo, int endRowNo){
		
		if(searchKeyword==null||searchKeyword.equals("")){
			
		
		}	
			return frontendDao.searchGoods(searchKeyword, startRowNo, endRowNo);
		

		
	
	}


	
	public BuyGoods buyGoods(String customerID, String[] goods, String[] quantities, int inputMoney) throws SQLException {
	    BuyGoods goodsPay = new BuyGoods();
	    goodsPay.setInputMoney(inputMoney);

	    Set<BigDecimal> goodsIDs = new LinkedHashSet<>();//元素不会有重复，即每个元素都是唯一/插入顺序和迭代顺序是一致

	    // 將Map<BigDecimal, Integer> buyMap修改為Map<BigDecimal, List<Integer>> buyMap
	    Map<BigDecimal,List<Integer>> buyMap = new HashMap<>();
	    int total = 0;

	    // 將購買數量添加到商品ID對應的列表中
	    for (int i = 0; i < goods.length; i++) {
	        if (quantities[i] != null && !quantities[i].equals("0")) {
	            BigDecimal goodsID = new BigDecimal(goods[i]);
	            goodsIDs.add(goodsID);
	            if (buyMap.containsKey(goodsID)) {
	            	//如果buyMap中已經包含了當前的商品ID
		         	//那麽它就將購買數量添加到對應的列表中；否則，它就創建一個新的列表
		            //將購買數量添加到新的列表中，然後將商品ID和新的列表添加到buyMap中。
	                List<Integer> quantityList = buyMap.get(goodsID);
	                quantityList.add(Integer.parseInt(quantities[i]));
	            } else {
	                List<Integer> quantityList = new ArrayList<>();
	                quantityList.add(Integer.parseInt(quantities[i]));
	                buyMap.put(goodsID, quantityList);
	            }//商品品的ID就會在goods[]數組中出現多次
		        }//每次出現的購買數量可能不同這段代碼將所有的購買數量都保存在buyMap映射中
	    }

	    // 查询購買的商品信息
	    Map<BigDecimal, Goods> buyGoods = frontendDao.queryBuyGoods(goodsIDs);

	    // 计算購買商品總金额和扣除庫存
	    for (BigDecimal goodsID : goodsIDs) {
	        if (buyGoods.containsKey(goodsID)) {
	            Goods goodsItem = buyGoods.get(goodsID);
	            int maxQuantity = goodsItem.getGoodsQuantity();
	            List<Integer> quantityList = buyMap.get(goodsID);

	            int totalQuantity = quantityList.stream().mapToInt(Integer::intValue).sum();

	            // 如果要購買的商品数量大於庫存数量，抛出一个异常
	            if (totalQuantity > maxQuantity) {
                throw new SQLException("Not enough stock for goodsID: " + goodsID);
	            }

	            if (totalQuantity > 0) {
	                // 计算商品金额和扣除库存
	                total += goodsItem.getGoodsPrice() * totalQuantity;
	                goodsItem.setGoodsQuantity(maxQuantity - totalQuantity);
	            }
	        }
	    }

	    // 判断付款金额和计算找零金额
	    int changeMoney = inputMoney - total;
	    if (inputMoney < total) {
	        // 付款金额不足，直接將購買金額作為找零
	        changeMoney = inputMoney;
	        total = 0; // 將購買金額設為0，表示未購買任何商品
	        buyMap.clear(); // 清空購買商品的映射
	    }

	    // 創建商品订單并批量更新庫存
	    if (changeMoney >= 0) {
	        Map<Goods, Integer> goodsOrders = new HashMap<>();//键（Key）和值（Value）都可以是任何类型的对象，也可以是null/键不能重复，但是值可以
	        for (BigDecimal goodsID : goodsIDs) {             //HashMap是非同步的，也就是说它不是线程安全
	            List<Integer> quantityList = buyMap.get(goodsID);
	            int totalQuantity = quantityList.stream().mapToInt(Integer::intValue).sum();
              //對於每個ID取對應的数量列表，然后计算出總数量
	            if (totalQuantity > 0) {
	                Goods goodsItem = buyGoods.get(goodsID);
	                goodsOrders.put(goodsItem, goodsOrders.getOrDefault(goodsItem, 0) + totalQuantity);
	            }//buyGoods中取对应的商品物件，然后在goodsOrders中更新该商品的数量
	        }//取键为 goodsItem 的值。如果 goodsOrders 中不存在键为 goodsItem ，那么就返回默认值 0。

	        frontendDao.batchCreateGoodsOrder(customerID, goodsOrders);//建立訂單
	        goodsPay.setBuyMoney(total);
	        goodsPay.setChangeMoney(changeMoney);

	        for (Goods goodsItem : goodsOrders.keySet()) {
	            goodsPay.addGoods(goodsItem.getGoodsName(), goodsItem.getGoodsPrice(), goodsOrders.get(goodsItem));
	        }

	        // 批量更新数据库中商品的库存数量
	        frontendDao.batchUpdateGoodsQuantity(goodsOrders.keySet());
	    }

	    return goodsPay;
	}
	public int countSearchGoods(String searchKeyword) {//關鍵字搜索商品
	    return frontendDao.countSearchGoods(searchKeyword);
	}
	      
	}

