import ClientPage from "./ClientPage";
import client from "@/lib/backend/client";

export default async function Page() {
  const categories: Array<"HAND_DRIP" | "DECAF" | "TEA"> = [
    "HAND_DRIP",
    "DECAF",
    "TEA",
  ];

  const responses = await Promise.all(
    categories.map((category) => {
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

  const rows = categories.map((category, index) => {
    const responseData = responses[index]?.data;

    return {
      category,
      items: responseData?.data?.items ?? [],
    };
  });

  console.log("최종 변환된 데이터:", JSON.stringify(rows, null, 2));

  return <ClientPage rows={rows} />;
}
