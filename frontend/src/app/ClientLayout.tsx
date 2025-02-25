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
import client from "@/lib/backend/client";
//import router from "next/router";

export default function ClinetLayout({
  children,
  me,
  fontVariable,
  fontClassName,
}: Readonly<{
  children: React.ReactNode;
  me: { id: number; role: string };
  fontVariable: string;
  fontClassName: string;
}>) {
  const isAdmin = me.role === "admin";

  async function handleLogout(e: React.MouseEvent<HTMLAnchorElement>) {
    e.preventDefault();
    const response = await client.DELETE("/GCcoffee/admin/logout", {
      credentials: "include",
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    window.location.href = "/";
  }

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
                <Link href="/store">구매자 페이지</Link>
              </DropdownMenuItem>

              {!isAdmin && (
                <DropdownMenuItem>
                  <Link href="/admin/login">관리자 로그인</Link>
                </DropdownMenuItem>
              )}

              {isAdmin && (
                <>
                  <DropdownMenuItem>
                    <Link href="/admin">관리자 상품 관리 페이지</Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem>
                    <Link href="/admin/order">관리자 주문 관리 페이지</Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem>
                    <Link href="" onClick={handleLogout}>
                      로그아웃
                    </Link>
                  </DropdownMenuItem>
                </>
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
