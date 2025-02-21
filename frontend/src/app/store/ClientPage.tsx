"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useRouter, useSearchParams } from "next/navigation";
import { paging } from "@/global/paging";
import Link from "next/link";

export default function ClientPage({
  rsData,
  keywordType = "name",
  keyword = "",
  pageSize = 10,
  page = 1,
}: {
  rsData: { data: { totalPages: number; items: any[] } };
  keywordType?: "name" | "description";
  keyword?: string;
  pageSize?: number;
  page?: number;
}) {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 현재 URL에서 검색 및 페이지네이션 정보를 가져옴
  const currentKeywordType =
    searchParams.get("keywordType") || keywordType || "name";
  const currentKeyword = searchParams.get("keyword") || keyword || "";
  const currentPageSize =
    Number(searchParams.get("pageSize")) || pageSize || 10;
  const currentPage = Number(searchParams.get("page")) || page || 1;

  const { totalPages, items } = rsData?.data || { totalPages: 0, items: [] };

  // 페이징 처리
  const { currentPageItems, visiblePages, handlePageChange } = paging(
    items,
    currentPageSize,
    currentPage
  );

  // 빈 페이지 제거
  const validPages = visiblePages.filter((pageNum) => {
    const startIdx = (pageNum - 1) * currentPageSize;
    return items.slice(startIdx, startIdx + currentPageSize).length > 0;
  });

  // 장바구니 상태 관리
  const [cart, setCart] = useState<
    Map<number, { item: any; quantity: number }>
  >(new Map());

  // 팝업 상태 관리
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any | null>(null);

  // 상품 추가
  const addToCart = (item: any) => {
    setCart((prevCart) => {
      const newCart = new Map(prevCart);
      if (newCart.has(item.id)) {
        newCart.set(item.id, {
          item,
          quantity: newCart.get(item.id)?.quantity! + 1,
        });
      } else {
        newCart.set(item.id, { item, quantity: 1 });
      }
      return newCart;
    });
  };

  // 상품 삭제
  const removeFromCart = (itemId: number) => {
    setCart((prevCart) => {
      const newCart = new Map(prevCart);
      newCart.delete(itemId);
      return newCart;
    });
  };

  // 결제 버튼 클릭
  const handleCheckout = () => {
    if (cart.size === 0) {
      alert("장바구니가 비어 있습니다.");
      return;
    }
    alert("결제가 완료되었습니다! 🎉");
    setCart(new Map());
  };

  // 팝업 열기
  const openModal = (item: any) => {
    setSelectedItem(item);
    setIsModalOpen(true);
  };

  // 팝업 닫기
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedItem(null);
  };

  return (
    <div className="flex gap-10">
      <div className="w-2/3">
        <h1 className="text-2xl font-bold mb-4">상품 목록</h1>

        {/* 검색 필터 */}
        <form
          onSubmit={(e) => {
            e.preventDefault();
            const formData = new FormData(e.target as HTMLFormElement);
            const searchKeyword = formData.get("keyword") as string;
            const searchKeywordType = formData.get("keywordType") as string;
            const page = 1;
            const pageSize = formData.get("pageSize") as string;

            router.push(
              `/store?keywordType=${searchKeywordType}&keyword=${searchKeyword}&pageSize=${pageSize}&page=${page}`
            );
          }}
          className="mb-5"
        >
          <div className="flex gap-3 items-center">
            <select
              name="keywordType"
              defaultValue={keywordType}
              className="border p-2 rounded"
            >
              <option value="name">상품 이름</option>
              <option value="description">내용</option>
            </select>
            <Input
              type="text"
              placeholder="검색어 입력"
              name="keyword"
              defaultValue={keyword}
              className="w-[200px]"
            />
            <Button>검색</Button>
          </div>
        </form>

        {/* 상품 리스트 */}
        <div className="grid grid-cols-2 md:grid-cols-3 gap-5">
          {currentPageItems
            .filter((item) => item.published) // 공개된 상품만 필터링
            .map((item) => (
              <div key={item.id} className="border rounded-lg p-3 shadow-md">
                <div
                  className="block cursor-pointer"
                  onClick={() => openModal(item)}
                >
                  <img
                    src={item.imageUrl}
                    alt={item.name}
                    className="w-full h-40 object-cover rounded-lg"
                  />
                  <h3 className="text-lg font-semibold mt-2">{item.name}</h3>
                  <p className="text-sm text-gray-500">
                    상품 ID: {item.itemId}
                  </p>
                </div>
                <Button
                  className="mt-3 w-full bg-blue-500 hover:bg-blue-600"
                  onClick={() => addToCart(item)}
                >
                  장바구니 추가
                </Button>
              </div>
            ))}
        </div>

        {/* 페이징 */}
        <div className="flex justify-center mt-4 space-x-2">
          {validPages.map((pageNum) => (
            <Link
              key={pageNum}
              href={`/store?keywordType=${currentKeywordType}&keyword=${currentKeyword}&pageSize=${currentPageSize}&page=${pageNum}`}
              className={`px-2 py-1 transition-colors duration-300 ${
                pageNum == currentPage ? `text-red-500` : `text-blue-500`
              }`}
            >
              {pageNum}
            </Link>
          ))}
        </div>
      </div>

      {/* 장바구니 */}
      <div className="w-1/3 border p-5 rounded-lg shadow-lg">
        <h2 className="text-lg font-bold">장바구니</h2>
        {cart.size === 0 ? (
          <p>장바구니가 비어 있습니다.</p>
        ) : (
          <ul>
            {Array.from(cart.values()).map(({ item, quantity }) => (
              <li
                key={item.id}
                className="border-b py-2 flex justify-between items-center"
              >
                <span>
                  {item.name} (수량: {quantity})
                </span>
                <Button onClick={() => removeFromCart(item.id)} size="sm">
                  제거
                </Button>
              </li>
            ))}
          </ul>
        )}
        <Button
          className="mt-4 w-full bg-green-500 hover:bg-green-600"
          onClick={handleCheckout}
        >
          결제하기
        </Button>
      </div>

      {/* 상세 정보 팝업 */}
      {isModalOpen && selectedItem && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white p-5 rounded-lg shadow-lg w-1/2">
            <h2 className="text-xl font-bold mb-4">{selectedItem.name}</h2>
            <img
              src={selectedItem.imageUrl}
              alt={selectedItem.name}
              className="w-full h-40 object-cover rounded-lg mb-4"
            />
            <p>
              <strong>상품 ID:</strong> {selectedItem.itemId}
            </p>
            <p>
              <strong>공개 여부:</strong> {`${selectedItem.published}`}
            </p>
            <p>
              <strong>상품 정보:</strong> {selectedItem.description}
            </p>
            <div className="mt-4 flex gap-3">
              <Button
                className="bg-blue-500"
                onClick={() => addToCart(selectedItem)}
              >
                장바구니 추가
              </Button>
              <Button className="bg-red-500" onClick={closeModal}>
                닫기
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
