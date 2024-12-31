package org.zerock.mallapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Cart;
import org.zerock.mallapi.domain.CartItem;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.CartItemDTO;
import org.zerock.mallapi.dto.CartItemListDTO;
import org.zerock.mallapi.repository.CartItemRepository;
import org.zerock.mallapi.repository.CartRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO) {

        String email = cartItemDTO.getEmail();
        Long pno = cartItemDTO.getPno();
        int qty = cartItemDTO.getQty();
        Long cino = cartItemDTO.getCino();

        //기존에 담겨있는 상품에 경우
        if (cino != null) {

            Optional<CartItem> cartItemResult = cartItemRepository.findById(cino);

            CartItem cartItem = cartItemResult.orElseThrow();

            cartItem.changeQty(qty);

            cartItemRepository.save(cartItem);

            return getCartItems(email);
        }

        Cart cart = getCart(email);

        CartItem cartItem = null;

        cartItem  = cartItemRepository.getItemOfPno(email, pno);

        if (cartItem == null) {

            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();

        } else {
            cartItem.changeQty(qty);
        }

        cartItemRepository.save(cartItem);

        return getCartItems(email);
    }

    private Cart getCart(String email) {

        //해당 email의 장바구니(Cart)가 있는지 확인 있으면 반환

        //없으면 Cart 객체 생성하고 추가 반환

        Cart cart = null;

        Optional<Cart> result = cartRepository.getCartOfMember(email);

        if (result.isEmpty()) {

            log.info("Cart of the member is not exist!!");

            Member member = Member.builder().email(email).build();

            Cart tomCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tomCart);

        } else {
            cart = result.get();
        }

        return cart;


    }

    @Override
    public List<CartItemListDTO> getCartItems(String email) {

        return cartItemRepository.getItemsOFCartDTOByEmail(email);
    }

    @Override
    public List<CartItemListDTO> remove(Long cino) {

        //cart id 조회
        Long cno = cartItemRepository.getCartFromItem(cino);

        //cart 삭제
        cartItemRepository.deleteById(cino);

        // cart 번호 가지고 조회
        return cartItemRepository.getItemsOfCartDTOByCart(cno);
    }
}
