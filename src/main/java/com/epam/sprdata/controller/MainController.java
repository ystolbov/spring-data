package com.epam.sprdata.controller;

import com.epam.sprdata.domain.Cart;
import com.epam.sprdata.domain.Good;
import com.epam.sprdata.domain.User;
import com.epam.sprdata.service.CartService;
import com.epam.sprdata.service.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    private GoodService goodService;

    @Autowired
    private CartService cartService;

    private List<String> goodList = new ArrayList<>();

    @GetMapping("/")
    public String greeting(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        Iterable<Good> goods = goodService.findAllGood();
        if (filter != null && !filter.isEmpty()) {
            goods = goodService.findByTagContaining(filter);
        } else {
            goods = goodService.findAllGood();
        }
        model.addAttribute("goods", goods);
        model.addAttribute("filter", filter);
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        Iterable<Good> goods = goodService.findAllGood();
        if (filter != null && !filter.isEmpty()) {
            goods = goodService.findByTagContaining(filter);
        } else {
            goods = goodService.findAllGood();
        }
        model.addAttribute("goods", goods);
        model.addAttribute("filter", filter);
        return "main";
    }

    @GetMapping("/cart")
    public String bask(@RequestParam(required = false, defaultValue = "") String basket, Model model) {
        Iterable<Good> goods = goodService.findAllGood();
        model.addAttribute("goods", goods);
        model.addAttribute("basket", basket);
        goodList.add(basket);
        return "greeting";
    }

    @GetMapping("/youcart")
    public String yourcart(@AuthenticationPrincipal User user, Model model) {
        Iterable<Good> goods = goodService.findAllGood();
        Iterable<Cart> carts;
        Set<Good> selectedGoods = new HashSet<>();
        Set<Good> allSelectedGoods = new HashSet<>();

        for (String stringId : goodList) {
            //Good good = goodService.findById(Long.parseLong(stringId)).orElse(new Good());
            Good good = goodService.findById(Long.parseLong(stringId));
            selectedGoods.add(good);
        }
        cartService.save(new Cart(user, selectedGoods));
        carts = cartService.findByUserId(user.getId());

        for (Cart cart : carts) {
            allSelectedGoods.addAll(cart.getGoods());
        }
        model.addAttribute("goods", goods);
        model.addAttribute("goodList", goodList);
        model.addAttribute("selectedGoods", selectedGoods);
        model.addAttribute("allSelectedGoods", allSelectedGoods);
        return "youcart";
    }

    @GetMapping("/cartdel")
    public String baskDel(@RequestParam(required = false, defaultValue = "") String basketdel, Model model) {
        Iterable<Good> goods = goodService.findAllGood();
        model.addAttribute("goods", goods);
        model.addAttribute("basketdel", basketdel);
        goodList.remove(basketdel);
        return "redirect:/youcart";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user, @RequestParam String cat,
            @RequestParam String tag, @RequestParam String price, @RequestParam String img, Map<String, Object> model) {
        Good good = new Good(cat, tag, price, img);
        goodService.save(good);
        Iterable<Good> goods = goodService.findAllGood();
        model.put("goods", goods);
        return "main";
    }
}
