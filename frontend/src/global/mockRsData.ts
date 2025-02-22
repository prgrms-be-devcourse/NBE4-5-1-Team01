// DB 연동 후 삭제 요망
export const mockRsData = {
  data: {
    totalPages: 5,
    items: Array.from({ length: 30 }, (_, index) => ({
      id: index + 1, // id는 1부터 시작
      name: `샘플 상품 ${index + 1}`, // 샘플 상품 1, 샘플 상품 2, ...
      price: 1000 + index * 500, // 가격은 1000부터 시작해서 500씩 증가
      description: `샘플 상품 ${index + 1}의 설명입니다.`, // 설명 추가
      imageUrl: `${String.fromCharCode(97 + index)}.url`, // a.url, b.url, c.url, ...
      inventory: 50 - index, // 재고는 50에서 시작해 감소
      category: index % 2 === 0 ? "HAND_DRIP" : "DECAF",
      deletedAt: null, // 기본적으로 삭제되지 않음
    })),
  },
};
