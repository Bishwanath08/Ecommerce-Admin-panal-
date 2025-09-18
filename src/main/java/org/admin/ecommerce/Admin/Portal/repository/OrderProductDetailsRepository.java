package org.admin.ecommerce.Admin.Portal.repository;

import org.admin.ecommerce.Admin.Portal.model.TblOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductDetailsRepository extends JpaRepository<TblOrderItem, Long> {

//   @Query("SELECT opd FROM OrderProductDetails opd WHERE opd.user.id = :userId")
//   List<TblOrderItem> showCustomerOrderProducts(@Param("userId") Long userId);

    List<TblOrderItem> findByOrderId(Long orderId);
}
