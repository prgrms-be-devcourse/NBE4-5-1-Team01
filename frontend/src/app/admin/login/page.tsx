import { cookies } from "next/headers";
import ClientPage from "./ClientPage";
import { RequestCookie } from "next/dist/compiled/@edge-runtime/cookies";

export default async function Page() {
    const myCookie = await cookies();
  const { isLogin } = parseAccessToken(myCookie.get("accessToken"));

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
  return <ClientPage />;
}
