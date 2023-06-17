package com.training.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.training.dao.BackendDao;
import com.training.model.Goods;

import com.training.vo.SalesReport;

public class BackendService {

	private static BackendService backendService = new BackendService();

	public BackendService() {
	}

	public static BackendService getInstance() {
		return backendService;
	}

	private BackendDao backendDao = BackendDao.getInstance();

	public List<Goods> queryGoods() {

		return backendDao.queryGoods();
	}

	public List<Goods> queryGoodsPage(int pageSize, int pageIndex) {

		return backendDao.queryGoodsPage(pageSize, pageIndex);

	}

	public int addGoods(Goods goods) {
		return backendDao.addGoods(goods);

	}

	public int updateGoods(Goods goods) {
		return backendDao.updateGoods(goods);
	}

	public int getOriginalQuantity(long goodsID) {
		return backendDao.getOriginalQuantity(goodsID);
	}

	public List<SalesReport> querySalesReport(String queryStartDate, String queryEndDate) {

		return backendDao.querySalesReport(queryStartDate, queryEndDate);
	}

//	public int deleteGoods(long goodsID) {
//
//		return backendDao.deleteGoods(goodsID);
//
//	}

	public Goods queryGoodsById(long goodsID) {

		return backendDao.queryGoodsById(goodsID);
	}

//	public List<Goods> queryGoods(String deleteMsg) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	public List<Goods> queryGoods1() {
		return backendDao.queryGoods1();
	}
}