"use client";

export default function ClinetPage() {
  async function login(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    const form = e.target as HTMLFormElement;

    const password = form.password.value;

    if (password.trim().length === 0) {
      alert("패스워드를 입력해주세요.");
      return;
    }

    // const response = await client.POST("/admin/login", {
    //   body: {
    //     password,
    //   },
    //   credentials: "include",
    // });

    // if (response.error) {
    //   alert(response.error.msg);
    //   return;
    // }

    // router.push(`/admin`);
  }

  return (
    <>
      <div>관리자 로그인</div>

      <form onSubmit={login} className="flex flex-col w-1/4 gap-3">
        <input
          type="password"
          name="password"
          placeholder="패스워드 입력"
          className="border-2 border-black"
        />
        <input type="submit" value="로그인" />
      </form>
    </>
  );
}
