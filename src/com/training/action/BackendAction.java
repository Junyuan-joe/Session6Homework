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

import com.training.vo.SalesReport;

import net.sf.json.JSONObject;

public class BackendAction extends DispatchAction {
	private BackendDao backendDao=BackendDao.getInstance();
	private BackendService backendService = BackendService.getInstance();
	
	public ActionForward queryGoods(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			
					throws Exception{
	
		
		List<Goods> goodsList = backendService.queryGoods();
				
		
		System.out.println("商品總數："+goodsList.size());
		
	
		
		goodsList.stream().forEach(g->System.out.println(g));
		
		request.setAttribute("goodsList", goodsList);
			
			return mapping.findForward("queryGoodsPageView");
		

		
		
	
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
		

		goodsbean.setGoodsID(Long.parseLong("0"));
		
		Goods go = new Goods();
		
		BeanUtils.copyProperties(go, goodsbean);
		
		//先處理圖片
		FormFile file = goodsbean.getGoodsImage();
		
		String fileName = file.getFileName();
		
		go.setGoodsImageName(fileName);
        FileOutputStream fileOutput = new FileOutputStream("C:/home/VendingMachine/DrinksImage/" 
        		+ fileName); 

        fileOutput.write(file.getFileData()); 
        
        fileOutput.flush(); 
        
        fileOutput.close(); 
        
        file.destroy() ;
        
        int goodsID = backendService.addGoods(go);
        String createMsg=null;
        
		if(goodsID > 0){ 
		
		 createMsg="商品新增上架成功！ 商品編號：" + goodsID;
		//System.out.println("商品新增上架成功！");
		}else{
			
		createMsg="商品新增上架失敗！";
		}
		session.setAttribute("createMsg",createMsg);
		
		return mapping.findForward("addGoods");
	}
	
	public ActionForward updateGoodsView(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		HttpSession session=request.getSession();
		
		List<Goods> goodsList = backendService.queryGoods();
		
        request.setAttribute("goodsList", goodsList);     
         
  String selectGoodsID=request.getParameter("goodsID");
   Object updateGoodsID=session.getAttribute("updateGoodsID");
  
   if (selectGoodsID != null && !selectGoodsID.isEmpty()) {
    	
    	Long goodsID = updateGoodsID != null ? (long) updateGoodsID : null;
    	goodsID =selectGoodsID !=null ? Long.parseLong(selectGoodsID) : goodsID;
    	Goods goods= backendService.queryGoodsById(goodsID);
	request.setAttribute("updateGoods", goods);

}
        	 	
	return mapping.findForward("updateGoodsView");
	}
	
//	public ActionForward getupdateGoodsView(ActionMapping mapping, ActionForm form, 
//			HttpServletRequest request, HttpServletResponse response)
//			throws Exception{
//		HttpSession session=request.getSession();
//	
//		 String selectGoodsID=request.getParameter("goodsID");
//		   Object updateGoodsID=session.getAttribute("updateGoodsID");
//		  
//		   if (selectGoodsID != null && !selectGoodsID.isEmpty()) {
//		    	
//		    	Long goodsID = updateGoodsID != null ? (long) updateGoodsID : null;
//		    	goodsID =selectGoodsID !=null ? Long.parseLong(selectGoodsID) : goodsID;
//		    	Goods goods= backendService.queryGoodsById(goodsID);
//			
//			
//           response.setCharacterEncoding("UTF-8");
//           response.setContentType("application/json");
//			PrintWriter out = response.getWriter();
//			out.print(JSONObject.fromObject(goods));
//			
//	
//		}
//		return null;
//		       
//	}

	public ActionForward updateGoods(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		
		HttpSession session=request.getSession();
		
		Goodsform goodsbean = (Goodsform) form;
			
	
		Goods go = new Goods();
		
		BeanUtils.copyProperties(go, goodsbean);
		
		
		int goodsID = backendService.updateGoods(go);
		
	     List<Goods> goodsList = backendService.queryGoods();
				
		
	 
	        String updateMsg="";
	        if(goodsID>0){
	   updateMsg="商品更新作業成功！ 商品編號："+goodsList.size();
			}
	        session.setAttribute("updateMsg",updateMsg);
	    	//session.setAttribute("updateGoodsID", go.getGoodsID());
	        
	    	return mapping.findForward("updateGoods");
		
	} 
	public ActionForward queryGoodsPage(ActionMapping mapping, ActionForm form, 
		    HttpServletRequest request, HttpServletResponse response) throws Exception {
		    
		    int pageSize = 10; // 每頁顯示 10 筆資料
		    int pageIndex = 1; // 預設為第 1 頁
		    String pageIndexStr = request.getParameter("pageIndex");

		    if (pageIndexStr != null && !pageIndexStr.isEmpty()) {
		        pageIndex = Integer.parseInt(pageIndexStr);
		    }

		    boolean isNextPage = Boolean.parseBoolean(request.getParameter("isNextPage"));
              //如果 isNextPage 为 true，那么就会进行下一步
		    if (isNextPage && pageIndex < (int) Math.ceil((double) backendService.queryGoods().size() / pageSize)) {
		        pageIndex++;
		    }

		    List<Goods> goodsList = backendService.queryGoods();
		    int totalPage = (int) Math.ceil((double) goodsList.size() / pageSize);
		    //Math.ceil 方法用于向上取整，因为如果有余数，那么就需要多一页来显示余下的商品
		    int startIndex = (pageIndex - 1) * pageSize;
		    int endIndex = Math.max(startIndex, Math.min(startIndex + pageSize, goodsList.size())); // 修正邊界檢查
		    List<Goods> pageGoodsList = goodsList.subList(startIndex, endIndex); // 修正邊界檢查

		    request.setAttribute("pageGoodsList", pageGoodsList);
		    request.setAttribute("totalPage", totalPage);
		    request.setAttribute("pageIndex", pageIndex);
		    request.setAttribute("pageSize", pageSize);
		    
		    // 動態生成分頁按鈕
		    int startPageNum = Math.max(1, pageIndex - 2);
		    int endPageNum = Math.min(startPageNum + 4, totalPage);
		    startPageNum = Math.max(1, endPageNum - 4);

		    List<Integer> pageButtonList = new ArrayList<>();
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

		    // 可選操作，將SalesReport對象打印到控制台以供調試
		    salesReports.stream().forEach(s -> System.out.println(s));

		    // 將"reports"變量作為請求屬性設置，而不是單個SalesReport對象
		    request.setAttribute("salesReports", salesReports);

		    return mapping.findForward("querySalesReport");
		}
}
//	public ActionForward deleteGoods(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//	    HttpSession session = request.getSession();
//
//	    String goodsIDParam = request.getParameter("goodsID");
//	    if (goodsIDParam == null || goodsIDParam.isEmpty()) {
//	        // If goodsID is null or an empty string, return to the "updateGoods" page
//	        return mapping.findForward("updateGoods");
//	    }
//
//	    Long goodsID = Long.parseLong(goodsIDParam);
//
//	    // Check if the goods with the specified ID exists
//	    Goods goods = backendService.queryGoodsById(goodsID);
//	    if (goods == null) {
//	        // If the goods do not exist, show an error message and return to the "updateGoods" page
//	        String deleteMsg = "商品不存在！";
//	        List<Goods> goodsList = backendService.queryGoods(deleteMsg);
//	        session.setAttribute("goodsList", goodsList);
//	        session.setAttribute("deleteMsg", deleteMsg);
//	        return mapping.findForward("updateGoods");
//	    }
//
//	    String status = goods.getStatus();
//	    String deleteMsg = null;
//	   
//	        int result = backendService.deleteGoods(goodsID);
//	        if (result >= 0) {
//	            deleteMsg = "商品已成功下架！";
//	        } else {
//	            deleteMsg = "商品下架失敗！";
//	        }
//	
//	    
//
//	    // Query for the updated goods list and show the delete message
//	    List<Goods> goodsList = backendService.queryGoods(deleteMsg);
//	    session.setAttribute("goodsList", goodsList);
//	    session.setAttribute("deleteMsg", deleteMsg);
//	    return mapping.findForward("updateGoods");
//	}
//		    	
//	}
//
