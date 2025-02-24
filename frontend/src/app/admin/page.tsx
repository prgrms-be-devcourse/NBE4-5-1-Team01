import { cookies } from "next/headers";
import { RequestCookie } from "next/dist/compiled/@edge-runtime/cookies";
import { mockRsData } from "@/global/mockRsData";
import ClientPage from "./ClientPage";
import client from "@/lib/backend/client";

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
    pageSize = 7,
    page = 1,
  } = await searchParams;

  const myCookie = await cookies();
  const { isLogin } = parseAccessToken(myCookie.get("accessToken"));

  //권한 없을 시 관리자 로그인 페이지로 이동
  if (!isLogin) {
    <ClientPage isLogin={isLogin} />;
  }

  const response = await client.GET("/GCcoffee/admin/items", {
    headers: {
      cookie: (await cookies()).toString(),
    },
  });

  console.log(response);

  const rsData = response.data;

  return (
    <ClientPage
      isLogin={isLogin}
      rsData={rsData}
      pageSize={pageSize}
      keyword={keyword}
      keywordType={keywordType}
      page={page}
    />
  );
}

function parseAccessToken(accessToken: RequestCookie | undefined) {
  let isExpired = true;
  let payload = null;

  if (accessToken) {
    try {
      const tokenParts = accessToken.value.split(".");
      payload = JSON.parse(Buffer.from(tokenParts[1], "base64").toString());
      const expTimestamp = payload.exp * 1000; // exp는 초 단위이므로 밀리초로 변환
      isExpired = Date.now() > expTimestamp;
    } catch (e) {
      console.error("토큰 파싱 중 오류 발생:", e);
    }
  }

  let isLogin = payload !== null;

  return { isLogin, isExpired, payload };
}
