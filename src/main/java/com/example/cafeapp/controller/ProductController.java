package com.example.cafeapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cafeapp.entity.Product;
import com.example.cafeapp.repository.ProductRepository;

@Controller
@RequestMapping("/admin/products")

public class ProductController {
	@Autowired
	private ProductRepository productRepository;

	@GetMapping
	public String listproducts(Model model) {
		List<Product> products = productRepository.findAll();
		model.addAttribute("products", products);
		return "admin/product_list";

	}

	@GetMapping("/new")
	public String showproductForm(Model model) {

		model.addAttribute("product", new Product());
		return "admin/product_form";
	}

	@PostMapping("/save")
	public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
		productRepository.save(product);
		redirectAttributes.addFlashAttribute("messege", "商品が正常に保存されました！");
		return "redirect:/admin/products";

	}

	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
		Optional<Product> productsOptional = productRepository.findById(id);
		if (productsOptional.isPresent()) {
			model.addAttribute("product", productsOptional.get());
			return "admin/product_form";
		} else {
			redirectAttributes.addFlashAttribute("errormessege", "商品が見つかりませんでした。");
			return "redirect:/admin/products";
		}
	}

	@GetMapping("/{id}/delete")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("message", "商品が正常に削除されました！");
		} else {
			redirectAttributes.addFlashAttribute("errorMessage", "商品が見つかりませんでした。");
		}
		return "redirect:/admin/products";

	}

}
