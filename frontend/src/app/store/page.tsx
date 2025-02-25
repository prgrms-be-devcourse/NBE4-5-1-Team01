import ClientPage from "./ClientPage";
import client from "@/lib/backend/client";

export default async function Page() {
  const categories: Array<"HAND_DRIP" | "DECAF" | "TEA"> = [
    "HAND_DRIP",
    "DECAF",
    "TEA",
  ];

  console.log("📌 요청할 카테고리 목록:", categories);

  const responses = await Promise.all(
    categories.map((category) => {
      console.log(`🔍 ${category} 카테고리 아이템 요청 중...`);
      return client.GET("/GCcoffee/items", {
        params: {
          query: {
            category,
            pageSize: 10,
            page: 0,
            sort: "asc",
          },
        },
      });
    })
  );

  console.log("✅ 응답 받은 데이터 (원본):", responses);

  const rows = categories.map((category, index) => {
    const responseData = responses[index]?.data;

    console.log(
      `${category} 카테고리 데이터 처리 중...`,
      JSON.stringify(responseData, null, 2)
    );

    return {
      category,
      items: responseData?.data?.items ?? [],
    };
  });

  console.log("최종 변환된 데이터:", JSON.stringify(rows, null, 2));

  return <ClientPage rows={rows} />;
}
