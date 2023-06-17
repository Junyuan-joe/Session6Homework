package com.training.action;


import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.*;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import com.training.dao.BackendDao;
import com.training.formbean.Goodsform;
import com.training.model.Goods;
import com.training.service.BackendService;
import com.training.service.FrontendService;

import com.training.vo.SalesReport;

import net.sf.json.JSONObject;

public class BackendActionAjax extends DispatchAction {
	private BackendDao backendDao=BackendDao.getInstance();
	private BackendService backendService = BackendService.getInstance();
	
	public ActionForward queryGoods(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			
					throws Exception{
	
		
		List<Goods> goodsList = backendService.queryGoods();
				
	
		request.setAttribute("goodsList", goodsList);//查詢商品
			
			return mapping.findForward("queryGoods");
		

		
		
	
	}
	public ActionForward addGoodsView(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{

		return mapping.findForward("addGoodsView");
	}
		
	public ActionForward addGoods(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		HttpSession session=request.getSession();
		//String createMsg=null;
		Goodsform goodsbean = (Goodsform) form;
		//進來的ActionForm物件轉換為Goodsform類型的變數goodsbean
		

		goodsbean.setGoodsID(Long.parseLong("0"));
		//商品的ID設置為0，是用於指定一個默認的商品ID
		
		Goods go = new Goods();
		
		BeanUtils.copyProperties(go, goodsbean);//屬性的複製
		
		//先處理圖片
		FormFile file = goodsbean.getGoodsImage();
		//從goodsbean中獲取商品圖片的FormFile物件
		
		String fileName = file.getFileName();//上傳文件的文件名
		
		go.setGoodsImageName(fileName);
		//商品物件go的圖片名稱屬性設置為上傳文件的文件名
		
        FileOutputStream fileOutput = new FileOutputStream("C:/home/VendingMachine/DrinksImage/" 
        		+ fileName); //指定路徑

        fileOutput.write(file.getFileData()); //上傳資料寫入到資料輸出流
        
        fileOutput.flush(); //刷新
        
        fileOutput.close(); //關閉
        
        file.destroy() ;
        
        int goodsID = backendService.addGoods(go);
        String createMsg=null;
        
		if(goodsID > 0){ 
		
		 createMsg="商品新增上架成功！ 商品編號：" + goodsID;
	
		}else{
			
		createMsg="商品新增上架失敗！";
		}
		session.setAttribute("createMsg",createMsg);
		
		return mapping.findForward("addGoods");
	}
	
	public ActionForward updateGoodsView(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpSession session=request.getSession();
		
	List<Goods> goodsList = backendService.queryGoods();
	
       session.setAttribute("goodsList", goodsList);//選擇商品列    
         
  String selectGoodsID=request.getParameter("goodsID");
  
   Object updateGoodsID=session.getAttribute("updateGoodsID");


   if (selectGoodsID != null && !selectGoodsID.isEmpty()) {
    	
    	Long goodsID = updateGoodsID != null ? (long) updateGoodsID : null;
    	goodsID =selectGoodsID !=null ? Long.parseLong(selectGoodsID) : goodsID;
    	Goods goods= backendService.queryGoodsById(goodsID);
  
    	 // 将查尋到的商品信息设置为属性，以便在goods中使用
        request.setAttribute("goods", goods);
   }
    	
 
	return mapping.findForward("updateGoodsView");
	}


	public ActionForward getupdateGoodsView(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		HttpSession session=request.getSession();
	
		 String selectGoodsID=request.getParameter("goodsID");
		 Object updateGoodsID=session.getAttribute("updateGoodsID");
		  
		 if (selectGoodsID != null && !selectGoodsID.isEmpty()) {
		    	
		  Long goodsID = updateGoodsID != null ? (long) updateGoodsID : null;
		  goodsID =selectGoodsID !=null ? Long.parseLong(selectGoodsID) : goodsID;
		  Goods goods= backendService.queryGoodsById(goodsID);
			//goodsID的参数值存在且非空，方法就将"updateGoodsID"的属性值转换为Long类型的商品ID，
		  //如果"updateGoodsID"的属性值不存在，那么商品ID就设置为null
		  //接着，方法使用"goodsID"的参数值覆盖商品ID，如果"goodsID"的参数值不存在，那么商品ID就保持不变
			
           response.setCharacterEncoding("UTF-8");
           response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.print(JSONObject.fromObject(goods));
			out.flush();
			out.close();
	
		}
		return null;
		       
	}
	public ActionForward updateGoods(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) throws Exception {

	    HttpSession session = request.getSession();
	
	  
    
	    Goodsform goodsbean = (Goodsform) form;////進來的ActionForm物件轉換為Goodsform類型的變數goodsbean

	    Goods go = new Goods();

	    BeanUtils.copyProperties(go, goodsbean);
  
	    	    
	    int goodsID = backendService.updateGoods(go);
	

	  
	  
	    session.setAttribute("updateGoods", go);
   
	    
//	    List<Goods> goodsList = backendService.queryGoods1();//計算上下架筆數


	    String updateMsg = "";
	    if (goodsID > 0) {
	        updateMsg = "商品更新作業成功！ 商品編號：" + go.getGoodsID();
	    }

	    session.setAttribute("updateMsg", updateMsg);
	   	
	 
	    return mapping.findForward("updateGoods");


}
	
	public ActionForward queryGoodsPage(ActionMapping mapping, ActionForm form, 
		    HttpServletRequest request, HttpServletResponse response) throws Exception {
		    
		int pageSize = 10; // 每頁顯示 10 筆資料
		int pageIndex = 1; // 預設為第 1 頁
		String pageIndexStr = request.getParameter("pageIndex");

		if (pageIndexStr != null && !pageIndexStr.isEmpty()) {
		    pageIndex = Integer.parseInt(pageIndexStr);
		}//請求中的"pageIndex"參數，
		 //並將其轉換為整數類型。如果參數存在且非空，則將其作為當前頁碼

		List<Goods> goodsList = backendService.queryGoods();//所有的商品列表
		int totalPage = (int) Math.ceil((double) goodsList.size() / pageSize);//計算總頁數
//		Math.ceil()方法對goodsList.size()除以每頁顯示數量
//		（pageSize）進行除法運算，並向上取整。這是為了確定總頁數，
//		確保每一頁能夠完整地容納指定的數。由於結果可能是浮點數，
//		所以再次使用類型轉換(int)將結果轉換為整數類型，以便得到最終的總頁數
		
		int startIndex = (pageIndex - 1) * pageSize;//計算起始索引和結束索引
		//根據當前頁碼和每頁顯示的數量，計算出當前頁碼對應的數據在商品列表中的		
		int endIndex = Math.max(startIndex, Math.min(startIndex + pageSize, goodsList.size())); 
		//使用Math.min()函數，將預計的結束索引與商品列表的總條數進行比較，取其中較小的值。這是為了確保結束索引不會超出商品列表的範圍。

        //使用Math.max()函數，將較小的值與起始索引進行比較，取其中較大的值。這是為了確保結束索引不會小於起始索引，避免出現不合理的索引範圍。

		List<Goods> pageGoodsList = goodsList.subList(startIndex, endIndex); // 分頁商品列表
       //通過goodsList.subList(startIndex, endIndex)方法，截取商品列表的指定範圍，得到當前頁碼對應的商品列表。
		
		request.setAttribute("pageGoodsList", pageGoodsList);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("pageIndex", pageIndex);
		request.setAttribute("pageSize", pageSize);

		// 動態生成分頁按鈕
		int startPageNum = Math.max(1, pageIndex - 2);//確保起始頁碼不會小於1，至少從第1頁開始
		int endPageNum = Math.min(startPageNum + 4, totalPage);
		//取起始頁碼加上4和總頁數中較小的值，以確保分頁按鈕的數量不超過5個，並且不會超出總頁數的範圍。
		startPageNum = Math.max(1, endPageNum - 4);
		//再次使用Math.max，將結束頁碼減去4後的值與1進行比較
		//取其中較大的值。這是為了確保結束頁碼與起始頁碼之間的差值為4
		//以便生成5個分頁按鈕。如果結束頁碼小於5，則將起始頁碼設為1，確保按鈕數量符合要求。


		List<Integer> pageButtonList = new ArrayList<>();//用於存儲生成的分頁按鈕的頁碼
		for (int i = startPageNum; i <= endPageNum; i++) {
		    pageButtonList.add(i);
		    }
		    
		    request.setAttribute("pageButtonList", pageButtonList);

		    return mapping.findForward("queryGoodsPageView");
		}

	
	public ActionForward querySalesReportView(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)	{

	return mapping.findForward("querySalesReportView");
}

	public ActionForward querySalesReport(ActionMapping mapping, ActionForm form, 
		    HttpServletRequest request, HttpServletResponse response)
		    throws Exception {
		    
		    String queryStartDate = request.getParameter("queryStartDate");
		    String queryEndDate = request.getParameter("queryEndDate");

		    List<SalesReport> salesReports = new ArrayList<>(backendService.querySalesReport(queryStartDate, queryEndDate));
           //創建了一個名為 salesReports 的新的 ArrayList，用於存儲銷售報表查詢的結果。
         //調用 backendService 物件的 querySalesReport 方法，
        //將 queryStartDate 和 queryEndDate 
      //作為參數傳遞進去。將結果添加到 salesReports 列表中。
		    
		    
		  

	
	
		    request.setAttribute("salesReports", salesReports);		  
		    
		    
		    return mapping.findForward("querySalesReport");
		}
	

//	public ActionForward deleteGoods(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//	    HttpSession session = request.getSession();	 
//	    String goodsIDParam = request.getParameter("goodsID");
//	    if (goodsIDParam == null || goodsIDParam.isEmpty()) {
//	        // 如果 goodsID 為 null 或空字符串，則返回到 "updateGoods" 頁面
//	        return mapping.findForward("deleteGoods");
//	    }
//
//	    Long goodsID = Long.parseLong(goodsIDParam);
//
//	    // 檢查指定ID的商品是否存在
//	    Goods goods = backendService.queryGoodsById(goodsID);
//
//	   
//
//         goods.setStatus("0"); // 將商品狀態修改為下架
//         //goods.setGoodsQuantity(0);
//	
//    int result = backendService.updateGoods(goods);
//
// 
//	
//	    return mapping.findForward("deleteGoods");
//
//	
//	}
}