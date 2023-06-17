package com.training.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.training.model.Goods;
import com.training.model.Member;

public class FrontEndDao {

	private static FrontEndDao frontEndDao = new FrontEndDao();

	private FrontEndDao() {
	}

	public static FrontEndDao getInstance() {
		return frontEndDao;
	}

	/**
	 * 前臺顧客登入查詢
	 * 
	 * @param identificationNo
	 * @return Member
	 */
	public static Member queryMemberById(String identificationNo) {
		Member member = null;
		String sql = "SELECT * FROM BEVERAGE_MEMBER WHERE IDENTIFICATION_NO = ?";

		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement prst = conn.prepareStatement(sql)) {
			prst.setString(1, identificationNo);
			ResultSet rs = prst.executeQuery();
			while (rs.next()) {
				member = new Member();
				member.setCustomerName(rs.getString("CUSTOMER_NAME"));
				member.setIdentificationNo(rs.getString("IDENTIFICATION_NO"));
				member.setPassword(rs.getString("PASSWORD"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return member;
	}
	
//分頁   查詢上架商品	
	public Set<Goods> searchGoods(String searchKeyword, int startRowNo, int endRowNo) {
	    Set<Goods> goods = new LinkedHashSet<>();
	    
	    String sql;
	    if (searchKeyword != null && !searchKeyword.isEmpty()) {
	        sql = "SELECT * FROM("
	            + " SELECT ROWNUM RN, GD.* FROM BEVERAGE_GOODS GD WHERE LOWER(GOODS_NAME) LIKE ? AND STATUS = 1)"
	            + " WHERE RN BETWEEN ? AND ?";
	    } else {
	        sql = "SELECT * FROM(" + " SELECT ROWNUM RN, GD.* FROM BEVERAGE_GOODS GD WHERE STATUS = 1)"
	            + " WHERE RN BETWEEN ? AND ?";
	    }

	    try (Connection conn = DBConnectionFactory.getOracleDBConnection();
	            PreparedStatement prst = conn.prepareStatement(sql)) {
	        if (searchKeyword != null && !searchKeyword.isEmpty()) {
	            prst.setString(1, "%" + searchKeyword + "%");
	            prst.setInt(2, startRowNo);
	            prst.setInt(3, endRowNo);
	        } else {
	            prst.setInt(1, startRowNo);
	            prst.setInt(2, endRowNo);
	        }
	        ResultSet rs = prst.executeQuery();
	        while (rs.next()) {
	            Goods gd = new Goods();
	            gd.setGoodsID(rs.getLong("GOODS_ID"));
	            gd.setGoodsName(rs.getString("GOODS_NAME"));
	            gd.setGoodsPrice(rs.getInt("PRICE"));
	            gd.setGoodsQuantity(rs.getInt("QUANTITY"));
	            gd.setGoodsImageName(rs.getString("IMAGE_NAME"));
	            gd.setStatus(rs.getString("STATUS"));
	            goods.add(gd);
	        }
	        rs.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return goods;
	}
	/**
	 * 統計關鍵字搜索商品的總數
	 * 
	 * @param searchKeyword
	 * @return int
	 */
	public int countSearchGoods(String searchKeyword) {
		int count = 0;
		String sql = "SELECT COUNT(*) FROM BEVERAGE_GOODS WHERE LOWER(GOODS_NAME) LIKE ? AND STATUS = 1";

		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				PreparedStatement prst = conn.prepareStatement(sql)) {
			// 設置查詢參數，將搜索關鍵字轉換為小寫，並使用LIKE語句進行匹配
			// 如果搜索關鍵字為null，則將查詢參數設置為"%"，表示匹配所有記錄
			if (searchKeyword != null) {
				prst.setString(1, "%" + searchKeyword.toLowerCase() + "%");
			} else {
				prst.setString(1, "%");
			}
			// 執行查询並取结果集
			ResultSet rs = prst.executeQuery();
			if (rs.next()) {
				// 如果结果集中有数据，则将第一列的值作为商品数量
				count = rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();//拋出异常的类型、异常的消息和異常发生的位置
			
		}

		return count;
	}

	/**
	 * 查詢顧客所購買商品資料(價格、庫存)
	 * 
	 * @param goodsIDs
	 * @return Map(BigDecimal, Goods)
	 */
	public Map<BigDecimal, Goods> queryBuyGoods(Set<BigDecimal> goodsIDs) {//不包含重复元素的集合
		// key:商品編號、value:商品
		    Map<BigDecimal, Goods> goods = new LinkedHashMap<>();//按照插入顺序或者访问顺序来排序/允许使用null键和null值/非同步的
		   
		    
		  //查询到的商品数
		String querySQL = "SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID = ? ";
		try (Connection conn = DBConnectionFactory.getOracleDBConnection();
				                  //  Statement 一次性存取DB	/靜態
			PreparedStatement stmt = conn.prepareStatement(querySQL)) {//預先編譯語法/編譯要重複的sql/批次存取DB/防止注入攻擊/動態
			//遍历商品ID集合，对每个商品ID执行查询
			for (BigDecimal o : goodsIDs) {

				stmt.setBigDecimal(1, o);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						Goods go = new Goods();
						go.setGoodsID(rs.getLong("GOODS_ID"));
						go.setGoodsName(rs.getString("GOODS_NAME"));
						go.setGoodsPrice(rs.getInt("PRICE"));
						go.setGoodsQuantity(rs.getInt("QUANTITY"));
						go.setGoodsImageName(rs.getString("IMAGE_NAME"));
						go.setStatus(rs.getString("STATUS"));						
						goods.put(o, go);
					}

				}

			}
		} catch (SQLException e) {

		}

		return goods;
	}

	/**
	 * 交易完成更新扣商品庫存數量
	 * 
	 * @param goods
	 * @return boolean
	 * @throws SQLException
	 * 
	 */
	//Set類型的參數goods，返回一個boolean類型的結果，並且可能拋出一個SQLException異常
	public boolean batchUpdateGoodsQuantity(Set<Goods> goods) throws SQLException {
		boolean updateSuccess = false;
		
		// Step1:取得Connection
		try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
			String updateSql = "UPDATE BEVERAGE_GOODS SET QUANTITY=? WHERE GOODS_ID=?";
			try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
				// Step3:將"資料欄位編號"、"資料值"作為引數傳入
				for (Goods o : goods) {

					stmt.setInt(1, o.getGoodsQuantity());
					stmt.setLong(2, o.getGoodsID());
					stmt.addBatch();// 多動態加入批次
				}
				int[] insertCounts = stmt.executeBatch();// 執行sql批次更新回傳比數

				for (int s : insertCounts) {
					if (s == 0)
						return false;
				}
				updateSuccess = true;

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return updateSuccess;
		}
	}

	/**
	 * 建立訂單資料
	 * 
	 * @param customerID
	 * @param goodsOrders【訂單資料(key:購買商品、value:購買數量)】
	 * @return boolean
	 */
	public boolean batchCreateGoodsOrder(String customerID, Map<Goods, Integer> goodsOrders) {
		boolean insertSuccess = false;
		Member member = null;
		try (Connection conn = DBConnectionFactory.getOracleDBConnection()) {
			// 關閉自動提交
			conn.setAutoCommit(false);

			String updateSql = "INSERT INTO BEVERAGE_ORDER (ORDER_ID, ORDER_DATE, CUSTOMER_ID, GOODS_ID, GOODS_BUY_PRICE, BUY_QUANTITY) "
					+ "VALUES (BEVERAGE_ORDER_SEQ.NEXTVAL, SYSDATE, ?, ?, ?, ?)";
			// 執行插入订单的批处理
			try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
				// 遍歷商品訂單数据，對每個商品進行操作
				for (Goods G : goodsOrders.keySet()) {
					// 检查商品是否存在
					String querySql = "SELECT * FROM BEVERAGE_GOODS WHERE GOODS_ID = ?";
					try (PreparedStatement checkStmt = conn.prepareStatement(querySql)) {
						checkStmt.setLong(1, G.getGoodsID());
						ResultSet rs = checkStmt.executeQuery();
						if (!rs.next()) {
							// 商品不存在，回滚事物
							conn.rollback();
							return false;
						}
						// 商品存在，插入订单
						int add = 0;
						stmt.setString(++add, customerID);
						stmt.setLong(++add, G.getGoodsID());
						stmt.setInt(++add, G.getGoodsPrice());
						stmt.setInt(++add, goodsOrders.get(G));
						stmt.addBatch();//批次楚理可以一次執行多條SQL语句
					}
				}
				int[] insert = stmt.executeBatch();//執行sql批次更新回傳比數
				for (int s : insert) {
					if (s == 0) {
				// 若有任何一筆資料插入失敗，則回滾事務
				conn.rollback();
				return false;
					}
				}
				insertSuccess = true;

				// 提交事务
				conn.commit();
			} catch (SQLException e) {
				// 發生異常時回滚
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return insertSuccess;
	}

	

	public Goods queryByGoods(long goodsID) {
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

	
	public void updateCartGoodsQuantity(long goodsID, int quantity) {
	    // 執行更新商品數量的 SQL 語句，productId 是商品的 ID，quantity 是更新後的數量
	    String sql = "UPDATE BEVERAGE_GOODS SET QUANTITY = ? WHERE GOODS_ID = ?";
	    try (Connection conn = DBConnectionFactory.getOracleDBConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, quantity);
	        stmt.setLong(2, goodsID);
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	}


