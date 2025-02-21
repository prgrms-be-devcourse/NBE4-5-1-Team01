"use client";

import Link from "next/link";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faStore } from "@fortawesome/free-solid-svg-icons";
//import router from "next/router";

//어드민 로그인 환경 구현 시 주석부분 처리 요망
export default function ClinetLayout({
  children,
  //me,
  fontVariable,
  fontClassName,
}: Readonly<{
  children: React.ReactNode;
  //me: components["schemas"]["MemberDto"];
  fontVariable: string;
  fontClassName: string;
}>) {
  //const isLogined = me.id !== 0;
  const isLogined = false;

  //   async function handleLogout(e: React.MouseEvent<HTMLAnchorElement>) {
  //     e.preventDefault();
  //     const response = await admin.DELETE("/amin/logout", {
  //       credentials: "include",
  //     });

  //     if (response.error) {
  //       alert(response.error.msg);
  //       return;
  //     }

  //     router.push(`/`);
  //   }

  return (
    <html lang="en" className={`${fontVariable}`}>
      <body className={`min-h-[100dvh] flex flex-col ${fontClassName}`}>
        <header className="flex justify-end gap-3 px-4">
          <DropdownMenu>
            <DropdownMenuTrigger>
              <FontAwesomeIcon icon={faStore} />
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuSeparator />
              <DropdownMenuItem>
                <Link href="/">초기 화면</Link>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <Link href="/store">구매자 페이지</Link>
              </DropdownMenuItem>
              <DropdownMenuItem>
                <Link href="/admin">관리자 페이지</Link>
              </DropdownMenuItem>
              {isLogined && (
                <DropdownMenuItem>
                  {/* <Link href="" onClick={handleLogout}> */}
                  <Link href="">로그아웃</Link>
                </DropdownMenuItem>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        </header>
        <div className="flex-grow">{children}</div>
        <footer></footer>
        <input type="text" />
      </body>
    </html>
  );
}
