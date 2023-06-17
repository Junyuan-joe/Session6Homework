package com.training.vo;

import java.util.ArrayList;
import java.util.List;

import com.training.model.Goods;

public class BuyGoods {
	private int inputMoney;
	private int buyMoney;
	private int changeMoney;
	
	List<Goods> goods = new ArrayList<>();

	public int getInputMoney() {
		return inputMoney;
	}
	public void setInputMoney(int inputMoney) {
		this.inputMoney = inputMoney;
	}
	public int getBuyMoney() {
		return buyMoney;
	}
	public void setBuyMoney(int buyMoney) {
		this.buyMoney = buyMoney;
	}
	public int getChangeMoney() {
		return changeMoney;
	}
	public void setChangeMoney(int changeMoney) {
		this.changeMoney = changeMoney;
	}
	public List<Goods> getGoods() {
		return goods;
	}
	public void addGoods(String goodsName, int goodsPrice, Integer integer) {
		Goods go = new Goods();
		go.setGoodsName(goodsName);
		
		go.setGoodsPrice(goodsPrice);
		
		go.setGoodsQuantity(integer);
		goods.add(go);
		
	}
	public void setErrorMessage(String string) {
		
		
	}
	

	}
	

