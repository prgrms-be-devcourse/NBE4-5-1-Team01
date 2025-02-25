package com.team1.beanstore.global.init;

import com.team1.beanstore.domain.order.repository.OrderRepository;
import com.team1.beanstore.domain.order.service.OrderService;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final ProductRepository productRepository;

    @Autowired
    @Lazy
    private BaseInitData self;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Bean
    @Order(1)
    public ApplicationRunner applicationRunner1() {
        return args -> {
            self.productInit();
        };
    }

    @Bean
    @Order(2)
    public ApplicationRunner applicationRunner2() {
        return args -> {
            self.orderInit();
        };
    }

    @Transactional
    public void productInit() {
        if (productRepository.count() > 0) {
            return;
        }

        //HAND_DRIP

        productRepository.saveAndFlush(Product.builder()
                .name("에티오피아 블루마운틴 리저브")
                .description("견과류와 초콜릿 향이 어우러진 풍부한 향미가 특징입니다.")
                .price(12500)
                .imageUrl("https://ifh.cc/g/5goMyn.webp")
                .inventory(70)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("과테말라 안티구아 골드")
                .description("깊고 진한 풍미를 자랑하는 핸드드립 커피 원두입니다.")
                .price(41570)
                .imageUrl("https://ifh.cc/g/q7jZGa.webp")
                .inventory(82)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("콜롬비아 수프리모 클래식")
                .description("깊고 진한 풍미를 자랑하는 핸드드립 커피 원두입니다.")
                .price(23470)
                .imageUrl("https://ifh.cc/g/jycCvs.webp")
                .inventory(67)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("브라질 세하도 내추럴")
                .description("산뜻한 산미와 부드러운 바디감을 지닌 스페셜티 원두입니다.")
                .price(45150)
                .imageUrl("https://ifh.cc/g/17YBMV.webp")
                .inventory(79)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("인도네시아 만델링 프리미엄")
                .description("견과류와 초콜릿 향이 어우러진 풍부한 향미가 특징입니다.")
                .price(28620)
                .imageUrl("https://ifh.cc/g/4tZpGl.webp")
                .inventory(48)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("에티오피아 예가체프")
                .description("과일 향과 부드러운 산미가 특징인 핸드드립 원두입니다.")
                .price(12500)
                .imageUrl("https://ifh.cc/g/YN0j5X.webp")
                .inventory(30)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("과테말라 안티구아")
                .description("깊고 진한 풍미와 달콤한 여운이 느껴지는 원두입니다.")
                .price(14000)
                .imageUrl("https://ifh.cc/g/CDCF0H.webp")
                .inventory(45)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("콜롬비아 수프리모")
                .description("견과류와 초콜릿 향이 감도는 부드러운 원두입니다.")
                .price(13000)
                .imageUrl("https://ifh.cc/g/5fYad6.webp")
                .inventory(60)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("브라질 산토스")
                .description("고소한 향과 깔끔한 마무리가 특징인 원두입니다.")
                .price(11000)
                .imageUrl("https://ifh.cc/g/VZJy6z.webp")
                .inventory(55)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("케냐 AA")
                .description("풍부한 바디감과 상큼한 산미가 어우러진 원두입니다.")
                .price(15000)
                .imageUrl("https://ifh.cc/g/ahBaR9.webp")
                .inventory(48)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("자메이카 블루마운틴 No.1")
                .description("부드러운 단맛과 은은한 과일향이 조화를 이루는 최고급 원두입니다.")
                .price(45000)
                .imageUrl("https://ifh.cc/g/F7o1vx.webp")
                .inventory(100)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("파나마 에스메랄다 게이샤")
                .description("화려한 꽃향과 우아한 단맛이 돋보이는 스페셜티 원두입니다.")
                .price(60000)
                .imageUrl("https://ifh.cc/g/jo51Xp.webp")
                .inventory(50)
                .category(ProductCategory.HAND_DRIP)
                .build());

        //DECAF

        productRepository.saveAndFlush(Product.builder()
                .name("콜롬비아 디카페인 스무스")
                .description("카페인 부담 없이 즐길 수 있는 부드러운 디카페인 원두입니다.")
                .price(19800)
                .imageUrl("https://ifh.cc/g/G89VZB.webp")
                .inventory(45)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("에티오피아 디카페인 내추럴")
                .description("자연 방식으로 카페인을 제거하여 고유의 풍미를 유지한 원두입니다.")
                .price(25300)
                .imageUrl("https://ifh.cc/g/6BS8rD.webp")
                .inventory(59)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("브라질 디카페인 클래식")
                .description("고소한 풍미와 부드러운 질감을 가진 디카페인 원두입니다.")
                .price(21400)
                .imageUrl("https://ifh.cc/g/y2BNKz.webp")
                .inventory(60)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("과테말라 디카페인 미디엄")
                .description("풍부한 바디감과 균형 잡힌 맛이 특징인 디카페인 원두입니다.")
                .price(23500)
                .imageUrl("https://ifh.cc/g/xwm2Ct.webp")
                .inventory(55)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("케냐 디카페인 AA")
                .description("풍부한 과일 향과 산미가 조화를 이루는 디카페인 원두입니다.")
                .price(26800)
                .imageUrl("https://ifh.cc/g/rtgC64.webp")
                .inventory(45)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("인도 디카페인 로부스타")
                .description("강한 바디감과 깊은 맛이 특징인 디카페인 원두입니다.")
                .price(18700)
                .imageUrl("https://ifh.cc/g/BlxnXT.webp")
                .inventory(70)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("탄자니아 디카페인 킬리만자로")
                .description("부드러운 질감과 향긋한 과일향이 특징인 디카페인 원두입니다.")
                .price(24600)
                .imageUrl("https://ifh.cc/g/pfBlGJ.webp")
                .inventory(50)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("코스타리카 디카페인 허니")
                .description("은은한 단맛과 균형 잡힌 바디감이 돋보이는 디카페인 원두입니다.")
                .price(22900)
                .imageUrl("https://ifh.cc/g/aayvKH.webp")
                .inventory(65)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("파나마 디카페인 게이샤")
                .description("고급스러운 꽃향과 복합적인 향미가 돋보이는 디카페인 원두입니다.")
                .price(27500)
                .imageUrl("https://ifh.cc/g/aWk5sq.webp")
                .inventory(55)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("멕시코 디카페인 치아파스")
                .description("견과류의 고소한 향과 부드러운 질감이 특징인 디카페인 원두입니다.")
                .price(22200)
                .imageUrl("https://ifh.cc/g/ZRANx8.webp")
                .inventory(58)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("이탈리아 디카페인 에스프레소")
                .description("진한 크레마와 깊은 풍미가 어우러진 이탈리안 스타일 디카페인 원두입니다.")
                .price(25000)
                .imageUrl("https://ifh.cc/g/gHx0sl.webp")
                .inventory(40)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("프랑스 디카페인 블렌드")
                .description("부드러운 질감과 고급스러운 다크 초콜릿 향이 특징인 디카페인 원두입니다.")
                .price(26000)
                .imageUrl("https://ifh.cc/g/T8ch4T.webp")
                .inventory(45)
                .category(ProductCategory.DECAF)
                .build());

        //TEA

        productRepository.saveAndFlush(Product.builder()
                .name("다즐링 퍼스트 플러시")
                .description("풍부한 향과 깊은 맛을 자랑하는 프리미엄 홍차입니다.")
                .price(31500)
                .imageUrl("https://ifh.cc/g/Ga7Vds.webp")
                .inventory(62)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("아삼 골든 팁스")
                .description("은은한 꽃향과 함께 부드러운 단맛이 느껴지는 명품 홍차입니다.")
                .price(22900)
                .imageUrl("https://ifh.cc/g/y3hHh9.webp")
                .inventory(48)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("실론 우바 프리미엄")
                .description("깊고 진한 맛이 오랜 여운을 남기는 스페셜티 티입니다.")
                .price(29700)
                .imageUrl("https://ifh.cc/g/WgaDZY.webp")
                .inventory(72)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("히말라야 네팔 실버팁스")
                .description("우아한 바디감과 신선한 향미가 조화를 이루는 차입니다.")
                .price(34200)
                .imageUrl("https://ifh.cc/g/vSyojX.webp")
                .inventory(60)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("프랑스 몽마르트르 블렌드")
                .description("산뜻한 과일 향이 어우러진 고급 블렌드 홍차입니다.")
                .price(27800)
                .imageUrl("https://ifh.cc/g/fY583d.webp")
                .inventory(52)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("영국 얼그레이 클래식")
                .description("우아한 베르가못 향이 감도는 클래식한 홍차입니다.")
                .price(26500)
                .imageUrl("https://ifh.cc/g/W0r39d.webp")
                .inventory(58)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("스리랑카 누와라엘리야 블랙티")
                .description("산뜻한 꽃향과 고급스러운 바디감을 지닌 블랙티입니다.")
                .price(29900)
                .imageUrl("https://ifh.cc/g/GMcAWF.webp")
                .inventory(47)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("중국 용정 녹차 스페셜")
                .description("신선한 풀 향과 부드러운 감미가 어우러진 녹차입니다.")
                .price(31400)
                .imageUrl("https://ifh.cc/g/xspLwJ.webp")
                .inventory(55)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("중국 대홍포 우롱차")
                .description("깊고 강한 풍미가 특징인 최고급 우롱차입니다.")
                .price(32500)
                .imageUrl("https://ifh.cc/g/jgqOOZ.webp")
                .inventory(45)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("일본 말차 프리미엄")
                .description("고운 가루로 갈아낸 진한 녹차로, 깊은 감칠맛이 특징입니다.")
                .price(29800)
                .imageUrl("https://ifh.cc/g/vngFAp.webp")
                .inventory(48)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("러시아 카라반 홍차")
                .description("스모키한 풍미와 깊은 감칠맛이 특징인 전통 러시아 블랙티입니다.")
                .price(28500)
                .imageUrl("https://ifh.cc/g/o8jA05.webp")
                .inventory(50)
                .category(ProductCategory.TEA)
                .build());

        productRepository.saveAndFlush(Product.builder()
                .name("터키 애플티 스페셜")
                .description("상큼한 사과 향과 은은한 달콤함이 어우러진 터키식 블렌드 티입니다.")
                .price(22000)
                .imageUrl("https://ifh.cc/g/MylRGW.webp")
                .inventory(50)
                .category(ProductCategory.TEA)
                .build());

        System.out.println("현재 저장된 상품 수: " + productRepository.count());
    }

    @Transactional
    public void orderInit() {
        if (orderRepository.count() > 0) {
            return;
        }

        String email = "test%d@example.com";
        String address = "aaa";
        String zipCode = "bbb";


        for (int i = 1; i <= 30; i++) {
            Map<Long, Integer> productQuantities = Map.of((long) i, 1, (long) (i + 1), 1);
            orderService.createOrder(email.formatted(i), address + i, zipCode + i, productQuantities);
        }

        System.out.println("현재 저장된 주문 수: " + orderRepository.count());
    }

}
