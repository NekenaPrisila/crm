package site.easy.to.build.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByCustomer(Customer customer);

    @Query(value = "SELECT SUM(b.totalAmount) FROM Budget b WHERE b.customer = :customer")
    Double getTotalBudgetByCustomer(@Param("customer") Customer customer);
}
