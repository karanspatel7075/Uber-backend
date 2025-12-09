package com.example.Navio.Payment;

import com.example.Navio.model.Ride;
import com.example.Navio.repository.RideRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String stripeSecretKey;

    @Autowired
    private RideRepository rideRepository;

    // Store Stripe sessionId → rideId mapping
    private final ConcurrentHashMap<String, Long> sessionRideMap = new ConcurrentHashMap<>();

    public StripeResponse checkoutFare(Double farePrice, Long rideId) {
        Stripe.apiKey = stripeSecretKey;

            long amountInPaise = (long) (farePrice * 100);
            Map<String, Object> priceData = new HashMap<>();
            priceData.put("currency", "inr");

            Map<String, Object> productData = new HashMap<>();
            productData.put("name", "Ride Fare Payment");
            priceData.put("product_data", productData);
            priceData.put("unit_amount", amountInPaise);

            Map<String, Object> lineItem = new HashMap<>();
            lineItem.put("quantity", 1);
            lineItem.put("price_data", priceData);

            List<Object> lineItems = new ArrayList<>();
            lineItems.add(lineItem);

            Map<String, Object> params = new HashMap<>();
            params.put("mode", "payment");
            params.put("line_items", lineItems);
            params.put("success_url", "http://localhost:8080/ride/success?session_id={CHECKOUT_SESSION_ID}");
            params.put("cancel_url", "http://localhost:8080/ride/paymentFailed");

        try {
            Session session = Session.create(params);

            // Save mapping sessionId → rideId
            saveSession(session.getId(), rideId);

            return  StripeResponse.builder()
                    .status("success")
                    .message("Stripe session created successfully")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (Exception e) {
            return  StripeResponse.builder()
                    .status("error")
                    .message("Failed to create the Stripe session: " + e.getMessage())
                    .build();
        }
    }

    public void saveSession(String sessionId, Long rideId) {
        sessionRideMap.put(sessionId, rideId);
    }

    public Ride getRideFromSession(String sessionId) {
        Long rideId = sessionRideMap.get(sessionId);
        if (rideId == null) {
            return null;
        }

        return rideRepository.findById(rideId).orElse(null);
    }

}
