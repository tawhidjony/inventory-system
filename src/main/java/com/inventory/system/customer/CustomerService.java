package com.inventory.system.customer;

import com.inventory.system.common.exception.ResourceNotFoundException;
import com.inventory.system.customer.dto.CustomerRequest;
import com.inventory.system.customer.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        applyRequest(customer, request);
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAllByDeletedFalse();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerRequest request) {
        Customer customer = getCustomerById(id);
        applyRequest(customer, request);
        return customerRepository.save(customer);
    }

    // সফট ডিলিট: পুরনো সেল/ইনভয়েসে কাস্টমারের রেফারেন্স থেকে যায়
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setDeleted(true);
        customerRepository.save(customer);
    }

    private void applyRequest(Customer customer, CustomerRequest request) {
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
    }
}
