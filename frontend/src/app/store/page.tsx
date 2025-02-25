import ClientPage from "./ClientPage";
import client from "@/lib/backend/client";

export default async function Page() {
  const categories: Array<"HAND_DRIP" | "DECAF" | "TEA"> = [
    "HAND_DRIP",
    "DECAF",
    "TEA",
  ];

  const responses = await Promise.all(
    categories.map((category) =>
      client.GET("/GCcoffee/items", {
        params: {
          query: {
            category,
            pageSize: 10,
            page: 0,
            sort: "asc",
          },
        },
      })
    )
  );

  const rows = categories.map((category, index) => ({
    category,
    items: responses[index].data?.data?.items ?? [],
  }));

  return <ClientPage rows={rows} />;
}
