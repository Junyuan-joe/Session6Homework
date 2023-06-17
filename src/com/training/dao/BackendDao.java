package com.training.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import com.training.model.Goods;
import com.training.vo.SalesReport;

import oracle.net.aso.e;



public class BackendDao {
	
private static BackendDao backendDao = new BackendDao();
	
	private BackendDao(){ }

	public static BackendDao getInstance(){
		return backendDao;
	}
	
	
	/**
	 * 後臺管理商品列表
	 * @return Set(Goods)
	 */
	public List<Goods> queryGoods() {
		List<Goods> goods = new ArrayList<>();	
		String querySQL = "SELECT * FROM BEVERAGE_GOODS";
		// Step1:取得Connection
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				// Step2:Create Statement
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(querySQL)) {
		
			while (rs.next()) {
				Goods gos = new Goods();
				gos.setGoodsID(rs.getLong("GOODS_ID"));
				gos.setGoodsName(rs.getString("GOODS_NAME"));
				gos.setGoodsPrice(rs.getInt("PRICE"));
				gos.setGoodsQuantity(rs.getInt("QUANTITY"));
				gos.setGoodsImageName(rs.getString("IMAGE_NAME"));
				gos.setStatus(rs.getString("STATUS"));
				//emp.setDeptno(rs.getInt("DEPTNO"));
				goods.add(gos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return goods;
	}
	/**
	 * 後臺管理商品列表
	 * @return Set(Goods)
	 */
	public List<Goods> queryGoods1() {
		List<Goods> goods = new ArrayList<>();	
		String querySQL = "SELECT * FROM BEVERAGE_GOODS WHERE STATUS = 1";
		// Step1:取得Connection
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				// Step2:Create Statement
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(querySQL)) {
		// try-with-resources 不再需要，就會自動被關閉，以釋放相關資源。這是一種確保資源在用完後能被適當清理的好方式。
			while (rs.next()) {
				Goods gos = new Goods();
				gos.setGoodsID(rs.getLong("GOODS_ID"));
				gos.setGoodsName(rs.getString("GOODS_NAME"));
				gos.setGoodsPrice(rs.getInt("PRICE"));
				gos.setGoodsQuantity(rs.getInt("QUANTITY"));
				gos.setGoodsImageName(rs.getString("IMAGE_NAME"));
				gos.setStatus(rs.getString("STATUS"));
				//emp.setDeptno(rs.getInt("DEPTNO"));
				goods.add(gos);
			}
		} catch (SQLException e) {
			e.printStackTrace();//處理異常
		}
		
		return goods;
	}

	
	
	
		
	/**
	 * 後臺管理商品列表
	 * @return Set(Goods)
	 */
	//分頁查詢商品資訊
	public List<Goods> queryGoodsPage(int pageSize,int pageIndex) {
		List<Goods> goods = new ArrayList<>();	
		String querySQL = "SELECT * FROM BEVERAGE_GOODS WHERE rownum >= ? AND rownum <= ?";
		//每一行都返回一个唯一的数值，表示这一行在查询结果中的行号，数值是從 1 開始的
		//在查询语句中，你可以使用 rownum 作为筛选或排序
		// Step1:取得Connection
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
			  PreparedStatement stmt = conn.prepareStatement(querySQL)){
			//使用參數化查詢可以防止 SQL 注入攻擊。因為參數值在插入查詢之前會被適當地轉義
            // 當你需要執行相同的 SQL 查詢多次，但是每次使用不同的參數值時			
			    stmt.setInt(1, pageSize);
			    stmt.setInt(2, pageIndex);
			    ResultSet rs = stmt.executeQuery();
				
			
			while (rs.next()) {
				Goods gos = new Goods();
				gos.setGoodsID(rs.getLong("GOODS_ID"));
				gos.setGoodsName(rs.getString("GOODS_NAME"));
				gos.setGoodsPrice(rs.getInt("PRICE"));
				gos.setGoodsQuantity(rs.getInt("QUANTITY"));
				gos.setGoodsImageName(rs.getString("IMAGE_NAME"));
				gos.setStatus(rs.getString("STATUS"));
				//emp.setDeptno(rs.getInt("DEPTNO"));
				goods.add(gos);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return goods;
	}	
		
		
		
	
	
	
	/**
	 * 後臺管理新增商品
	 * @param goods
	 * @return int(商品編號)
	 */
	public int addGoods(Goods goods){
	    int goodsID=0;

	    System.out.println("Adding to DataBase...");
	    String[] cols = { "GOODS_ID" };
	    try (Connection conn = DBConnectionFactory.getOracleDBConnection()){
	        // 設置交易不自動提交
	        conn.setAutoCommit(false);
	        // Insert SQL
	        String insertSQL = "INSERT INTO BEVERAGE_GOODS (GOODS_ID, GOODS_NAME, PRICE, QUANTITY, IMAGE_NAME, STATUS) VALUES (BEVERAGE_GOODS_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";
	        // Step2:Create prepareStatement For SQL
	        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL,cols)){
	            // Step3:將"資料欄位編號"、"資料值"作為引數傳入
	            pstmt.setString(1, goods.getGoodsName());
	            pstmt.setInt(2, goods.getGoodsPrice());
	            pstmt.setInt(3,goods.getGoodsQuantity());
	            pstmt.setString(4, goods.getGoodsImageName());
	            pstmt.setString(5, goods.getStatus());
	            pstmt.executeUpdate();

	            ResultSet psKeys = pstmt.getGeneratedKeys();
               //自动递增的主键/新记录生成一个唯一的主键/pstmt.executeUpdate();
	            while (psKeys.next()) {//调用 getGeneratedKeys() 方法获取生成的键/结果集中提取生成的键的值
	            	
	                goodsID = Integer.parseInt(psKeys.getString(1));
	            }

	            // Step5:Transaction commit(交易提交)
	            conn.commit();
	        } catch (SQLException e) {
	            // 若發生錯誤則資料 rollback(回滾)
	            conn.rollback();
	            throw e;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return goodsID;
	}
	
	/**
	 * 後臺管理更新商品
	 * @param goods
	 * @return boolean
	 */
	public int updateGoods(Goods goods) {
	    int updateSucces = 0;
	    String select = "SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID=?";
	    int quantity = 0;
	    String status = "";
	    try(Connection conn = DBConnectionFactory.getOracleDBConnection();
	       PreparedStatement prst = conn.prepareStatement(select)){
	        prst.setLong(1, goods.getGoodsID());
	        ResultSet rs = prst.executeQuery();
	        while(rs.next()){
	            quantity = rs.getInt("QUANTITY");
	            status = rs.getString("STATUS");//狀態
	        }
	    } catch (SQLException e0) {
	        e0.printStackTrace();
	    }
	    
	    
	    String sql = "UPDATE BEVERAGE_GOODS SET PRICE=?, QUANTITY=?, STATUS=? " +
	            "WHERE GOODS_ID=?";
	    try(Connection conn = DBConnectionFactory.getOracleDBConnection();
	            PreparedStatement prst = conn.prepareStatement(sql)){
	    	//可以防止 SQL 注入攻击，并且在执行多次相同的 SQL 语句时，
	    	//使用 PreparedStatement 会比使用 Statement 較好
	    	
	    	
	        int counter = 0;
	        prst.setInt(++counter, goods.getGoodsPrice());
	
	     
	            int total = goods.getGoodsQuantity() + quantity;//庫存數量+新輸入數量
	            prst.setInt(++counter, total);

	        
	        if(goods.getStatus()!=null && !goods.getStatus().isEmpty()){//狀態
	            prst.setString(++counter, goods.getStatus());
	        }else{
	            prst.setString(++counter, status);
	        }   
	        prst.setLong(++counter, goods.getGoodsID());
	        if(prst.executeUpdate()>0){//执行更新操作，如果更新成功，那么 updateSucces 的值会增加 1。

                                     //  返回 updateSucces，如果它的值大于 0，那么表示更新操作成功。
	            ++updateSucces;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return updateSucces;
	}
        
	public int getOriginalQuantity(long goodsID) {
	    String select = "SELECT QUANTITY FROM BEVERAGE_GOODS WHERE GOODS_ID=?";
	    int quantity = 0;

	    try (Connection conn = DBConnectionFactory.getOracleDBConnection();
	         PreparedStatement prst = conn.prepareStatement(select)) {
	        prst.setLong(1, goodsID);
	        ResultSet rs = prst.executeQuery();
	        if (rs.next()) {
	            quantity = rs.getInt("QUANTITY");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return quantity;
	}
	
	/**
 * 後臺管理刪除商品
	 * @param goodsID
 * @return boolean
	 */
	public int deleteGoods(long goodsID) {
	    int success = 0;
	    
	    try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
      conn.setAutoCommit(false);
        
	      
        String deleteChildSQL = "DELETE FROM BEVERAGE_ORDER WHERE GOODS_ID = ?";
        try (PreparedStatement childStmt = conn.prepareStatement(deleteChildSQL)) {
            childStmt.setLong(1, goodsID);	           
            childStmt.executeUpdate();
	        }

      
        String deleteSQL = "DELETE FROM BEVERAGE_GOODS WHERE GOODS_ID = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
	            stmt.setLong(1, goodsID);
	            success = stmt.executeUpdate();
	            conn.commit();
	        } catch (SQLException e) {
	            conn.rollback();
	            throw e;
	        }            
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return success;
	}
	
	
	/**
	 * 後臺管理顧客訂單查詢
	 * @param queryStartDate
	 * @param queryEndDate	 
	 */
	public  List<SalesReport> querySalesReport(String queryStartDate, String queryEndDate) {
		List<SalesReport> reports = new ArrayList<>();	
		String querySQL ="SELECT S.ORDER_ID,M.CUSTOMER_NAME, "+
				"s.ORDER_DATE, "+
			"G.GOODS_NAME,S.GOODS_BUY_PRICE,S.BUY_QUANTITY "+
			"FROM BEVERAGE_ORDER S "+
			"LEFT JOIN BEVERAGE_MEMBER M ON S.CUSTOMER_ID=M.IDENTIFICATION_NO "+
			"LEFT JOIN BEVERAGE_GOODS G ON S.GOODS_ID=G.GOODS_ID "+
			"WHERE TRUNC(S.ORDER_DATE) BETWEEN to_date(?,'"+ "yyyy-MM-dd') AND to_date(?,'yyyy-MM-dd')";
			
			//TRUNC函数用于去除日期的时间部分，只保留日期
		
	     // Step1:取得Connection
	try (Connection conn = DBConnectionFactory.getOracleDBConnection();
		 // Step2:Create PreparedStatement For SQL
		 PreparedStatement stmt = conn.prepareStatement(querySQL)){
		// 設置查詢的欄位值
	
		stmt.setString(1,queryStartDate);
		stmt.setString(2,queryEndDate); 
		ResultSet rs = stmt.executeQuery();				
		// Step3:Process Result
		
		while (rs.next()) {
			
			SalesReport report=new SalesReport();
			report.setOrderID(rs.getLong("ORDER_ID"));
			report.setCustomerName(rs.getString("CUSTOMER_NAME"));
			java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
			java.util.Date utilDate = new java.util.Date(sqlDate.getTime());

			report.setOrderDate(rs.getDate("ORDER_DATE"));
			report.setGoodsName(rs.getString("GOODS_NAME"));
			report.setGoodsBuyPrice(rs.getInt("GOODS_BUY_PRICE"));
			report.setBuyQuantity(rs.getInt("BUY_QUANTITY"));
		   //report.getGoodsBuyPrice()*report.getBuyAmount()
			report.setBuyAmount(report.getGoodsBuyPrice()*report.getBuyQuantity());
			//report.setBuyAmount(rs.getInt("BUY_AMOUNT"));
			reports.add(report);											   						
			
		}	
		} catch (SQLException e) {						
	}
	
	return reports;

}


	
public Goods queryGoodsById(Long goodsID){
	Goods goods = null;		
	// querySQL SQL
	String querySQL = "SELECT GOODS_ID, GOODS_NAME, PRICE, QUANTITY, IMAGE_NAME, STATUS FROM BEVERAGE_GOODS WHERE GOODS_ID = ?";		
	// Step1:取得Connection
	try (Connection conn = DBConnectionFactory.getOracleDBConnection();
	    // Step2:Create prepareStatement For SQL
		PreparedStatement stmt = conn.prepareStatement(querySQL)){
		stmt.setLong(1,goodsID);
		try(ResultSet rs = stmt.executeQuery()){
			if(rs.next()){
				goods = new Goods();
				goods.setGoodsID(rs.getLong("GOODS_ID"));
				goods.setGoodsName(rs.getString("GOODS_NAME"));
				goods.setGoodsPrice(rs.getInt("PRICE"));
				goods.setGoodsQuantity(rs.getInt("QUANTITY"));
			    goods.setGoodsImageName(rs.getString("IMAGE_NAME"));
				goods.setStatus(rs.getString("STATUS"));
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
		
	
	return goods;
	}



}
	
	
