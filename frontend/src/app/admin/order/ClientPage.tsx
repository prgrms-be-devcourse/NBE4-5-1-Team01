"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { components } from "@/lib/backend/apiV1/schema";
import client from "@/lib/backend/client";

export default function ClientPage({
  rsData,
  keyword,
  pageSize,
  page,
  sort,
}: {
  rsData: components["schemas"]["RsDataPageDtoOrderResponseWithDetail"];
  keyword: string;
  pageSize: number;
  page: number;
  sort: "asc" | "desc";
}) {
  const pageDto = rsData.data;
  const router = useRouter();

  // 팝업 상태 관리
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [selectedStatus, setSelectedStatus] = useState(
    selectedItem?.orderStatus
  );
  const [formData, setFormData] = useState({
    keyword: keyword || "",
    pageSize: pageSize.toString() || "10",
    page: page.toString() || "1",
    sort: sort || "desc",
  });

  // 관리자 페이지 이동처리 - 쿠키로그인 되면 추가가
  // useEffect(() => {
  //   if (!isLogin) {
  //     router.replace("/admin/login");
  //   }
  // }, [isLogin, router]);

  // 팝업 열기
  const openModal = (item: any) => {
    fetchOrderItems(item.id);
    setIsModalOpen(true);
  };

  // 팝업 닫기
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedItem(null);
    setSelectedStatus("PENDING");
  };

  // 주문 목록 가져오기
  const fetchOrderItems = async (id: number) => {
    try {
      setLoading(true);
      setError("");

      const response = await client.GET("/GCcoffee/admin/orders/{id}", {
        params: {
          path: {
            id,
          },
        },
      });

      const rsData = response.data;
      if (!rsData || !rsData.data || !rsData.data.items) {
        throw new Error("데이터를 불러오는 데 실패했습니다.");
      }

      setSelectedItem(rsData.data);
      setSelectedStatus(rsData.data.orderStatus);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 입력값 변경 핸들러
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  // 폼 제출 핸들러
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    formData["page"] = "1";

    const queryString = new URLSearchParams(formData).toString();
    router.push(`/admin/order?${queryString}`);
  };

  // 주문 삭제 핸들러
  const handleDelete = async (id: number) => {
    if (!window.confirm("정말 이 주문을 삭제하시겠습니까?")) return;

    try {
      setLoading(true);
      setError("");

      const response = await client.DELETE("/GCcoffee/admin/orders/{id}", {
        params: {
          path: {
            id,
          },
        },
      });

      if (response.error) {
        throw new Error("주문 삭제에 실패했습니다.");
      }

      alert("주문이 삭제되었습니다.");
      closeModal();
      router.refresh();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // 주문 수정 핸들러
  const handleUpdate = async () => {
    if (!selectedItem) return;
    const id = selectedItem.id;

    try {
      setLoading(true);
      setError("");

      const response = await client.PATCH("/GCcoffee/admin/orders/{id}", {
        params: {
          path: {
            id,
          },
        },
        body: {
          orderStatus: selectedStatus,
        },
      });

      if (response.error) {
        throw new Error("주문 상태 업데이트 실패");
      }

      alert("주문 상태가 업데이트되었습니다.");
      closeModal();
      router.refresh();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex gap-10">
      <div className="w-2/3">
        <h1 className="text-2xl font-bold mb-4">주문 목록</h1>

        {/* 검색 */}
        <form onSubmit={handleSubmit} className="mb-5">
          <div className="flex gap-3 items-center">
            <select
              name="sort"
              className="border p-2 rounded"
              onChange={handleChange}
              value={formData.sort}
            >
              <option value="desc">최신순</option>
              <option value="asc">오래된순</option>
            </select>
            <select
              name="pageSize"
              className="border p-2 rounded"
              onChange={handleChange}
              value={formData.pageSize}
            >
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="15">15</option>
            </select>
            <Input
              type="text"
              placeholder="검색어 입력"
              name="keyword"
              className="w-[200px]"
              onChange={handleChange}
              value={formData.keyword}
            />
            <Button type="submit">검색</Button>
          </div>
        </form>

        {/* 주문 목록 */}
        <ul className="space-y-4">
          {pageDto.items.map((item) => (
            <li key={item.id} className="border rounded-lg p-4 shadow-md">
              <div
                className="flex items-center cursor-pointer"
                onClick={() => openModal(item)}
              >
                <div>
                  <h3 className="text-lg font-semibold">
                    {item.email}님의 주문
                  </h3>
                  <p className="text-sm text-gray-500">주문 ID: {item.id}</p>
                  <p className="text-sm text-gray-500">주소: {item.email}</p>
                  <p className="text-sm text-gray-500">
                    우편번호: {item.zipCode}
                  </p>
                  <p className="text-sm text-gray-500">
                    주문일자: {item.orderDate}
                  </p>
                  <p className="text-sm text-gray-500">
                    주문 상태: {item.orderStatus}
                  </p>
                  <p className="text-sm text-gray-500">
                    총액: {item.totalPrice}원
                  </p>
                </div>
              </div>
            </li>
          ))}
        </ul>

        {/* 페이지 이동 */}
        <div className="flex gap-3">
          {Array.from({ length: pageDto.totalPages }, (_, i) => i + 1).map(
            (pageNo) => {
              return (
                <Link
                  key={pageNo}
                  className={pageNo == page ? `text-red-500` : `text-blue-500`}
                  href={`/admin/order?&keyword=${keyword}&pageSize=${pageSize}&page=${pageNo}&sort=${sort}`}
                >
                  {pageNo}
                </Link>
              );
            }
          )}
        </div>

        {/* 주문 상세 팝업, 수정, 삭제 기능 */}
        {isModalOpen && selectedItem && (
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
            <div className="bg-white p-5 rounded-lg shadow-lg w-1/2">
              <h2 className="text-xl font-bold mb-4">주문 상세</h2>
              {/* 로딩 표시 */}
              {loading && <p className="text-blue-500">불러오는 중...</p>}

              {/* 에러 메시지 */}
              {error && <p className="text-red-500">{error}</p>}

              {!loading && !error && selectedItem && (
                <div>
                  <p className="text-lg font-semibold">주문 상태</p>
                  <select
                    value={selectedStatus}
                    onChange={(e) => setSelectedStatus(e.target.value)}
                    className="border p-2 rounded w-full"
                  >
                    <option value="PENDING">PENDING</option>
                    <option value="COMPLETED">COMPLETED</option>
                    <option value="CANCEL">CANCEL</option>
                  </select>

                  {/* 주문 상품 리스트 */}
                  <h3 className="text-lg font-semibold mt-4">상품 목록</h3>
                  {selectedItem.items?.length > 0 ? (
                    <ul className="mt-2 border-t pt-2 space-y-2">
                      {selectedItem.items.map((item: any, index: number) => (
                        <li
                          key={index}
                          className="border p-2 rounded shadow-sm"
                        >
                          <p>상품명: {item.productName}</p>
                          <p>수량: {item.quantity}개</p>
                          <p>가격: {item.totalPrice}원</p>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-gray-500">상품 정보가 없습니다.</p>
                  )}
                </div>
              )}

              <div className="flex items-center gap-2 mb-2">
                <Button
                  className="mt-3 w-full bg-blue-500 hover:bg-blue-600"
                  onClick={handleUpdate}
                  disabled={loading}
                >
                  {loading ? "업데이트 중..." : "수정"}
                </Button>
                <Button
                  className="mt-3 w-full bg-gray-500 hover:bg-gray-600"
                  onClick={() => selectedItem && handleDelete(selectedItem.id)}
                  disabled={loading}
                >
                  {loading ? "삭제 중..." : "삭제"}
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
      </div>
    </div>
  );
}
