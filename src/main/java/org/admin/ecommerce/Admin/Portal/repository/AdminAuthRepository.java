package org.admin.ecommerce.Admin.Portal.repository;

import org.admin.ecommerce.Admin.Portal.model.TblAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAuthRepository extends JpaRepository<TblAdmin, Long> {

    TblAdmin findByEmail(String email);
}
