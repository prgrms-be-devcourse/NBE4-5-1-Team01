"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";
import client from "@/lib/backend/client";

export default function ClientPage({
  isLogin,
  rsData = { data: { totalPages: 0, items: [] } },
  keywordType,
  keyword,
  totalPages,
  totalItems,
  curPageNo,
  pageSize,
  page,
}: {
  isLogin: boolean;
  rsData?: { data: { totalPages: number; items: any[] } };
  keywordType?: "name" | "description" | "category";
  keyword?: string;
  totalPages: number;
  totalItems: number;
  curPageNo: number;
  pageSize?: number;
  page?: number;
}) {
  const router = useRouter();
  const searchParams = useSearchParams();

  const { items = [] } = rsData?.data ?? {};

  // 관리자 페이지 이동처리 - 쿠키로그인 되면 추가가
  useEffect(() => {
    if (!isLogin) {
      router.replace("/admin/login");
    }
  }, [isLogin, router]);

  // 현재 URL에서 검색 및 페이지네이션 정보를 가져옴
  const [currentKeyword, setCurrentKeyword] = useState(keyword || "");
  const [currentKeywordType, setCurrentKeywordType] = useState<
    "name" | "description" | "category"
  >(keywordType || "name");
  const [isCategory, setIsCategory] = useState(false);
  const [currentPageSize, setCurrentPageSize] = useState(pageSize || 10);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  // 팝업 상태 관리
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any | null>(null);
  const [newItem, setNewItem] = useState<{
    name: string;
    price: number;
    imageUrl: string;
    inventory: number;
    description: string;
    category: "HAND_DRIP" | "DECAF" | "TEA";
  }>({
    name: "",
    price: 0,
    imageUrl: "",
    inventory: 0,
    description: "",
    category: "HAND_DRIP",
  });

  // 비로그인 시 관리자 페이지 이동처리
  // useEffect(() => {
  //   if (!isLogin) {
  //     router.replace("/admin/login");
  //   }
  // }, [isLogin, router]);

  useEffect(() => {
    const newPage = searchParams.get("page");

    if (newPage) {
      fetchProductItems(); // 페이지 번호가 변경되면 상품 목록 다시 불러오기
    }
  }, [searchParams, rsData]);

  useEffect(() => {
    if (currentKeywordType === "category") {
      if (!isCategory) {
        setCurrentKeyword("HAND_DRIP");
        setIsCategory(true);
      }
    } else if (isCategory) {
      // "category"에서 다른 값으로 변경될 때만 실행되도록 함
      setCurrentKeyword((prevKeyword) => {
        return prevKeyword === "HAND_DRIP" || prevKeyword === "DECAF" || prevKeyword === "TEA" ? "" : prevKeyword;
      });
      setIsCategory(false);
    }
  }, [currentKeywordType, isCategory]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setCurrentPageSize(parseInt(e.target.value));
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

  // 검색 핸들러
  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    fetchProductItems();

    // URL 업데이트
      router.push(
        `/admin?keywordType=${currentKeywordType}&keyword=${currentKeyword}&pageSize=${currentPageSize}`
      );
  };

  // 상품 목록 가져오기
  const fetchProductItems = async (pageOverride?: number) => {
    try {
      setLoading(true);
      setError("");

      const pageToFetch = pageOverride || page;

      const response = await client.GET("/GCcoffee/admin/items", {
        params: {
          query: {
            keyword,
            keywordType,
            pageSize,
            page: pageToFetch,
          },
        },
      });

      const rsData = response.data;
      if (!rsData || !rsData.data || !rsData.data.items) {
        throw new Error("데이터를 불러오는 데 실패했습니다.");
      }
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 상품 추가 핸들러
  const handleAddItem = async () => {
    if (!newItem.name || !newItem.description || !newItem.imageUrl) return;
    try {
      setLoading(true);
      setError("");
      const response = await client.POST("/GCcoffee/admin/item", {
        credentials: "include",
        body: {
          name: newItem.name,
          price: newItem.price,
          imageUrl: newItem.imageUrl,
          inventory: newItem.inventory,
          description: newItem.description,
          category: newItem.category,
        },
      });

      if (response.error) {
        throw new Error("상품 수정 실패");
      } else {
        alert("상품이 추가가되었습니다.");
        closeModal();
        router.refresh();
        setNewItem({
          name: "",
          price: 0,
          imageUrl: "",
          inventory: 0,
          description: "",
          category: "HAND_DRIP",
        });
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  // 상품 삭제 핸들러
  const handleDeleteItem = async (id: number) => {
    if (!window.confirm("정말 이 상품을 삭제하시겠습니까?")) return;

    try {
      setLoading(true);
      setError("");

      const response = await client.DELETE("/GCcoffee/admin/delete/{id}", {
        params: {
          path: {
            id,
          },
        },
      });

      if (response.error) {
        throw new Error("상품품 삭제에 실패했습니다.");
      }

      alert("상품품이 삭제되었습니다.");
      closeModal();
      router.refresh();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 상품 수정 핸들러
  const handleUpdateItem = async (id: number) => {
    if (!selectedItem.name || !selectedItem.description || !selectedItem.imageUrl || !selectedItem.category || selectedItem.price < 1) return;
    try {
      setLoading(true);
      setError("");
      const response = await client.PATCH("/GCcoffee/admin/item/{id}", {
        params: {
          path: {
            id,
          },
        },
        credentials: "include",
        body: {
          name: selectedItem.name,
          price: selectedItem.price,
          imageUrl: selectedItem.imageUrl,
          inventory: selectedItem.inventory,
          description: selectedItem.description,
          category: selectedItem.category,
        },
      });

      if (response.error) {
        throw new Error("상품 수정 실패");
      } else {
        alert("상품 정보가 수정되었습니다.");
        closeModal();
        router.refresh();
        closeModal();
      }
  } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex gap-10 p-10">
      <div className="w-2/3">
        <h1 className="text-2xl font-bold mb-4">상품 목록</h1>
        <form onSubmit={handleSearchSubmit} className="mb-5">
          <div className="flex gap-3 items-center">
          <select
              name="pageSize"
              className="border p-2 rounded"
              onChange={handleChange}
              value={currentPageSize}
            >
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="15">15</option>
            </select>
            <select
              name="keywordType"
              value={currentKeywordType}
              onChange={(e) =>
                setCurrentKeywordType(e.target.value as "name" | "description")
              }
              className="border p-2 rounded"
            >
              <option value="name">상품 이름</option>
              <option value="description">내용</option>
              <option value="category">카테고리</option>
            </select>            
            {currentKeywordType !== "category" ? <Input
              type="text"
              placeholder="검색어 입력"
              name="keyword"
              value={currentKeyword}
              onChange={(e) => {
                setCurrentKeyword(e.target.value)
              }}
              className="w-[200px]"
            /> : <select
            name="searchKeywordType"
            value={currentKeyword}
            onChange={(e) => setCurrentKeyword(e.target.value as "HAND_DRIP" | "DECAF" | "TEA")}
            className="border p-2 rounded"
          >
            <option value="HAND_DRIP">핸드 드립</option>
            <option value="DECAF">디카페인</option>
            <option value="TEA">티</option>
          </select>}
            <Button type="submit">검색</Button>
          </div>
        </form>

        {loading && <p className="text-blue-500">불러오는 중...</p>}
        {error && <p className="text-red-500">{error}</p>}

        {!loading && <ul className="space-y-4">
          {items.map((item: any) => (
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
                    재고: {item.inventory}
                  </p>
                  <p className="text-sm text-gray-500">
                    상품 정보: {`${item.description}`}
                  </p>
                  <p className="text-sm text-gray-500">
                    카테고리: {`${item.category === "HAND_DRIP" ? "핸드 드립" : item.category === "DECAF" ? "디카페인" : "티"}`}
                  </p>
                </div>
              </div>
            </li>
          ))}
        </ul>}
        <div className="flex justify-center mt-4 space-x-2">
          {Array.from({ length: totalPages }, (_, index) => index + 1).map(
            (pageNum) => (
              <Link
                key={pageNum}
                href={`/admin?keywordType=${currentKeywordType}&keyword=${currentKeyword}&pageSize=${currentPageSize}&page=${pageNum}`}
                aria-disabled={curPageNo === pageNum}
                className={`px-2 py-1 transition-colors duration-300 ${
                  pageNum === curPageNo ? "text-gray-500" : "text-blue-500"
                }`}
              >
                {pageNum}
              </Link>
            )
          )}
        </div>
      </div>

      {isModalOpen && selectedItem && (
        <div 
          className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
          onClick={closeModal}
        >
          <div 
            className="bg-white p-5 rounded-lg shadow-lg w-1/2"
            onClick={(e) => e.stopPropagation()}
            >
            <h2 className="text-xl font-bold mb-4">상품 수정</h2>
            <img
              src={selectedItem.imageUrl}
              alt={selectedItem.name}
              className="w-full h-40 object-cover rounded-lg mb-4"
            />
            <label className="block text-sm font-semibold">이름</label>
            <Input
              type="text"
              value={selectedItem.name}
              onChange={(e) =>
                setSelectedItem({ ...selectedItem, name: e.target.value })
              }
              className="mb-2"
            />
            <label className="block text-sm font-semibold">가격</label>
            <Input
              type="number"
              value={selectedItem.price === 0 ? "" : selectedItem.price}
              onChange={(e) =>
                setSelectedItem({ ...selectedItem, price: parseFloat(e.target.value) || 0 })
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
            <label className="block text-sm font-semibold">재고</label>
            <Input
              type="number"
              value={selectedItem.inventory === 0 ? "" : selectedItem.inventory}
              onChange={(e) =>
                setSelectedItem({ ...selectedItem, inventory: parseFloat(e.target.value) || 0 })
              }
              className="mb-2"
            />
            <label className="block text-sm font-semibold">설명</label>
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
            <label className="block text-sm font-semibold">카테고리</label>
            <select
              name="keywordType"
              value={selectedItem.category}
              onChange={(e) =>
                setSelectedItem({
                  ...selectedItem,
                  category: e.target.value as "HAND_DRIP" | "DECAF" | "TEA",
                })
              }
              className="border p-2 rounded"
            >
            <option value="HAND_DRIP">핸드 드립</option>
            <option value="DECAF">디카페인</option>
            <option value="TEA">티</option>
          </select>
            <div className="mt-4 flex gap-3">
              <Button
                onClick={() => handleUpdateItem(selectedItem.id)}
                className="mt-3 w-full bg-blue-500 hover:bg-blue-600"
              >
                수정 완료
              </Button>
              <Button
                onClick={() => handleDeleteItem(selectedItem.id)}
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
          type="number"
          placeholder="상품 가격"
          value={newItem.price === 0 ? "" : newItem.price}
          onChange={(e) =>
            setNewItem({ ...newItem, price: parseFloat(e.target.value) || 0 })
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
        <Input
          type="number"
          placeholder="재고"
          value={newItem.inventory === 0 ? "" : newItem.inventory}
          onChange={(e) =>
            setNewItem({
              ...newItem,
              inventory: parseFloat(e.target.value) || 0,
            })
          }
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
        <div className="flex items-center gap-2 mb-2">
          <label>카테고리: </label>
          <select
            name="keywordType"
            value={newItem.category}
            onChange={(e) =>
              setNewItem({
                ...newItem,
                category: e.target.value as "HAND_DRIP" | "DECAF" | "TEA",
              })
            }
            className="border p-2 rounded"
          >
            <option value="HAND_DRIP">핸드 드립</option>
            <option value="DECAF">디카페인</option>
            <option value="TEA">티</option>
          </select>
        </div>
        <Button onClick={handleAddItem} className="w-full">
          추가
        </Button>
      </div>
    </div>
  );
}
