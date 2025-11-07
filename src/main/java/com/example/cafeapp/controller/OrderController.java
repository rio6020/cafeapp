package com.example.cafeapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cafeapp.entity.Order;
import com.example.cafeapp.repository.OrderRepository;

@Controller
@RequestMapping("/admin/orders")

public class OrderController {

	@Autowired
	private OrderRepository orderRepository;

	@GetMapping
	public String listOrders(Model model) {
		List<Order> orders = orderRepository.findAll();
		model.addAttribute("orders", orders);
		return "admin/order_list";
	}

	@GetMapping("/{id}")
	public String showOrderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Order> order = orderRepository.findById(id);

		if (order.isPresent()) {
			model.addAttribute("order", order.get());
			return "admin/order_detail";
		} else {
			redirectAttributes.addFlashAttribute("errormessege", "注文が見つかりませんでした。");
			return "redirect:/admin/orders";
		}

	}

	@PostMapping("/{id}/updateStatus")
	public String updateOrderStatus(@PathVariable Long id, @RequestParam String status,
			RedirectAttributes redirectAttributes) {
		Optional<Order> orderOptional = orderRepository.findById(id);
		if (orderOptional.isPresent()) {
			Order order = orderOptional.get();
			order.setStatus(status);
			orderRepository.save(order);
			redirectAttributes.addFlashAttribute("messege", "注文が更新されました！");
		} else {
			redirectAttributes.addFlashAttribute("errormessege", "注文が見つかりませんでした。");
		}
		return "redirect/admin/orders/" + id;
	}

	@GetMapping("/{id}/delete")
	public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (orderRepository.existsById(id)) {
			orderRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("message", "注文が正常に削除されました！");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "注文が見つかりませんでした。");
		}
		return "redirect:/admin/orders";
	}

}
