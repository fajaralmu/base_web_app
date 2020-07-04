//package com.fajar.entitymanagement.service;
//
//import static com.fajar.entitymanagement.util.CollectionUtil.arrayToList;
//import static com.fajar.entitymanagement.util.CollectionUtil.convertList;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.function.Supplier;
//
//import javax.annotation.PostConstruct;
//import javax.persistence.Query;
//
//import org.hibernate.Transaction;
//import org.omg.IOP.TransactionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.fajar.entitymanagement.dto.Filter;
//import com.fajar.entitymanagement.dto.WebRequest;
//import com.fajar.entitymanagement.dto.WebResponse;
//import com.fajar.entitymanagement.entity.Product;
//import com.fajar.entitymanagement.repository.ProductRepository;
//import com.fajar.entitymanagement.repository.RepositoryCustomImpl;
//import com.fajar.entitymanagement.util.CollectionUtil;
//import com.fajar.entitymanagement.util.DateUtil; 
//
//@Service
//public class ProductService {
//
//	private static final String OPTION_WITH_STOCK = "withStock";
// 
//	private static final String OPTION_WITH_NEW_INFO = "withNewInfo";
//
//	private static final String OPTION_WITH_SUPPLIER = "withSupplier";
//
//	private static final String FIELD_PRODUCT_ID = "productId";
//	
//	
//	@Autowired
//	private EntityService entityService; 
//	@Autowired
//	private ProductRepository productRepository; 
//	@Autowired
//	private ProgressService progressService; 
//	@Autowired
//	private RepositoryCustomImpl repositoryCustomImpl;
//
//	@PostConstruct
//	public void init() {
//		LogProxyFactory.setLoggers(this);
//	}
//
//	/**
//	 * get list product for catalog page
//	 * @param request
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getProductsCatalog(WebRequest request, String requestId) { 
//		
//		Map<String, Object> filter = request.getFilter().getFieldsFilter();
//		
//		boolean withStock = (filter.get(OPTION_WITH_STOCK) != null && (Boolean.valueOf(filter.get(OPTION_WITH_STOCK).toString())) .equals(true));
//		boolean withSupplier = (filter.get(OPTION_WITH_SUPPLIER) != null && (Boolean.valueOf(filter.get(OPTION_WITH_SUPPLIER).toString())).equals(true));
//		boolean withNewInfo = (filter.get(OPTION_WITH_NEW_INFO) != null && (Boolean.valueOf(filter.get(OPTION_WITH_NEW_INFO).toString())).equals(true));
//
//		request.getFilter().getFieldsFilter().remove(OPTION_WITH_STOCK);
//		
//		WebResponse filteredProducts = entityService.filter(request);
//		
//		progressService.sendProgress(1, 1, 20.0, true, requestId);
//		
//		if (filteredProducts == null || filteredProducts.getEntities() == null || filteredProducts.getEntities().size() == 0) {
//			return new WebResponse("01", "Data Not Found"); 
//		}
//		
//		//get from db
//		List<Product> products = convertList(filteredProducts.getEntities()); 
//		
//		for (Product product : products) {    
//			
//			if (withNewInfo) { 
//				product.setNewProduct(isNewProduct(product.getId())); 
//			}
//			if (withStock) {
//				int remaining = productInventoryService.getProductInventory(product);
//				product.setCount(remaining);
//			}
//			if(withSupplier) {
//				List<Supplier> suppliers = transactionService.getProductSupplier(product.getId(), 5, 0);
//				product.setSuppliers(suppliers);
//			}
//			progressService.sendProgress(1, products.size(), 80, false, requestId);  
//		}
//		
////		if (withStock) {
////			products = transactionService.populateProductWithStocks(products, true, requestId);
////		} 
//
//		progressService.sendComplete(requestId);
//		
//		filteredProducts.setFilter(request.getFilter());
//		filteredProducts.setEntities(convertList(products));
//		return filteredProducts;
//	}
//
//	/**
//	 * check if product is new in the shop
//	 * @param id
//	 * @return
//	 */
//	private boolean isNewProduct(Long id) {
//		 
//			Transaction firstTransactionIn = transactionService.getFirstTransaction(id); 
//			boolean firstTransactionExists = null != firstTransactionIn && null != firstTransactionIn.getTransactionDate();
//			
//			if (firstTransactionExists ) { 
//				long diffDays = getDiffDays(firstTransactionIn.getTransactionDate()); 
//				if (diffDays <= 14) {
//					return true;
//				}
//			}
//			
//			return false;
//	}
//
//	/**
//	 * get difference days from now
//	 * @param date
//	 * @return
//	 */
//	private long getDiffDays(Date date) {
//		long diff = new Date().getTime() - date.getTime();
//		long diffDays = diff / (24 * 60 * 60 * 1000);
//		return diffDays;
//	}
//
//	/**
//	 * get product sales in month & year
//	 * @param month
//	 * @param year
//	 * @param productId
//	 * @return
//	 */
//	public ProductSales getProductSalesAt(int month, int year, Long productId) {
//
//		try {
//			Object result = productRepository.findProductSales(month, year, productId);// .getSingleResult(sql);
//			int count = Integer.parseInt(result.toString());
//			ProductSales sales = new ProductSales();
//			sales.setSales(count);
//			sales.setMonth(month);
//			sales.setYear(year);
//			return sales;
//			
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return new ProductSales();
//		}
//	}
//
//	/**
//	 * get product sales count between period
//	 * @param periodFrom
//	 * @param periodTo
//	 * @param productId
//	 * @return
//	 */
//	public int getProductSalesBetween(String periodFrom, String periodTo, Long productId) {
//		
//		try {
//			Object count = productRepository.findProductSalesBetween(periodFrom, periodTo, productId);// .getSingleResult(sql);
//			return Integer.parseInt(count.toString());
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return 0;
//		}
//	}
//
//	
//	/**
//	 * get product sales in period range
//	 * @param request
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getProductSales(WebRequest request, String requestId) { 
//		
//		WebResponse response = new WebResponse();
//		Filter filter = request.getFilter();
//		String periodFrom = DateUtil.getFullFirstDate(filter.getMonth(), filter.getYear());
//		String periodTo = DateUtil.getFullFirstDate(filter.getMonthTo(), filter.getYearTo());
//
//		String productName = request.getProduct() == null || request.getProduct().getName() == null ? ""
//				: request.getProduct().getName();
//		
//		List<Product> products = productRepository.getByLimitAndOffset(filter.getLimit(),
//				filter.getLimit() * filter.getPage(), productName);
//
//		List<ProductSales> productSalesList = new ArrayList<>();
//		
//		/**
//		 * populate product sales
//		 */
//		for (Product product : products) { 
//			
//			ProductSales productSales = getProductSales(product, periodFrom, periodTo);
//			productSalesList.add(productSales );
//			progressService.sendProgress(1, products.size(), 100, false, requestId);
//		}
//		 
//		response.setEntities(convertList(productSalesList));
//		response.setFilter(request.getFilter());
//		return response;
//	}
//
//	/**
//	 * get product sales for specified product & period
//	 * @param product
//	 * @param periodFrom yyyy-MM-DD
//	 * @param periodTo yyyy-MM-DD
//	 * @return
//	 */
//	public ProductSales getProductSales(Product product, String periodFrom, String periodTo) {
//		int sales = getProductSalesBetween(periodFrom, periodTo, product.getId()); 
//		
//		ProductSales productSales = new ProductSales();
//		productSales.setProduct(product);  
//		productSales.setSales(sales);
//		
//		return productSales;
//	}
//
//	/**
//	 * get suppliers for specified product
//	 * @param request
//	 * @return
//	 */
//	public WebResponse getMoreProductSupplier(WebRequest request) {
//		
//		try {
//			WebResponse response 	= new WebResponse();
//			Filter filter 				= request.getFilter();
//			Integer productId 			= (Integer) filter.getFieldsFilter().get(FIELD_PRODUCT_ID); 
//			List<Supplier> suppliers 	= getProductSupplier(productId.longValue(),   filter.getPage()); 
//			 
//			response.setEntities(CollectionUtil.convertList(suppliers));
//			return response;
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return WebResponse.failed(e.getMessage());
//		}
//	}
//
//	private List<Supplier> getProductSupplier(long longValue, Integer page) { 
//		return transactionService.getProductSupplier(longValue, 5, 5 * page); 
//	}
//
//	/**
//	 * get product sales data in range of period
//	 * @param request
//	 * @param productId
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getProductSalesDetail(WebRequest request, Long productId, String requestId) { 
//
//		Optional<Product> productOpt = productRepository.findById(productId);
//		Product product = null;
//
//		if (productOpt.isPresent()) {
//			product = productOpt.get();
//
//		} else {
//			return WebResponse.failedResponse();
//		}
//		Filter filter = request.getFilter();
//		int monthFromReq = filter.getMonth();
//		int yearFrom = filter.getYear();
//		int monthToReq = filter.getMonthTo();
//		int yearTo = filter.getYearTo();
//
//		Integer maxValue = 0;
//		Integer totalPeriod = 0;
//		Integer runningPeriod = 0;
//
//		/**
//		 * calculate total months
//		 */
//		totalPeriod = getTotalPeriod(monthFromReq, monthToReq, yearFrom, yearTo);
//
//		/**
//		 * get sales data for each period
//		 */
//		List<ProductSales> salesList = new ArrayList<>();
//		for (int runningYear = yearFrom; runningYear <= yearTo; runningYear++) {
//			
//			int beginningMonth = runningYear == yearFrom ? monthFromReq : 1;
//			int endOfMonth = runningYear == yearTo ? monthToReq : 12;
//			
//			for (int runningMonth = beginningMonth; runningMonth <= endOfMonth; runningMonth++) {
//				
//				runningPeriod++;
//				ProductSales productSales = getProductSalesAt(runningMonth, runningYear, product.getId()); 
//				
//				salesList.add(productSales);
//				
//				/**
//				 * update maxValue
//				 */
//				if (productSales.getSales() > maxValue) {
//					maxValue = productSales.getSales();
//				}
//
//				progressService.sendProgress(1, totalPeriod, 100, false, requestId);
//			}
//		}
//		/**
//		 * set sales proportion for each product sales
//		 */
//		for (ProductSales sales : salesList) {
//			
//			double ratio = (Double.parseDouble(String.valueOf(sales.getSales())) / Double.parseDouble(maxValue.toString())); 
//			double percentage = ratio * 100;
//			
//			sales.setPercentage(percentage);
//		}
//
//		WebResponse response = new WebResponse();
//		response.setEntity(product);
//		response.setMaxValue(maxValue.longValue());
//		response.setEntities(convertList(salesList));
//		
//		progressService.sendComplete(requestId);
//		return response;
//	}
//
//	/**
//	 * get total month between period
//	 * @param monthFromReq
//	 * @param monthToReq
//	 * @param yearFrom
//	 * @param yearTo
//	 * @return
//	 */
//	private Integer getTotalPeriod(int monthFromReq, int monthToReq, int yearFrom, int yearTo) {
//		
//		 int totalPeriod = 0;
//		 for (int runningYear = yearFrom; runningYear <= yearTo; runningYear++) {
//			
//			int monthFrom = (runningYear == yearFrom) ? monthFromReq : 1;
//			int monthTo = (runningYear == yearTo) ? monthToReq : 12;
//			
//			for (int runningMonth = monthFrom; runningMonth <= monthTo; runningMonth++) {
//				totalPeriod++;
//			}
//
//		};
//		
//		return totalPeriod;
//	}
//
//	/**
//	 * get random images name for display purpose
//	 * @param imagebasePath
//	 * @return
//	 */
//	public List<String> getRandomProductImages(String imagebasePath) {
// 
//		Query query = repositoryCustomImpl.createNativeQuery( "select product.image_url from product where product.image_url is not null limit 7");
//		List<?> result = query.getResultList();
//		
//		if (result == null || result.size() == 0) {
//			return new ArrayList<>();
//		}
//		
//		List<String> rawImageNames = convertList(result);
//		List<String> finalImageNames = new ArrayList<>();
//
//		for (String string : rawImageNames) {
//			String[] imageUrls = string.split("~");
//			
//			for (int i = 0; i < imageUrls.length; i++) {
//				imageUrls[i] = imagebasePath + imageUrls[i];
//
//			}
//			finalImageNames.addAll(arrayToList(imageUrls));
//		}
//		return finalImageNames;
//	}
//
//	/**
//	 * get entities available for public
//	 * @param request
//	 * @param requestId
//	 * @return
//	 */
//	public WebResponse getPublicEntities(WebRequest request, String requestId) {
//
//		if (request.getEntity().equals("product")) {
//			return getProductsCatalog(request, requestId);
//			
//		} else if (request.getEntity().equals("supplier")) {
//			return entityService.filter(request);
//		}
//		return WebResponse.failed("invalid option");
//	}
//}
