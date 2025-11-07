package com.example.cafeapp.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cafeapp.entity.Customer;
import com.example.cafeapp.entity.Order;
import com.example.cafeapp.entity.OrderItem;
import com.example.cafeapp.entity.Product;
import com.example.cafeapp.repository.CustomerRepository;
import com.example.cafeapp.repository.OrderRepository;
import com.example.cafeapp.repository.ProductRepository;

@Controller
public class HomeController {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private OrderRepository orderRepository;

	private Map<Long, Integer> cart = new HashMap<>();

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("drinks", productRepository.findByCategory("ドリンク"));
		model.addAttribute("foods", productRepository.findByCategory("フード"));
		model.addAttribute("cartSize", cart.size());
		return "index";

	}

	@PostMapping("/add-to-cart")
	public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
			RedirectAttributes redirectAttributes) {
		cart.put(productId, cart.getOrDefault(productId, 0) + quantity);
		redirectAttributes.addFlashAttribute("messege", "商品がカートに追加されました！");
		return "redirect:/";

	}

	@GetMapping("/cart")
	public String viewCart(Model model) {
		List<OrderItem> cartItems = new ArrayList<>();
		double total = 0.0;
		for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
			Optional<Product> productOptional = productRepository.findById(entry.getKey());
			if (productOptional.isPresent()) {
				Product product = productOptional.get();
				OrderItem item = new OrderItem();
				item.setProduct(product);
				item.setQuantity(entry.getValue());
				item.setPriceAtOrder(product.getPrice());
				cartItems.add(item);
				total += product.getPrice() * entry.getValue();
			}
		}

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalAmount", total);
		model.addAttribute("customer", new Customer());
		return "cart";
	}

	@PostMapping("/cart/remove")
	public String removeFromCart(@RequestParam Long productId) {
		cart.remove(productId);
		return "redirect:/cart";
	}

	@PostMapping("/place-order")
	public String placeOrder(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {
		if (cart.isEmpty()) {
			redirectAttributes.addFlashAttribute("errormessege", "カートが空です。");
			return "redirect:/cart";
		}

		Customer existingCustomer = customerRepository.findByEmail(customer.getEmail());
		if (existingCustomer != null) {
			customer = existingCustomer;

		} else {
			customerRepository.save(customer);
		}

		Order order = new Order();
		order.setCustomer(customer);
		order.setOrderDate(LocalDateTime.now());
		order.setStatus("受付済");

		List<OrderItem> orderItems = new ArrayList<>();
		double totalAmount = 0.0;

		for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
			Product product = productRepository.findById(entry.getKey()).orElseThrow();

			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(product);
			orderItem.setQuantity(entry.getValue());
			orderItem.setPriceAtOrder(product.getPrice());
			orderItem.setOrder(order);
			orderItems.add(orderItem);
			totalAmount += product.getPrice() * entry.getValue();
		}
		order.setOrderItems(orderItems);
		order.setTotalAmount(totalAmount);

		orderRepository.save(order);

		cart.clear();

		redirectAttributes.addFlashAttribute("message", "ご注文ありがとうございます！注文 ID: " +
				order.getId());

		return "redirect:/order-success";

	}

	@GetMapping("/order-success")
	public String ordersuccess() {
		return "order_success";
	}

}
