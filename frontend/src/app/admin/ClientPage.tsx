"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useRouter, useSearchParams } from "next/navigation";
import { paging } from "@/global/paging";
import Link from "next/link";

export default function ClientPage({
  rsData = { data: { totalPages: 0, items: [] } },
  keywordType,
  keyword,
  pageSize,
  page,
}: {
  rsData?: { data: { totalPages: number; items: any[] } };
  keywordType?: "name" | "description";
  keyword?: string;
  pageSize?: number;
  page?: number;
}) {
  const router = useRouter();
  const searchParams = useSearchParams();

  // 검색 및 페이징 정보
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

  // 팝업 상태 관리
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any | null>(null);

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
  // 상품 추가 상태 관리
  const [newItem, setNewItem] = useState({
    name: "",
    description: "",
    imageUrl: "",
    published: false,
  });

  // 새 상품 추가 핸들러
  const handleAddItem = () => {
    if (!newItem.name || !newItem.description || !newItem.imageUrl) return;
    const newItemData = {
      id: items.length + 1,
      itemId: `item-${items.length + 1}`,
      ...newItem,
    };
    rsData.data.items.push(newItemData);
    setNewItem({ name: "", description: "", imageUrl: "", published: false });
  };

  // 상품 삭제 핸들러
  const handleDeleteItem = (itemId: string) => {
    rsData.data.items = rsData.data.items.filter(
      (item) => item.itemId !== itemId
    );
    closeModal();
  };

  // 상품 수정 핸들러
  const handleUpdateItem = (updatedItem: any) => {
    rsData.data.items = rsData.data.items.map((item) =>
      item.itemId === updatedItem.itemId ? updatedItem : item
    );
    closeModal();
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
            const pageSize = formData.get("pageSize") as string;

            router.push(
              `/admin?keywordType=${searchKeywordType}&keyword=${searchKeyword}&pageSize=${pageSize}&page=1`
            );
          }}
          className="mb-5"
        >
          <div className="flex gap-3 items-center">
            <select
              name="keywordType"
              defaultValue={currentKeywordType}
              className="border p-2 rounded"
            >
              <option value="name">상품 이름</option>
              <option value="description">내용</option>
            </select>
            <Input
              type="text"
              placeholder="검색어 입력"
              name="keyword"
              defaultValue={currentKeyword}
              className="w-[200px]"
            />
            <Button>검색</Button>
          </div>
        </form>

        {/* 상품 리스트 */}
        <ul className="space-y-4">
          {currentPageItems.map((item) => (
            <li key={item.id} className="border rounded-lg p-4 shadow-md">
              <div
                className="flex items-center cursor-pointer"
                onClick={() => openModal(item)}
              >
                <img
                  src={item.imageUrl}
                  alt={item.name}
                  className="w-24 h-24 object-cover rounded-lg mr-4"
                />
                <div>
                  <h3 className="text-lg font-semibold">{item.name}</h3>
                  <p className="text-sm text-gray-500">
                    상품 ID: {item.itemId}
                  </p>
                  <p className="text-sm text-gray-500">
                    공개 여부: {`${item.published}`}
                  </p>
                </div>
              </div>
            </li>
          ))}
        </ul>

        {/* 페이징 */}
        <div className="flex justify-center mt-4 space-x-2">
          {validPages.map((pageNum) => (
            <Link
              key={pageNum}
              href={`/admin?keywordType=${currentKeywordType}&keyword=${currentKeyword}&pageSize=${currentPageSize}&page=${pageNum}`}
              className={`px-2 py-1 transition-colors duration-300 ${
                pageNum == currentPage ? `text-red-500` : `text-blue-500`
              }`}
            >
              {pageNum}
            </Link>
          ))}
        </div>
      </div>

      {/* 상세 정보 팝업, 수정 기능 */}
      {isModalOpen && selectedItem && (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
          <div className="bg-white p-5 rounded-lg shadow-lg w-1/2">
            <h2 className="text-xl font-bold mb-4">상품 수정</h2>
            <img
              src={selectedItem.imageUrl}
              alt={selectedItem.name}
              className="w-full h-40 object-cover rounded-lg mb-4"
            />
            <label className="block text-sm font-semibold">상품 이름</label>
            <Input
              type="text"
              value={selectedItem.name}
              onChange={(e) =>
                setSelectedItem({ ...selectedItem, name: e.target.value })
              }
              className="mb-2"
            />
            <label className="block text-sm font-semibold">상품 설명</label>
            <Input
              type="text"
              value={selectedItem.description}
              onChange={(e) =>
                setSelectedItem({
                  ...selectedItem,
                  description: e.target.value,
                })
              }
              className="mb-2"
            />
            <label className="block text-sm font-semibold">이미지 URL</label>
            <Input
              type="text"
              value={selectedItem.imageUrl}
              onChange={(e) =>
                setSelectedItem({ ...selectedItem, imageUrl: e.target.value })
              }
              className="mb-2"
            />
            <div className="flex items-center gap-2 mb-2">
              <input
                type="checkbox"
                checked={selectedItem.published}
                onChange={(e) =>
                  setSelectedItem({
                    ...selectedItem,
                    published: e.target.checked,
                  })
                }
              />
              <label>공개 여부</label>
            </div>
            <div className="mt-4 flex gap-3">
              <Button
                onClick={() => handleUpdateItem(selectedItem)}
                className="mt-3 w-full bg-blue-500 hover:bg-blue-600"
              >
                수정 완료
              </Button>
              <Button
                onClick={() => handleDeleteItem(selectedItem.itemId)}
                className="mt-3 w-full bg-gray-500 hover:bg-gray-600"
              >
                삭제
              </Button>
              <Button
                onClick={closeModal}
                className="mt-3 w-full bg-red-500 hover:bg-red-600"
              >
                닫기
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* 상품 추가 */}
      <div className="w-1/3 p-4 border rounded-lg shadow-md">
        <h2 className="text-lg font-bold mb-2">새 상품 추가</h2>
        <Input
          type="text"
          placeholder="상품 이름"
          value={newItem.name}
          onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
          className="mb-2"
        />
        <Input
          type="text"
          placeholder="상품 설명"
          value={newItem.description}
          onChange={(e) =>
            setNewItem({ ...newItem, description: e.target.value })
          }
          className="mb-2"
        />
        <Input
          type="text"
          placeholder="이미지 URL"
          value={newItem.imageUrl}
          onChange={(e) => setNewItem({ ...newItem, imageUrl: e.target.value })}
          className="mb-2"
        />
        <div className="flex items-center gap-2 mb-2">
          <input
            type="checkbox"
            checked={newItem.published}
            onChange={(e) =>
              setNewItem({ ...newItem, published: e.target.checked })
            }
          />
          <label>공개 여부</label>
        </div>
        <Button onClick={handleAddItem} className="w-full">
          추가
        </Button>
      </div>
    </div>
  );
}
