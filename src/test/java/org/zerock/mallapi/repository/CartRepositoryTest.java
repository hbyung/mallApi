package org.zerock.mallapi.repository;

import jakarta.persistence.PreUpdate;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Cart;
import org.zerock.mallapi.domain.CartItem;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.dto.CartItemListDTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
@Log4j2
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;


    //장바구니 추가 테스트
    @Transactional
    @Commit
    @Test
    public void testInsertByProduct() {

        String email = "user1@naver.com";
        Long pno = 2l;
        int qty = 3;

        //이메일 상품번호로 장바구니 아이탬 확인 없으면 추가 있으면 수량 저장 후 변경
        CartItem cartItem = cartItemRepository.getItemOfPno(email, pno);

        // 이미 사용자의 장바구니에 담겨있는 상품
        if (cartItem != null) {
            cartItem.changeQty(qty);
            cartItemRepository.save(cartItem);
            return;
        }

        // 장바구니 자체가 없을 수 도있음
        Optional<Cart> result = cartRepository.getCartOfMember(email);

        Cart cart = null;

        if (result.isEmpty()) {

            Member member = Member.builder().email(email).build();
            Cart tmepCart = Cart.builder().owner(member).build();

            cart = cartRepository.save(tmepCart);

        } else { //장바구니는 있으나 상품의 장바구니 아이템은 없는 경우

            cart = result.get();

        }

        if (cartItem == null) {

            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().cart(cart).product(product).qty(qty).build();

        }

        cartItemRepository.save(cartItem);

    }
    // 조회
    @Test
    public void testListOfMember() {

        String email = "user1@naver.com";
        List<CartItemListDTO> cartItemListDTOlist = cartItemRepository.getItemsOFCartDTOByEmail(email);

        for (CartItemListDTO cartItemListDTO : cartItemListDTOlist) {
            log.info(cartItemListDTO);
        }

    }

    // 수량 업데이트
    @Transactional
    @Commit
    @Test
    public void testUpdateByCino() {

        Long cino = 1l;
        int qty = 2;

        CartItem cartItem = cartItemRepository.findById(cino).orElseThrow();

        cartItem.changeQty(qty);

        cartItemRepository.save(cartItem);
    }

    @Test
    public void testDeleteThenList() {

        Long cino = 3l;

        Long cno = cartItemRepository.getCartFromItem(cino);

        cartItemRepository.deleteById(cino);

        List<CartItemListDTO> cartItemList = cartItemRepository.getItemsOfCartDTOByCart(cno);

        for (CartItemListDTO dto : cartItemList) {
            log.info(dto);
        }

    }


}
