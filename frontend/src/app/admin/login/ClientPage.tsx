"use client";

import client from "@/lib/backend/client";
import { useRouter } from "next/navigation";

export default function ClinetPage() {
  const router = useRouter();

  async function login(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    const form = e.target as HTMLFormElement;

    const password = form.password.value;

    if (password.trim().length === 0) {
      alert("패스워드를 입력해주세요.");
      return;
    }

    const response = await client.POST("/GCcoffee/admin/login", {
      body: {
        password,
      },
      credentials: "include",
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    document.cookie = `token=${response.data}; path=/; secure; HttpOnly`;

    router.push("/admin");
    window.location.href = "/admin";
  }

  return (
    <>
      <div className="flex items-center justify-center h-screen bg-gray-100">
        <div className="w-96 p-8 bg-white shadow-lg rounded-lg">
          <h2 className="text-2xl font-semibold text-center text-gray-800 mb-6">
            관리자 로그인
          </h2>

          <form onSubmit={login} className="flex flex-col gap-4">
            <input
              type="password"
              name="password"
              placeholder="패스워드 입력"
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
            <button
              type="submit"
              className="w-full p-2 bg-blue-500 text-white font-semibold rounded-md hover:bg-blue-600 transition duration-200"
            >
              로그인
            </button>
          </form>
        </div>
      </div>
    </>
  );
}
