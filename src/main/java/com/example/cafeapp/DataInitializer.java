package com.example.cafeapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.cafeapp.entity.Product;
import com.example.cafeapp.repository.ProductRepository;

@Component

public class DataInitializer implements CommandLineRunner {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public void run(String... args) {
		if (productRepository.count() == 0) { // 商品が 1 つも登録されていなければ
			productRepository.save(new Product(null, "ブレンドコーヒー", 350.0, "香り高いブレンドコーヒー", "ドリンク"));
			productRepository.save(new Product(null, "カフェラテ", 400.0, "ミルクとエスプレッソのハーモニー", "ドリンク"));
			productRepository.save(new Product(null, "紅茶", 300.0, "香り豊かな紅茶", "ドリンク"));
			productRepository.save(new Product(null, "チーズケーキ", 450.0, "濃厚なチーズケーキ", "フード"));
			productRepository.save(new Product(null, "チョコレートマフィン", 380.0, "しっとりとしたチョコレートマフィン", "フード"));

			System.out.println("初期商品データを登録しました。");

		}

	}
}
