package com.fajar.entitymanagement.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fajar.entitymanagement.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(nativeQuery = true, value = "select * from product where name like %?3% limit ?1 offset ?2")
	public List<Product> getByLimitAndOffset(int limit, int offset, String name);

	@Query(nativeQuery = true, value = "select sum(product_flow.count) as productCount  from product   "
			+ "left join product_flow on product.id = product_flow.product_id "
			+ "left join `transaction` on transaction.id = product_flow.transaction_id  where transaction.`type` = 'OUT' and  "
			+ "month(transaction.transaction_date) = ?1 and  year(transaction.transaction_date) = ?2"
			+ " and product.id = ?3")
	public Object findProductSales(int month, int year, Long productId);

	/**
	 * String sql = "select sum(product_flow.count) as productCount from
	 * product_flow " + " left join `transaction` on transaction.id =
	 * product_flow.transaction_id " + " where transaction.`type` = 'OUT' and
	 * product_flow.product_id = " + productId + " and transaction.transaction_date
	 * >= '" + period1 + "' and " + " transaction.transaction_date <= '" + period2 +
	 * "' ";
	 * 
	 */
	@Query(nativeQuery = true, value = "select sum(product_flow.count) as productCount from product_flow  "
			+ " left join `transaction` on transaction.id = product_flow.transaction_id "
			+ " where transaction.`type` = 'OUT' and product_flow.product_id = ?3"
			+ " and transaction.transaction_date >= ?1 and " + " transaction.transaction_date <= ?2 ")
	public BigDecimal findProductSalesBetween(String period1, String period2, Long productId);

}