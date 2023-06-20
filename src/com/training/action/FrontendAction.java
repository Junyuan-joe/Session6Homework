package com.training.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.training.dao.FrontEndDao;
import com.training.model.Goods;
import com.training.model.Member;
import com.training.service.BackendService;
import com.training.service.FrontendService;
import com.training.vo.BuyGoods;

public class FrontendAction extends DispatchAction {

	FrontendService frontendService = FrontendService.getInstance();

	private BackendService backendService = BackendService.getInstance();

	public ActionForward searchGoodsView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return mapping.findForward("searchGoodsView");
	}

	public ActionForward searchGoods(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String searchKeyword = request.getParameter("searchKeyword");
		// 请求参数中讀取搜尋字
		request.setAttribute("searchKeyword", searchKeyword);

		// 设置每页的商品数量（页面大小）並确定當前页索引
		int pageSize = 6;// 每頁6筆
		int pageIndex = 1;// 起始頁
		String pageIndexStr = request.getParameter("pageIndex");

		if (pageIndexStr != null && !pageIndexStr.isEmpty()) {
			pageIndex = Integer.parseInt(pageIndexStr);
		}

		// 计算總商品数和總页数
		int totalGoods = frontendService.countSearchGoods(searchKeyword);
		// 返回大於或等於給定數字的最小整數值
		int totalPage = (int) Math.ceil((double) totalGoods / pageSize);

		// 计算當前页的起始行號和结束行號
		int startRowNo = (pageIndex - 1) * pageSize + 1;//指定页面开始的记录数。

                       //最后，加1是为了将起始行號從0-based索引转换为1-based索引
		
		int endRowNo = Math.min(startRowNo + pageSize - 1, totalGoods);//指定页面开始的记录的结束行号，确保不超过总记录数或商品总数

		// 商品搜索，讀取商品集合
		Set<Goods> goods = frontendService.searchGoods(searchKeyword, startRowNo, endRowNo);

		request.setAttribute("frontgoods", goods);
		request.setAttribute("totalPage", totalPage);
		request.setAttribute("pageIndex", pageIndex);
		request.setAttribute("pageSize", pageSize);

		// 計算開始頁碼和結束頁碼，並將它們以及頁碼列表設置為請求物件的屬性
		int startPageNum = Math.max(1, pageIndex - 2); // 修改起始頁码的计算逻辑
		int endPageNum = Math.min(startPageNum + 2, totalPage); // 修改结束页码的计算逻辑

		// for循環從起始頁碼到結束頁碼（包括）遍歷，並將每個頁碼添加到pageList中。
		List<Integer> pageList = new ArrayList<>();
		for (int i = startPageNum; i <= endPageNum; i++) {
			pageList.add(i);
		}

		request.setAttribute("pageList", pageList);

		return mapping.findForward("searchGoods");
	}

	public ActionForward buyGoodsView(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		session.removeAttribute("cartGoods");

		return mapping.findForward("buyGoodsView");
	}

	public ActionForward buyGoods(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();

		Object cartGoodsObj = session.getAttribute("cartGoods");

		// 檢查cartGoodsObj是否是Map類型的物件，並將其轉換為Map<Goods, Integer>類型的變量cartGoods。
		if (cartGoodsObj instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<Goods, Integer> cartGoods = (Map<Goods, Integer>) cartGoodsObj;
			// 告訴編譯器忽略轉換cartGoodsObj到Map<Goods, Integer>類型時的未檢查警告
			// 如果cartGoodsObj不是Map類型的物件，那麼轉換操作可能會引發ClassCastException異常
			@SuppressWarnings("unchecked")
			Map<Goods, Integer> cartGood = (Map<Goods, Integer>) cartGoodsObj;

			List<String> goodsIDList = new ArrayList<>();

			List<String> quantityList = new ArrayList<>();
			
			for (Map.Entry<Goods, Integer> entry : cartGoods.entrySet()) {
				// 這個for循環用於遍歷cartGoods的鍵值對（Entry）
				// ，將每個鍵值對的鍵（商品）和值（數量）分別存儲在entry變量中
				Goods goods = entry.getKey();// 鍵值
				int quantity = entry.getValue();
				// 將goods的goodsID轉換為字符串並添加到goodsIDList中，
				// 同樣，將quantity轉換為字符串並添加到quantityList中。
				goodsIDList.add(String.valueOf(goods.getGoodsID()));
				quantityList.add(Integer.toString(quantity));
			}
			// 轉換陣列 所有元素（以相同的順序）
			String[] goodsIDs = goodsIDList.toArray(new String[0]);

			String[] Quantity = quantityList.toArray(new String[0]);

			Member member = (Member) session.getAttribute("identificationNo");

			String customerID = member.getIdentificationNo();//帳號

			String inputMoneyParam = request.getParameter("inputMoney");
			// 如果參數值為null，則將inputMoney設置為0
			int inputMoney = (inputMoneyParam != null) ? Integer.parseInt(inputMoneyParam) : 0;

			BuyGoods buyGoods = frontendService.buyGoods(customerID, goodsIDs, Quantity, inputMoney);

			session.setAttribute("inputMoney", buyGoods.getInputMoney());// 投入金額

			session.setAttribute("buyMoney", buyGoods.getBuyMoney());// 購買金額

			session.setAttribute("changeMoney", buyGoods.getChangeMoney());// 找零金額

			session.setAttribute("cartGood", cartGood); // 顯示結帳商品明細

			session.setAttribute("cartGoods", cartGoods); // 顯示商品明細

			session.removeAttribute("cartGoods");

			session.removeAttribute("cartCount");//加入商品數

		}
		return mapping.findForward("buyGoods");
	}

}
