package com.jforce.order.web;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.api.dto.ProductRequest;
import com.jforce.order.api.dto.ProductUpdateRequest;
import com.jforce.order.user.cart.service.CartService;
import com.jforce.order.user.order.service.OrderService;
import com.jforce.order.user.product.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class StoreController {

    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    public StoreController(ProductService productService,
                           CartService cartService,
                           OrderService orderService) {

        this.productService = productService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/products")
    public String products(HttpSession session, Model model) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        try    {
            model.addAttribute("products", productService.listEnabled());
        } catch (Exception exception) {
            logger.error("Error fetching products: {}", exception.getMessage());
            model.addAttribute("error", exception.getMessage());
        }

        return "products";
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("cart", cartService.getCart(user.getUsername()));

        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(HttpSession session, @RequestParam Long productId,
                            @RequestParam int quantity, RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        if (quantity < 1) {
            redirect.addFlashAttribute("error", "Quantity must be at least one.");
            return "redirect:/products";
        }

        try {
            cartService.add(user.getUsername(), productId, quantity);
            redirect.addFlashAttribute("message", "Product added to your cart.");
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/products";
    }

    @PostMapping("/cart/update")
    public String updateCart(HttpSession session, @RequestParam Long productId,
                             @RequestParam int quantity, RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        if (quantity < 1) {
            redirect.addFlashAttribute("error", "Quantity must be at least one.");
            return "redirect:/cart";
        }

        try {
            cartService.update(user.getUsername(), productId, quantity);
            redirect.addFlashAttribute("message", "Cart updated.");
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(HttpSession session, @RequestParam Long productId,
                                 RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        try {
            cartService.remove(user.getUsername(), productId);
            redirect.addFlashAttribute("message", "Product removed from your cart.");
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/orders/place")
    public String placeOrder(HttpSession session, RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        try {
            orderService.placeOrder(user.getUsername());

            redirect.addFlashAttribute("message", "Your order was placed successfully.");

            return "redirect:/orders";
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {

        JForceUser user = currentUser(session);

        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("orders", orderService.listOrders(user.getUsername()));

        return "orders";
    }

    @GetMapping("/admin/products")
    public String adminProducts(HttpSession session, Model model) {

        JForceUser user = currentUser(session);

        if (!isAdmin(user)) return user == null ? "redirect:/login" : "redirect:/products";

        model.addAttribute("user", user);
        try {
            model.addAttribute("products", productService.listAll());
        } catch (Exception exception) {
            model.addAttribute("error", exception.getMessage());
        }

        return "admin-products";
    }

    @PostMapping("/admin/products")
    public String createProduct(HttpSession session,
                                @RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam BigDecimal price,
                                @RequestParam int quantity,
                                RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (!isAdmin(user)) return user == null ? "redirect:/login" : "redirect:/products";

        if (name.isBlank() || price.signum() < 0 || quantity < 0) {

            redirect.addFlashAttribute("error", "Enter a product name, non-negative price, and quantity.");
            return "redirect:/admin/products";
        }

        try {

            productService.create(new ProductRequest(name, description, price, quantity));
            redirect.addFlashAttribute("message", "Product added.");
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/update")
    public String updateProduct(HttpSession session,
                                @PathVariable Long id,
                                @RequestParam BigDecimal price,
                                @RequestParam int quantity,
                                RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (!isAdmin(user)) return user == null ? "redirect:/login" : "redirect:/products";

        if (price.signum() < 0 || quantity < 0) {
            redirect.addFlashAttribute("error", "Price and quantity must be non-negative.");
            return "redirect:/admin/products";
        }

        try {
            productService.update(id, new ProductUpdateRequest(price, quantity));
            redirect.addFlashAttribute("message", "Product updated.");
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/enabled")
    public String setEnabled(HttpSession session, @PathVariable Long id,
                             @RequestParam boolean enabled, RedirectAttributes redirect) {

        JForceUser user = currentUser(session);

        if (!isAdmin(user)) return user == null ? "redirect:/login" : "redirect:/products";

        try {
            productService.setEnabled(id, enabled);
            redirect.addFlashAttribute("message", "Product availability updated.");
        } catch (Exception exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/admin/products";
    }

    private static JForceUser currentUser(HttpSession session) {
        Object value = session.getAttribute("user");
        return value instanceof JForceUser user ? user : null;
    }

    private static boolean isAdmin(JForceUser user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
}
