package site.easy.to.build.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCustomer(Customer customer);

    @Query(value = "SELECT SUM(b.amount) FROM Expense b WHERE b.customer = :customer")
    Double getTotalExpenseByCustomer(@Param("customer") Customer customer);
}
