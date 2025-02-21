// DB연동 후 삭제 요망
export const mockRsData = {
  data: {
    totalPages: 5,
    items: Array.from({ length: 30 }, (_, index) => ({
      id: index + 1, // id는 1부터 시작
      name: `샘플 상품 ${index + 1}`, // 샘플 상품 1, 샘플 상품 2, ...
      itemId: 101 + index, // itemId는 101부터 시작하여 증가
      published: index % 2 === 0, // 짝수번째 아이템은 published가 true, 홀수번째는 false
      listed: index % 2 === 0, // 짝수번째 아이템은 listed가 true, 홀수번째는 false
      imageUrl: `${String.fromCharCode(97 + index)}.url`, // a.url, b.url, c.url, ...
    })),
  },
};
