import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";

export function paging(
  totalItems: any[],
  pageSize: number,
  currentPage: number
) {
  const router = useRouter();
  const searchParams = useSearchParams();

  // URL에서 page 값을 가져와서 초기화
  const initialPage = Number(searchParams.get("page")) || 1;
  const [page, setPage] = useState(initialPage);

  useEffect(() => {
    setPage(initialPage);
  }, [initialPage]); // URL 변경 시 page 업데이트

  // 전체 페이지 수 계산
  const totalPages = Math.ceil(totalItems.length / pageSize);

  // 현재 페이지에 해당하는 아이템들만 가져오기
  const startIndex = (page - 1) * pageSize;
  const endIndex = page * pageSize;
  const currentPageItems = totalItems.slice(startIndex, endIndex);

  // 페이지 이동 처리 (router.push 후 상태 동기화)
  const handlePageChange = (newPage: number) => {
    if (newPage !== page) {
      router.push(`?page=${newPage}&pageSize=${pageSize}`);
    }
  };

  // 페이지 번호 계산
  const pageRange = 5;
  const visiblePages = Array.from(
    { length: totalPages },
    (_, i) => i + 1
  ).filter(
    (pageNum) =>
      pageNum >= Math.max(1, page - Math.floor(pageRange / 2)) &&
      pageNum <= Math.min(totalPages, page + Math.floor(pageRange / 2))
  );

  return {
    currentPageItems,
    totalPages,
    visiblePages,
    handlePageChange,
    page,
  };
}
