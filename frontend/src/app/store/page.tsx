import ClientPage from "./ClientPage";
import { mockRsData } from "../../global/mockRsData";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    keywordType?: "name" | "description";
    keyword: string;
    pageSize: number;
    page: number;
  };
}) {
  const {
    keywordType = "name",
    keyword = "",
    pageSize = 10,
    page = 1,
  } = await searchParams;

  //가짜 rsdata, DB연동후 삭제 필요
  const rsData = mockRsData;

  //   const response = await client.GET("/items", {
  //     params: {
  //       query: {
  //         keyword,
  //         keywordType,
  //         pageSize,
  //         page,
  //       },
  //     },
  //   });

  //const rsData = response.data!!;

  return (
    <ClientPage
      rsData={rsData}
      pageSize={pageSize}
      keyword={keyword}
      keywordType={keywordType}
      page={page}
    />
  );
}
